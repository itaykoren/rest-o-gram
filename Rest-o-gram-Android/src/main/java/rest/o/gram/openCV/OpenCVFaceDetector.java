package rest.o.gram.openCV;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.common.Defs;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 8/26/13
 */
public class OpenCVFaceDetector extends FaceDetectorBase {
    public OpenCVFaceDetector(Context context) {
        super(context);
    }

    /**
     * @param bitmap given bitmap to process
     * @return does the given bitmap contain any faces?
     */
    @Override
    public boolean hasFaces(final Bitmap bitmap) {
        if (RestogramClient.getInstance().isDebuggable())
            Log.d("REST-O-GRAM", "openCV bitmap filter used");
        final double maxFaceSize = Math.min(bitmap.getWidth(), bitmap.getHeight());
        final long actualMaxFaceSize = (long)(maxFaceSize * Defs.Filtering.OpenCVDetector.MAX_FACE_SIZE_FACTOR);
        final Mat pixelMatrix = createPixelMatrix(bitmap);
        final MatOfRect faces = new MatOfRect();
        nativeDetectFaces(pixelMatrix.getNativeObjAddr(), faces.getNativeObjAddr(),
                          (long)Defs.Filtering.OpenCVDetector.MIN_FACE_SIZE, actualMaxFaceSize,
                           RestogramClient.getInstance().isDebuggable());
        final boolean hasFaces = !faces.empty();
        if (RestogramClient.getInstance().isDebuggable())
            Log.d("REST-O-GRAM", "is bitmap approved? " + !hasFaces);

        // TODO: remove
        if (!hasFaces)
            ++inCount;
        else
            ++outCount;
        if (RestogramClient.getInstance().isDebuggable())
        {
            Log.d("REST-O-GRAM", "bitmap processed: " + (!hasFaces ? "in" : "out"));
            Log.d("REST-O-GRAM", "total - in:" + inCount + " out:" + outCount);
        }
        faces.release();
        return hasFaces;
    }

    @Override
    public void dispose() {
    }

    @Override
    protected void initOpenCVClassifier(final File cascadeFile) {
        final String classifierPath =  cascadeFile.getAbsolutePath();
        if (RestogramClient.getInstance().isDebuggable())
            Log.d("REST-O-GRAM", "path to classifier: " + classifierPath);
        nativeLoadClassifier(classifierPath, (long)Defs.Filtering.OpenCVDetector.MIN_FACE_SIZE,
                             RestogramClient.getInstance().isDebuggable());
    }

    private int inCount = 0;
    private  int outCount = 0;

    private static native void nativeLoadClassifier(String cascadeName, long minFaceSize, boolean debug);
    private static native void nativeDetectFaces(long inputImage, long faces, long minSize, long maxSize, boolean debug);
}
