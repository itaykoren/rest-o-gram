package rest.o.gram.commands;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 06/05/13
 */
public class DownloadImageCommand extends AbstractRestogramCommand {

    public DownloadImageCommand(String url, ImageView imageView) {
        this.url = url;
        this.imageView = imageView;
        this.width = -1;
        this.height = -1;
    }

    public DownloadImageCommand(String url, ImageView imageView, int width, int height) {
        this(url, imageView);
        this.width = width;
        this.height = height;
    }

    @Override
    public void execute() {
        super.execute();
        fetchDrawableOnThread(url, imageView);
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
                imageView.setImageDrawable((Drawable) message.obj);
                notifyFinished();
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

    private InputStream fetch(String urlString) throws MalformedURLException, IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet request = new HttpGet(urlString);
        HttpResponse response = httpClient.execute(request);
        return response.getEntity().getContent();
    }

    private String url;
    private ImageView imageView;
    private int width;
    private int height;
}
