package rest.o.gram.filters;

import android.content.Context;
import android.util.Log;
import rest.o.gram.common.Defs;
import rest.o.gram.common.Utils;
import rest.o.gram.openCV.*;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 8/22/13
 */
public class BitmapFilterFactory implements IBitmapFilterFactory {

    @Override
    public void create(final Defs.Filtering.BitmapFilterType type) {

        // If hardware is not capable of filtering - falls back to default filter
        if(!Utils.canApplyBitmapFilter() && callback != null)
            callback.onBitmapFilterInit(new DefaultBitmapFilter());

        switch(type) {
            case DoNothingBitmapFilter: {
                    if (callback != null)
                        callback.onBitmapFilterInit(new DefaultBitmapFilter());
            } break;
            case AndroidFaceBitmapFilter: {
                    if (callback != null)
                        callback.onBitmapFilterInit(new AndroidFaceBitmapFilter(Defs.Filtering.AndroidDetector.MAX_FACES_TO_DETECT));
            } break;
            case JavaCVFaceBitmapFilter:
            case OpenCVFaceBitmapFilter:
            {
                faceDetectorFactory.create(type);
            } break;
            default: {
                if (callback != null)
                    callback.onBitmapFilterInit(new DefaultBitmapFilter());
            }
        }
    }

    @Override
    public BitmapFilterInitCallback getCallback() {
        return callback;
    }

    @Override
    public void setCallback(final BitmapFilterInitCallback callback) {
        this.callback = callback;
    }

    @Override
    public FaceDetectorFactory getFaceDetectorFactory() {
        return faceDetectorFactory;
    }

    @Override
    public void setFaceDetectorFactory(final FaceDetectorFactory faceDetectorFactory) {
        this.faceDetectorFactory = faceDetectorFactory;
        faceDetectorFactory.setCallback(new FaceDetectorInitCallback() {
            @Override
            public void onFaceDetectorInit(final FaceDetector detector) {
                if (detector != null)
                    createOpenCVBitmapFilter(detector);
                else {
                    Log.w("REST-O-GRAM", "cannot init face detector");
                    callback.onBitmapFilterInit(null);
                }
            }
        });
    }

    private void createOpenCVBitmapFilter(final FaceDetector faceDetector) {
        final OpenCvBitmapFilter filter = new OpenCvBitmapFilter();
        filter.setFaceDetector(faceDetector);
        if (callback != null)
            callback.onBitmapFilterInit(filter);
    }

    private BitmapFilterInitCallback callback;
    private FaceDetectorFactory faceDetectorFactory;
}
