package rest.o.gram.filters;

import android.graphics.Bitmap;
import rest.o.gram.common.Defs;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 5/21/13
 */
public class DefaultBitmapFilter implements IBitmapFilter {
    @Override
    public Defs.Filtering.BitmapQuality requiredQuality() {
        return Defs.Filtering.BitmapQuality.LowResolution;
    }

    @Override
    public boolean accept(final Bitmap bitmap) {
        // Accept all bitmaps
        return true;
    }

    @Override
    public void dispose() {
        // Empty
    }

    @Override
    public void setFaceDetector(FaceDetector faceDetector) {
        // Empty
    }
}
