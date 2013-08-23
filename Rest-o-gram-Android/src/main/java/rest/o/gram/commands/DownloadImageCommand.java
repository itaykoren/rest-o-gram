package rest.o.gram.commands;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import rest.o.gram.cache.IBitmapCache;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.filters.IBitmapFilter;
import rest.o.gram.view.IPhotoViewAdapter;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 06/05/13
 */
public class DownloadImageCommand extends AbstractRestogramCommand {

    public DownloadImageCommand(Context context, String url,
                                String photoId, IPhotoViewAdapter viewAdapter,
                                int width, int height) {
        this.context = context;
        this.url = url;
        this.photoId = photoId;
        this.width = width;
        this.height = height;
        this.viewAdapter = viewAdapter;
    }

    public DownloadImageCommand(Context context, String url,
                                String photoId, ImageView imageView,
                                int width, int height) {
        this.context = context;
        this.url = url;
        this.photoId = photoId;
        this.width = width;
        this.height = height;
        this.imageView = imageView;
    }

    @Override
    public boolean execute() {
        if(!super.execute())
            return false;

        if(imageView != null) {
            fetchDrawableOnThread(url, photoId, imageView);
            return true;
        }

        if(viewAdapter != null) {
            fetchDrawableOnThread(url, photoId, viewAdapter);
            return true;
        }

        return false;
    }

    @Override
    public boolean cancel() {
        if(!super.cancel())
            return false;

        // Cancel
        isCanceled = true;
        return true;
    }

    private Bitmap fetchDrawable(String urlString, String photoId, int reqWidth, int reqHeight, boolean filter) {
        try {
            IBitmapCache cache = RestogramClient.getInstance().getBitmapCache();
            String filename = generateFilename(urlString, photoId);
            Bitmap bitmap = cache.load(filename);

            if(bitmap == null) {
                if(filter) {
                    // Download full scale bitmap
                    bitmap = decodeBitmap(urlString);

                    // Get bitmap filter
                    final IBitmapFilter bitmapFilter = RestogramClient.getInstance().getBitmapFilter();

                    // Apply filter to bitmap
                    if(!bitmapFilter.accept(bitmap)) {
                        return null;
                    }

                    // Download scaled bitmap
                    bitmap = decodeBitmap(urlString, bitmap, reqWidth, reqHeight);
                }
                else {
                    // Download scaled bitmap
                    bitmap = decodeBitmap(urlString, reqWidth, reqHeight);
                }

                // Save scaled bitmap to cache
                cache.save(filename, bitmap);
            }

            return bitmap;
        }
        catch (OutOfMemoryError e) {
            return null;
        }
        catch (Exception e) {
            return null;
        }
    }

    private void fetchDrawableOnThread(final String urlString, final String photoId, final ImageView imageView) {

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                try {
                    if(isCanceled) {
                        notifyCanceled();
                        return;
                    }

                    final Bitmap bitmap = (Bitmap)message.obj;
                    if(bitmap == null) {
                        notifyError();
                        return;
                    }

                    imageView.setImageBitmap(bitmap);
                    notifyFinished();
                }
                catch(Exception e) {
                    notifyError();
                }
            }
        };

        Thread thread = new Thread() {
            @Override
            public void run() {
                Bitmap bitmap = fetchDrawable(urlString, photoId, width, height, false);
                Message message = handler.obtainMessage(1, bitmap);
                handler.sendMessage(message);
            }
        };
        thread.start();
    }

    private void fetchDrawableOnThread(final String urlString, final String photoId, final IPhotoViewAdapter viewAdapter) {

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                try {
                    if(isCanceled) {
                        notifyCanceled();
                        return;
                    }

                    final Bitmap bitmap = (Bitmap)message.obj;
                    if(bitmap == null) {
                        notifyError();
                        return;
                    }

                    // Get bitmapId
                    String bitmapId = generateFilename(urlString, photoId);

                    // Add photo to view adapter
                    viewAdapter.addPhoto(photoId, bitmapId);
                    viewAdapter.refresh();

                    notifyFinished();
                }
                catch(Exception e) {
                    notifyError();
                }
            }
        };

        Thread thread = new Thread() {
            @Override
            public void run() {
                Bitmap bitmap = fetchDrawable(urlString, photoId, width, height, true);
                Message message = handler.obtainMessage(1, bitmap);
                handler.sendMessage(message);
            }
        };
        thread.start();
    }

    private InputStream fetch(String urlString) throws IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet request = new HttpGet(urlString);
        HttpResponse response = httpClient.execute(request);
        return response.getEntity().getContent();
    }

    private String generateFilename(String urlString, String photoId) {
        return urlString.replaceAll("[^A-Za-z0-9]", "_");
    }

    private Bitmap decodeBitmap(String urlString, int reqWidth, int reqHeight) throws IOException {
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

    private Bitmap decodeBitmap(String urlString, final Bitmap source, int reqWidth, int reqHeight) throws IOException {
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

    private Bitmap decodeBitmap(String urlString) throws IOException {
        InputStream is = fetch(urlString);
        return BitmapFactory.decodeStream(is);
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

    private Context context;
    private String url;
    private ImageView imageView;
    private String photoId;
    private int width;
    private int height;
    private IPhotoViewAdapter viewAdapter;
    private boolean isCanceled = false;
}
