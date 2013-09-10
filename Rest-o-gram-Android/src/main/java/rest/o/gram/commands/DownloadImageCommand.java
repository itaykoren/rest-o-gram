package rest.o.gram.commands;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import rest.o.gram.cache.IBitmapCache;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.common.Defs;
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.filters.IBitmapFilter;
import rest.o.gram.network.INetworkStateProvider;
import rest.o.gram.tasks.DownloadImageObserver;
import rest.o.gram.tasks.DownloadImageStrategy;
import rest.o.gram.tasks.DownloadImageTask;
import rest.o.gram.view.IPhotoViewAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executor;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 06/05/13
 */
public class DownloadImageCommand extends AbstractRestogramCommand {

    public DownloadImageCommand(String url, RestogramPhoto photo, IPhotoViewAdapter viewAdapter,
                                int width, int height) {
        this.url = url;

        ViewAdapterOption option = new ViewAdapterOption(photo.getInstagram_id(), photo, viewAdapter, width, height);
        strategy = option;
        observer = option;

        executor = RestogramClient.getInstance().getExecutor();
    }

    public DownloadImageCommand(String url, String photoId, ImageView imageView,
                                int width, int height) {
        this.url = url;

        ImageViewOption option = new ImageViewOption(photoId, imageView, width, height);
        strategy = option;
        observer = option;

        executor = RestogramClient.getInstance().getExecutor();
    }

    @Override
    public boolean execute() {
        if(!super.execute())
            return false;

        DownloadImageTask t = new DownloadImageTask(strategy, observer);
        t.executeOnExecutor(executor, url);
        task = t;
        return true;
    }

    @Override
    public boolean cancel() {
        if(!super.cancel())
            return false;

        task.cancel(true);
        return true;
    }

    protected AsyncTask task;

    private InputStream fetch(String urlString) throws IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet request = new HttpGet(urlString);
        HttpResponse response = httpClient.execute(request);
        return response.getEntity().getContent();
    }

    private String generateFilename(String urlString, String photoId) {
        return urlString.replaceAll("[^A-Za-z0-9]", "_");
    }

    private Bitmap decodeBitmap(String urlString, int reqWidth, int reqHeight) {
        try {
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            InputStream is = fetch(urlString);
            BitmapFactory.decodeStream(is, null, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap (new stream) with inSampleSize set
            options.inJustDecodeBounds = false;
            is = fetch(urlString);
            return BitmapFactory.decodeStream(is, null, options);
        }
        catch(Exception | Error e) {
            return null;
        }
    }

    private Bitmap decodeBitmap(String urlString, final Bitmap source, int reqWidth, int reqHeight) {
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.outHeight = source.getHeight();
            options.outWidth = source.getWidth();

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap (new stream) with inSampleSize set
            options.inJustDecodeBounds = false;
            InputStream is = fetch(urlString);
            return BitmapFactory.decodeStream(is, null, options);
        }
        catch(Exception | Error e) {
            return null;
        }
    }

    private Bitmap decodeBitmap(String urlString) {
        try {
            InputStream is = fetch(urlString);
            return BitmapFactory.decodeStream(is);
        }
        catch(Exception | Error e) {
            return null;
        }
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if(height > reqHeight || width > reqWidth) {
            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    private Executor executor;
    private String url;
    private DownloadImageStrategy strategy;
    private DownloadImageObserver observer;

    /**
     * Used when downloading images for view adapters
     */
    private class ViewAdapterOption implements DownloadImageStrategy, DownloadImageObserver {
        private ViewAdapterOption(String photoId, RestogramPhoto photo, IPhotoViewAdapter viewAdapter, int width, int height) {
            this.photoId = photoId;
            this.photo = photo;
            this.viewAdapter = viewAdapter;
            this.width = width;
            this.height = height;
        }

        @Override
        public Bitmap download(final String url) {
            // Try loading image from cache
            IBitmapCache cache = RestogramClient.getInstance().getBitmapCache();
            String filename = generateFilename(url, photoId);
            Bitmap bitmap = cache.load(filename);

            if(bitmap == null) {
                final boolean filter = !photo.isApproved();
                if(filter) {
                    // Get network state provider
                    final INetworkStateProvider networkStateProvider = RestogramClient.getInstance().getNetworkStateProvider();

                    // Get bitmap filter
                    final IBitmapFilter bitmapFilter = RestogramClient.getInstance().getBitmapFilter();

                    if(networkStateProvider.isWifi() &&
                       bitmapFilter.requiredQuality() == Defs.Filtering.BitmapQuality.HighResolution) {
                        // Download full scale bitmap
                        bitmap = decodeBitmap(url);

                        // Apply filter to bitmap
                        if(!bitmapFilter.accept(bitmap)) {
                            return null;
                        }

                        // Download scaled bitmap using existing
                        bitmap = decodeBitmap(url, bitmap, width, height);
                    }
                    else {
                        // Download scaled bitmap
                        bitmap = decodeBitmap(url, width, height);

                        // Apply filter to bitmap
                        if(!bitmapFilter.accept(bitmap)) {
                            return null;
                        }
                    }
                }
                else {
                    // Download scaled bitmap
                    bitmap = decodeBitmap(url, width, height);
                }

                // Save scaled bitmap to cache
                cache.save(filename, bitmap);
            }

            return bitmap;
        }

        @Override
        public void onDownloaded(final String url, final Bitmap bitmap) {
            if(bitmap == null) {
                notifyError();
                return;
            }

            // Get bitmapId
            String bitmapId = generateFilename(url, photoId);

            // Add photo to view adapter
            viewAdapter.addPhoto(photoId, bitmapId);
            viewAdapter.refresh();

            notifyFinished();
        }

        @Override
        public void onError(final String url) {
            notifyError();
        }

        @Override
        public void onCanceled(final String url) {
            notifyCanceled();
        }

        private String photoId;
        private RestogramPhoto photo;
        private IPhotoViewAdapter viewAdapter;
        private int width;
        private int height;
    }

    /**
     * Used when downloading images for image views
     */
    private class ImageViewOption implements DownloadImageStrategy, DownloadImageObserver {
        private ImageViewOption(String photoId, ImageView imageView, int width, int height) {
            this.photoId = photoId;
            this.imageView = imageView;
            this.width = width;
            this.height = height;
        }

        @Override
        public Bitmap download(final String url) {
            // Try loading image from cache
            IBitmapCache cache = RestogramClient.getInstance().getBitmapCache();
            String filename = generateFilename(url, photoId);
            Bitmap bitmap = cache.load(filename);

            if(bitmap == null) {
                // Download scaled bitmap
                bitmap = decodeBitmap(url, width, height);

                // Save scaled bitmap to cache
                cache.save(filename, bitmap);
            }

            return bitmap;
        }

        @Override
        public void onDownloaded(final String url, final Bitmap bitmap) {
            if(bitmap == null) {
                notifyError();
                return;
            }

            if (imageView != null)
                imageView.setImageBitmap(bitmap);
            notifyFinished();
        }

        @Override
        public void onError(final String url) {
            notifyError();
        }

        @Override
        public void onCanceled(final String url) {
            notifyCanceled();
        }

        private String photoId;
        private ImageView imageView;
        private int width;
        private int height;
    }
}
