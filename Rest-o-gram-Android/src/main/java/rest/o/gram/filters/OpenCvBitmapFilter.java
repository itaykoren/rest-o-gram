package rest.o.gram.filters;

import android.graphics.Bitmap;
import rest.o.gram.common.Defs;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 8/26/13
 */
public class OpenCvBitmapFilter implements IBitmapFilter {
    @Override
    public Defs.Filtering.BitmapQuality requiredQuality() {
        return isInitialized ?
                Defs.Filtering.BitmapQuality.HighResolution : Defs.Filtering.BitmapQuality.LowResolution;
    }

    @Override
    public boolean accept(final Bitmap bitmap) {
        if(!isInitialized)
            return true;

        final FaceDetector detector = faceDetectors.get();
        if(detector == null)
            return true;

        return !detector.hasFaces(bitmap);
    }

    @Override
    public void dispose() {
        if(faceDetector != null)
            faceDetector.dispose();
    }

    @Override
    public void setFaceDetector(FaceDetector faceDetector) {
        this.faceDetector = faceDetector;
        isInitialized = true;
    }

    private boolean isInitialized = false;
    private FaceDetector faceDetector = null;
    private ThreadLocal<FaceDetector> faceDetectors = new ThreadLocal<FaceDetector>() {
        @Override
        protected FaceDetector initialValue() {
            if(!isInitialized)
                return null;

            try {
                return faceDetector.clone();
            }
            catch(Exception e) {
                return null;
            }
        }
    };
}
