package rest.o.gram.filters;

import android.graphics.Bitmap;
import rest.o.gram.common.Defs.Filtering.BitmapQuality;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 5/21/13
 */
public interface IBitmapFilter {
    /**
     * Returns the required bitmap quality
     */
    BitmapQuality requiredQuality();

    /**
     * Returns true whether this bitmap is accepted by this filter
     */
    boolean accept(final Bitmap bitmap);

    /**
     * Disposes all resources
     */
    void dispose();

    /**
     * Sets face detector
     */
    void setFaceDetector(FaceDetector faceDetector);
}
