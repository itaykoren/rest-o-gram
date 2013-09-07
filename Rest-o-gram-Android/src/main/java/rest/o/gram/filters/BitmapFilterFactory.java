package rest.o.gram.filters;

import android.content.Context;
import android.util.Log;
import org.opencv.android.OpenCVLoader;
import rest.o.gram.common.Defs;
import rest.o.gram.common.Utils;
import rest.o.gram.openCV.JavaCVFaceDetector;
import rest.o.gram.openCV.OpenCVFaceDetector;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 8/22/13
 */
public class BitmapFilterFactory implements IBitmapFilterFactory {
    /**
     * Ctor
     */
    public BitmapFilterFactory(Context context) {
        this.context = context;
    }

    @Override
    public IBitmapFilter create(final Defs.Filtering.BitmapFilterType type) {
        // If hardware is not capable of filtering - falls back to default filter
        if(!Utils.canApplyBitmapFilter())
            return new DefaultBitmapFilter();

        switch(type) {
            case DoNothingBitmapFilter:
                return new DefaultBitmapFilter();
            case AndroidFaceBitmapFilter:
                return new AndroidFaceBitmapFilter(Defs.Filtering.AndroidDetector.MAX_FACES_TO_DETECT);
            case JavaCVFaceBitmapFilter:
            case OpenCVFaceBitmapFilter:
            {
                if(!OpenCVLoader.initDebug()) {
                    Log.e("REST-O-GRAM", "error while loading openCV");
                    return new DefaultBitmapFilter();
                }
                else {
                    OpenCvBitmapFilter filter = new OpenCvBitmapFilter();
                    FaceDetector detector = createFaceDetector(type);
                    detector.initialize(context);
                    filter.setFaceDetector(detector);
                    return filter;
                }
            }
            default:
                return null;
        }
    }

    private FaceDetector createFaceDetector(final Defs.Filtering.BitmapFilterType type) {
        if(type == Defs.Filtering.BitmapFilterType.OpenCVFaceBitmapFilter) {
            System.loadLibrary("face_detector");
            return new OpenCVFaceDetector();
        }
        else if(type == Defs.Filtering.BitmapFilterType.JavaCVFaceBitmapFilter) {
            return new JavaCVFaceDetector();
        }
        else {
            return null;
        }
    }

    private Context context;
}
