package rest.o.gram.filters;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.FaceDetector;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 5/21/13
 */
public class FaceBitmapFilter implements IBitmapFilter {

    /**
     * Ctor
     */
    public FaceBitmapFilter(int maxFaces) {
        // Set max faces
        this.maxFaces = maxFaces;
    }

    @Override
    public boolean accept(Bitmap bitmap) {
        try {
            Bitmap b = createBitmap(bitmap);
            FaceDetector.Face[] faces = new FaceDetector.Face[maxFaces];

            // Initialize face detector
            FaceDetector detector = new FaceDetector(b.getWidth(), b.getHeight(), maxFaces);
            int amount = detector.findFaces(b, faces);
            return amount == 0;
        }
        catch(Exception e) {
            // TODO: report error
        }

        return true;
    }

    /**
     * Creates a new 565 RGB bitmap from given source bitmap
     */
    private Bitmap createBitmap(Bitmap source) {
        return source.copy(Bitmap.Config.RGB_565, false);
    }

    private int maxFaces;
}
