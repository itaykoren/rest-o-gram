package rest.o.gram.openCV;

import android.graphics.Bitmap;
import android.util.Log;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Size;
import org.opencv.objdetect.CascadeClassifier;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.common.Defs;
import rest.o.gram.filters.FaceDetector;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 8/26/13
 */
public class JavaCVFaceDetector extends FaceDetectorBase {
    /**
     * Default ctor
     */
    public JavaCVFaceDetector() {}

    /**
     * "Copy ctor"
     */
    public JavaCVFaceDetector(final File cascadeFile) {
        initOpenCVClassifier(cascadeFile);
    }

    @Override
    public boolean hasFaces(Bitmap bitmap) {
        try
        {
            if (RestogramClient.getInstance().isDebuggable())
                Log.d("REST-O-GRAM", "openCV bitmap filter used");
            final Mat pixelMatrix = createPixelMatrix(bitmap);
            final double maxFaceSize = Math.min(bitmap.getWidth(), bitmap.getHeight());
            final double actualMaxFaceSize = maxFaceSize * Defs.Filtering.OpenCVDetector.MAX_FACE_SIZE_FACTOR;
            final MatOfRect objects = new MatOfRect();
            if (RestogramClient.getInstance().isDebuggable())
                Log.d("REST-O-GRAM", "processing bitmap");
            final double minFaceSize = Defs.Filtering.OpenCVDetector.MIN_FACE_SIZE;
            classifier.detectMultiScale(pixelMatrix, objects, 1.1, 2, 0, new Size(minFaceSize,minFaceSize), new Size(actualMaxFaceSize, actualMaxFaceSize));
            if (RestogramClient.getInstance().isDebuggable())
                Log.d("REST-O-GRAM", "is bitmap approved? " + objects.empty());

            return !objects.empty();
        }
        catch(Exception e)
        {
            Log.e("REST-O-GRAM", "error while processing bitmap : " + e.getMessage());
            return false;
        }
    }

    @Override
    public FaceDetector clone() throws CloneNotSupportedException {
        super.clone();
        return new JavaCVFaceDetector(cascadeFile);
    }

    @Override
    protected void initOpenCVClassifier(final File cascadeFile) {
        try {
            classifier = new CascadeClassifier(cascadeFile.getAbsolutePath());
            if(RestogramClient.getInstance().isDebuggable()) {
                if(classifier.empty())
                    Log.e("REST-O-GRAM", "Failed to load cascade classifier");
                else
                    Log.i("REST-O-GRAM", "Loaded cascade classifier from " + cascadeFile.getAbsolutePath());
            }
        }
        catch(Exception | Error e) {
            if(RestogramClient.getInstance().isDebuggable())
                Log.e("REST-O-GRAM", "openCV classifier loading failed");
        }
    }

    private CascadeClassifier classifier;
}
