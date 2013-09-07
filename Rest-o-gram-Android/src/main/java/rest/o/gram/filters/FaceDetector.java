package rest.o.gram.filters;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 8/26/13
 */
public interface FaceDetector extends Cloneable {
    /**
     * Initializes this face detector using given application context
     */
    void initialize(Context context);

    /**
     * @param bitmap given bitmap to process
     * @return does the given bitmap contain any faces?
     */
    boolean hasFaces(Bitmap bitmap);

    /**
     * Disposes all resources
     */
    void dispose();

    /**
     * Returns a clone of this face detector
     */
    FaceDetector clone() throws CloneNotSupportedException;
}
