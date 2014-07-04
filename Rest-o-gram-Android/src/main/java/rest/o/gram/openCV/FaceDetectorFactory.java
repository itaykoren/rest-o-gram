package rest.o.gram.openCV;

import rest.o.gram.common.Defs;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 6/25/14
 */
public interface FaceDetectorFactory {

    /**
     *  Returns a face detector according to given type.
     * @param type the bitmap filter type to create
     */
    void create(Defs.Filtering.BitmapFilterType type);

    /**
     * @return Returns the face detector init callback
     */
    FaceDetectorInitCallback getCallback ();

    /**
     * Sets a new face detector init callback
     * @param callback the new face detector init callback
     */
    void setCallback(FaceDetectorInitCallback callback);
}
