package rest.o.gram.tasks;

import android.graphics.Bitmap;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 9/7/13
 */
public interface DownloadImageStrategy {
    /**
     * Downloads bitmap from given url
     */
    Bitmap download(final String url);
}
