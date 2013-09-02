package rest.o.gram.filters;

import android.graphics.Bitmap;
import android.media.FaceDetector;
import android.util.Log;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.common.Defs;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 5/21/13
 */
public class AndroidFaceBitmapFilter implements IBitmapFilter {

    /**
     * Ctor
     */
    public AndroidFaceBitmapFilter(int maxFaces) {
        // Set max faces
        this.maxFaces = maxFaces;
    }

    @Override
    public Defs.Filtering.BitmapQuality requiredQuality() {
        return Defs.Filtering.BitmapQuality.HighResolution;
    }

    @Override
    public boolean accept(final Bitmap bitmap) {
        try {
            Bitmap b = createBitmap(bitmap);
            FaceDetector.Face[] faces = new FaceDetector.Face[maxFaces];

            // Initialize face detector
            final FaceDetector detector = new FaceDetector(b.getWidth(), b.getHeight(), maxFaces);
            int amount = detector.findFaces(b, faces);
            b.recycle();
            if (amount == 0)
                ++inCount;
            else
                ++outCount;
            if (RestogramClient.getInstance().isDebuggable())
            {
                Log.d("REST-O-GRAM", "bitmap processed: " + (amount == 0 ? "in" : "out"));
                Log.d("REST-O-GRAM", "total - in:" + inCount + " out:" + outCount);
            }
            return amount == 0;
        }
        catch(Exception e) {
            // TODO: report error
        }

        return true;
    }

    @Override
    public void dispose() {
        // Empty
    }

    @Override
    public void setFaceDetector(rest.o.gram.filters.FaceDetector faceDetector) {
        // Empty
    }

    /**
     * Creates a new 565 RGB bitmap from given source bitmap
     */
    private Bitmap createBitmap(Bitmap source) {
        return source.copy(Bitmap.Config.RGB_565, false);
    }

    private int maxFaces;
    private static int inCount = 0;
    private static  int outCount = 0;
}
