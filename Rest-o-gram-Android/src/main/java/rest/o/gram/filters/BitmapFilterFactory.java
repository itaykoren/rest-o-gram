package rest.o.gram.filters;

import android.util.Log;
import rest.o.gram.common.Defs;
import rest.o.gram.common.Utils;
import rest.o.gram.openCV.FaceDetector;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 8/22/13
 */
public class BitmapFilterFactory implements IBitmapFilterFactory {
    @Override
    public IBitmapFilter create(final Defs.Filtering.BitmapFilterType type, final FaceDetector faceDetector) {
        // if hardware is not capable of filtering - falls back to default filter..
        if (!Utils.canApplyBitmapFilter())
            return new DefaultBitmapFilter();

        switch(type) {
            case DoNothingBitmapFilter:
                return new DefaultBitmapFilter();
            case AndroidFaceBitmapFilter:
                return new AndroidFaceBitmapFilter(Defs.Filtering.AndroidDetector.MAX_FACES_TO_DETECT);
            case JavaCVFaceBitmapFilter:
            case OpenCVFaceBitmapFilter:
            {
                if (faceDetector == null)
                {
                    Log.w("REST-O-GRAM", "bitmap filter is defined as OpenCV, yet no cascade classifier has been specified");
                    return null;
                }
                return new OpenCvBitmapFilter(faceDetector);
            }
            default:
                return null;
        }
    }
}
