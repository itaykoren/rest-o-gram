package rest.o.gram.commands;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

    public DownloadImageCommand(Context context, String url, String photoId, IPhotoViewAdapter viewAdapter) {
        this.context = context;
        this.url = url;
        this.photoId = photoId;
        this.viewAdapter = viewAdapter;
    }

    public DownloadImageCommand(Context context, String url, String photoId, ImageView imageView) {
        this.context = context;
        this.url = url;
        this.photoId = photoId;
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

    public Drawable fetchDrawable(String urlString, String photoId) {
        try {
            IBitmapCache cache = RestogramClient.getInstance().getBitmapCache();
            Bitmap bitmap = cache.load(photoId);

            if(bitmap == null) {
                // Download image
                InputStream is = fetch(urlString);
                Drawable drawable = Drawable.createFromStream(is, "src");

                // Save bitmap to cache
                bitmap = ((BitmapDrawable)drawable).getBitmap();
                cache.save(photoId, bitmap);
                return drawable;
            }
            else {
                return new BitmapDrawable(context.getResources(), bitmap);
            }
        }
        catch (OutOfMemoryError e) {
            return null;
        }
        catch (Exception e) {
            return null;
        }
    }

    public void fetchDrawableOnThread(final String urlString, final String photoId, final ImageView imageView) {

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                try {
                    if(isCanceled) {
                        notifyCanceled();
                        return;
                    }

                    Drawable drawable = (Drawable)message.obj;
                    imageView.setImageDrawable(drawable);

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
                Drawable drawable = fetchDrawable(urlString, photoId);
                Message message = handler.obtainMessage(1, drawable);
                handler.sendMessage(message);
            }
        };
        thread.start();
    }

    public void fetchDrawableOnThread(final String urlString, final String photoId, final IPhotoViewAdapter viewAdapter) {

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                try {
                    if(isCanceled) {
                        notifyCanceled();
                        return;
                    }

                    Drawable drawable = (Drawable)message.obj;

                    Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
                    final IBitmapFilter filter = RestogramClient.getInstance().getBitmapFilter();

                    // Apply filter to bitmap
                    if(!filter.accept(bitmap)) {
                        notifyFinished();
                        return;
                    }

                    // Add photo to view adapter
                    viewAdapter.addPhoto(photoId, bitmap);
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
                Drawable drawable = fetchDrawable(urlString, photoId);
                Message message = handler.obtainMessage(1, drawable);
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

    private Context context;
    private String url;
    private ImageView imageView;
    private String photoId;
    private IPhotoViewAdapter viewAdapter;
    private boolean isCanceled = false;
}
