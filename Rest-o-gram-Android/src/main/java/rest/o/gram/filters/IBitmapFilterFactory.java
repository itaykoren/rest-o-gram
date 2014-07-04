package rest.o.gram.filters;

import rest.o.gram.common.Defs;
import rest.o.gram.openCV.FaceDetectorFactory;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 8/22/13
 */
public interface IBitmapFilterFactory {
    /**
     * Returns bitmap filter according to given type.
     * If for any reason(such as lack of sufficient hardware capabilities
     * or failure to load required libraries), the defined filter cannot be used -
     * falls back to using {@link rest.o.gram.common.Defs.Filtering.BitmapFilterType#DoNothingBitmapFilter}.
     * @param type type of filter to create
     */
    void create(Defs.Filtering.BitmapFilterType type);

    /**
     * @return The defined bitmap filter init callback.
     */
    BitmapFilterInitCallback getCallback();

    /**
     * Sets a new bitmap filter init callback
     * @param callback the new bitmap filter init callback
     */
    void setCallback(BitmapFilterInitCallback callback);

    /**
     * @return The defined face detector factory
     */
    FaceDetectorFactory getFaceDetectorFactory();

    /**
     * Sets a new face detector factory
     * @param faceDetectorFactory the new face detector factory
     */
    void setFaceDetectorFactory(FaceDetectorFactory faceDetectorFactory);
}
