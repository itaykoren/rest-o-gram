package rest.o.gram.tasks;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 9/7/13
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    /**
     * Ctor
     */
    public DownloadImageTask(DownloadImageStrategy strategy, DownloadImageObserver observer) {
        this.strategy = strategy;
        this.observer = observer;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        try {
            url = params[0];
            return strategy.download(url);
        }
        catch(Exception | Error e) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    observer.onError(url);
                }
            });
            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        observer.onDownloaded(url, bitmap);
    }

    @Override
    protected void onCancelled() {
        observer.onCanceled(url);
    }

    private DownloadImageStrategy strategy;
    private DownloadImageObserver observer;
    private String url;
}
