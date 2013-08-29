package rest.o.gram.filters;

import rest.o.gram.common.Defs;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 8/22/13
 */
public interface IBitmapFilterFactory {
    /**
     * Returns bitmap filter according to given type
     */
    IBitmapFilter create(Defs.Filtering.BitmapFilterType type, FaceDetector faceDetector);
}
