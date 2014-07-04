package rest.o.gram.openCV;

import android.graphics.Bitmap;
import android.util.Log;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
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

    @Override
    public boolean hasFaces(final Bitmap bitmap) {
        try {
            Log.v("REST-O-GRAM", "openCV bitmap filter used");

            final double maxFaceSize = Math.min(bitmap.getWidth(), bitmap.getHeight());
            final long actualMaxFaceSize = (long)(maxFaceSize * Defs.Filtering.OpenCVDetector.MAX_FACE_SIZE_FACTOR);
            final Mat pixelMatrix = createPixelMatrix(bitmap);
            final MatOfRect faces = new MatOfRect();

            nativeDetectFaces(pixelMatrix.getNativeObjAddr(), faces.getNativeObjAddr(),
                              (long)Defs.Filtering.OpenCVDetector.MIN_FACE_SIZE, actualMaxFaceSize);

            final boolean hasFaces = !faces.empty();
            Log.v("REST-O-GRAM", "is bitmap approved? " + !hasFaces);

            faces.release();
            return hasFaces;
        }
        catch(Throwable tr) {
            Log.e("REST-O-GRAM", "openCV face detection failed", tr);
            return true;
        }
    }

    @Override
    public FaceDetector clone() throws CloneNotSupportedException {
        final OpenCVFaceDetector clone = new OpenCVFaceDetector();
        if (!clone.initOpenCVClassifier(cascadeFile))
            return null;
        return clone;
    }

    @Override
    protected boolean initOpenCVClassifier(final File cascadeFile) {
        try {
            final String classifierPath =  cascadeFile.getAbsolutePath();
            Log.d("REST-O-GRAM", "path to classifier: " + classifierPath);
            nativeLoadClassifier(classifierPath, (long)Defs.Filtering.OpenCVDetector.MIN_FACE_SIZE);
        }
        catch(Throwable tr) {
                Log.e("REST-O-GRAM", "openCV classifier loading failed", tr);
            throw tr;
        }
        return true;
    }

    private static native void nativeLoadClassifier(String cascadeName, long minFaceSize);
    private static native void nativeDetectFaces(long inputImage, long faces, long minSize, long maxSize);
}
