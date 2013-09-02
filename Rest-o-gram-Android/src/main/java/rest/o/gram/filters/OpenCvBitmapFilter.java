package rest.o.gram.filters;

import android.graphics.Bitmap;
import rest.o.gram.common.Defs;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 8/26/13
 */
public class OpenCvBitmapFilter implements IBitmapFilter {
    public OpenCvBitmapFilter() {
    }

    @Override
    public Defs.Filtering.BitmapQuality requiredQuality() {
        return Defs.Filtering.BitmapQuality.HighResolution;
    }

    @Override
    public boolean accept(final Bitmap bitmap) {
        if(faceDetector == null)
            return true;

        return !faceDetector.hasFaces(bitmap);
    }

    @Override
    public void dispose() {
        if(faceDetector != null)
            faceDetector.dispose();
    }

    @Override
    public void setFaceDetector(FaceDetector faceDetector) {
        this.faceDetector = faceDetector;
    }

    private FaceDetector faceDetector;
}
