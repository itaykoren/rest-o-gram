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
import rest.o.gram.view.IViewAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 06/05/13
 */
public class DownloadImageCommand extends AbstractRestogramCommand {

    public DownloadImageCommand(String url, ImageView imageView, IViewAdapter viewAdapter) {
        this.url = url;
        this.imageView = imageView;
        this.viewAdapter = viewAdapter;
    }

    public DownloadImageCommand(String url, ImageView imageView) {
        this.url = url;
        this.imageView = imageView;
    }

    @Override
    public void execute() {
        super.execute();
        fetchDrawableOnThread(url, imageView, viewAdapter);
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

    public void fetchDrawableOnThread(final String urlString, final ImageView imageView, final IViewAdapter viewAdapter) {

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                try {
                    Drawable drawable = (Drawable)message.obj;
                    Bitmap bitmap;

                    if(viewAdapter != null) {
                        if(viewAdapter.width() > 0 && viewAdapter.height() > 0) {
                            bitmap = ((BitmapDrawable)drawable).getBitmap();
                            bitmap = resizeBitmap(bitmap, viewAdapter.width(), viewAdapter.height());
                            imageView.setImageBitmap(bitmap);
                        }

                        viewAdapter.addView(imageView);
                        viewAdapter.refresh();
                    }
                    else {
                        imageView.setImageDrawable(drawable);
                    }

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
    private IViewAdapter viewAdapter;
}
