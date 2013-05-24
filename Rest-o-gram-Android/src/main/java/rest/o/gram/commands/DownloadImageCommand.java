package rest.o.gram.commands;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.filters.IBitmapFilter;
import rest.o.gram.view.IPhotoViewAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 06/05/13
 */
public class DownloadImageCommand extends AbstractRestogramCommand {

    public DownloadImageCommand(String url, RestogramPhoto photo, IPhotoViewAdapter viewAdapter) {
        this.url = url;
        this.photo = photo;
        this.viewAdapter = viewAdapter;
    }

    public DownloadImageCommand(String url, ImageView imageView) {
        this.url = url;
        this.imageView = imageView;
    }

    @Override
    public boolean execute() {
        if(!super.execute())
            return false;

        if(imageView != null) {
            fetchDrawableOnThread(url, imageView);
            return true;
        }

        if(photo != null && viewAdapter != null) {
            fetchDrawableOnThread(url, photo, viewAdapter);
            return true;
        }

        return false;
    }

    @Override
    public boolean cancel() {
        if(!super.cancel())
            return false;

        // Cancel
        isCancled = true;
        return true;
    }

    public Drawable fetchDrawable(String urlString) {
        try {
            InputStream is = fetch(urlString);
            return Drawable.createFromStream(is, "src");
        } catch (MalformedURLException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    public void fetchDrawableOnThread(final String urlString, final ImageView imageView) {

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                try {
                    if(isCancled) {
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
                Drawable drawable = fetchDrawable(urlString);
                Message message = handler.obtainMessage(1, drawable);
                handler.sendMessage(message);
            }
        };
        thread.start();
    }

    public void fetchDrawableOnThread(final String urlString, final RestogramPhoto photo, final IPhotoViewAdapter viewAdapter) {

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                try {
                    if(isCancled) {
                        notifyCanceled();
                        return;
                    }

                    Drawable drawable = (Drawable)message.obj;

                    if(viewAdapter.width() <= 0 && viewAdapter.height() <= 0) {
                        notifyError();
                        return;
                    }

                    Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
                    final IBitmapFilter filter = RestogramClient.getInstance().getBitmapFilter();

                    // Apply filter to bitmap
                    if(!filter.accept(bitmap)) {
                        notifyFinished();
                        return;
                    }

                    bitmap = resizeBitmap(bitmap, viewAdapter.width(), viewAdapter.height());

                    // Add photo to view adapter
                    viewAdapter.addPhoto(photo, bitmap);
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
                Drawable drawable = fetchDrawable(urlString);
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

    private Bitmap resizeBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // Resize bitmap
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        // Create new bitmap
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
    }

    private String url;
    private ImageView imageView;
    private RestogramPhoto photo;
    private IPhotoViewAdapter viewAdapter;
    private boolean isCancled = false;
}
