package rest.o.gram.openCV;

import android.graphics.Bitmap;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 8/26/13
 */
public interface FaceDetector {
    /**
     * @param bitmap given bitmap to process
     * @return does the given bitmap contain any faces?
     */
    boolean hasFaces(Bitmap bitmap);

    /**
     * Disposes all resources
     */
    void dispose();
}
