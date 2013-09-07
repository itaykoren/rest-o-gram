package rest.o.gram.openCV;

import android.graphics.Bitmap;
import android.util.Log;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.common.Defs;
import rest.o.gram.filters.FaceDetector;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 8/26/13
 */
public class OpenCVFaceDetector extends FaceDetectorBase {
    /**
     * Default ctor
     */
    public OpenCVFaceDetector() {}

    /**
     * "Copy ctor"
     */
    public OpenCVFaceDetector(final File cascadeFile) {
        initOpenCVClassifier(cascadeFile);
    }

    @Override
    public boolean hasFaces(final Bitmap bitmap) {
        try {
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

            faces.release();
            return hasFaces;
        }
        catch(Exception | Error e) {
            if (RestogramClient.getInstance().isDebuggable())
                Log.e("REST-O-GRAM", "openCV face detection failed");
            return true;
        }
    }

    @Override
    public FaceDetector clone() throws CloneNotSupportedException {
        super.clone();
        return new OpenCVFaceDetector(cascadeFile);
    }

    @Override
    protected void initOpenCVClassifier(final File cascadeFile) {
        try {
            final String classifierPath =  cascadeFile.getAbsolutePath();
            if (RestogramClient.getInstance().isDebuggable())
                Log.d("REST-O-GRAM", "path to classifier: " + classifierPath);
            nativeLoadClassifier(classifierPath, (long)Defs.Filtering.OpenCVDetector.MIN_FACE_SIZE,
                                 RestogramClient.getInstance().isDebuggable());
        }
        catch(Exception | Error e) {
            if (RestogramClient.getInstance().isDebuggable())
                Log.e("REST-O-GRAM", "openCV classifier loading failed");
        }
    }

    private static native void nativeLoadClassifier(String cascadeName, long minFaceSize, boolean debug);
    private static native void nativeDetectFaces(long inputImage, long faces, long minSize, long maxSize, boolean debug);
}
