package rest.o.gram.tasks;

import android.graphics.Bitmap;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 9/7/13
 */
public interface DownloadImageObserver {
    /**
     * Called after bitmap was successfully downloaded
     */
    void onDownloaded(final String url, final Bitmap bitmap);

    /**
     * Called after download has failed
     */
    void onError(final String url);

    /**
     * Called after download was canceled
     */
    void onCanceled(final String url);
}
