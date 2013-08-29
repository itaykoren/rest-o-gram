package rest.o.gram.filters;

import android.graphics.Bitmap;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 8/26/13
 */
public class OpenCvBitmapFilter implements IBitmapFilter {
    public OpenCvBitmapFilter(FaceDetector faceDetector) {
        this.faceDetector = faceDetector;
    }

    @Override
    public boolean accept(final Bitmap bitmap) {
        return !faceDetector.hasFaces(bitmap);
    }

    @Override
    public void dispose() {
        faceDetector.dispose();
    }

    private FaceDetector faceDetector;
}
