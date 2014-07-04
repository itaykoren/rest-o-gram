
package rest.o.gram.openCV;

import android.content.Context;
import android.util.Log;
import org.opencv.android.OpenCVLoader;
import rest.o.gram.common.Defs.Filtering;
import rest.o.gram.filters.FaceDetector;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 6/25/14
 */
public final class OpenCVFaceDetectorFactory implements FaceDetectorFactory {
    public OpenCVFaceDetectorFactory(final Context context) {
        this.context = context;
    }

    @Override
    public void create(final Filtering.BitmapFilterType type) {
        if (callback == null)
            return;

        if (type == Filtering.BitmapFilterType.JavaCVFaceBitmapFilter)
            initJavaCV();
        else if (type == Filtering.BitmapFilterType.OpenCVFaceBitmapFilter) {
            // TODO: refactor
            try {
                System.loadLibrary("face_detector");
            } catch (Throwable tr) {
                Log.e("REST-O-GRAM", "error while loading face_detector", tr);
                callback.onFaceDetectorInit(null); // error while loading face_detector is recoverable
                return;
            }
            callback.onFaceDetectorInit(new OpenCVFaceDetector());
        }
    }

    @Override
    public FaceDetectorInitCallback getCallback() {
        return callback;
    }

    @Override
    public void setCallback(final FaceDetectorInitCallback callback) {
        this.callback = callback;
    }

    private void initJavaCV() {
        // first - tries shared library load
        if (Filtering.JavaCV.USE_SHARED_LIBRARY_INIT && initSharedLibrary()) {
            Log.i("REST-O-GRAM", "JavaCV loaded sucessfully)");
            createJavaCVFaceDetector();
            return;
        }

        if (!Filtering.JavaCV.USE_OPENCV_MANAGER_INIT) {
            callback.onFaceDetectorInit(null);
            return;
        }

        // if could not load library - tries the OpenCV manager
        final RestogramBaseLoaderCallback javaCVCallback = new RestogramBaseLoaderCallback(context) {
            @Override
            protected void onSuccess() {
                super.onSuccess();
                Log.i("REST-O-GRAM", "openCVManager loaded sucessfully");
                createJavaCVFaceDetector();
            }

            @Override
            protected void onFailure() {
                super.onFailure();
                Log.w("REST-O-GRAM", "openCVManager could not be loaded");
                callback.onFaceDetectorInit(null);
            }
        };

        if (!initOpenCVManager(javaCVCallback)) {
            Log.w("REST-O-GRAM", "openCVManager could not be loaded, will try to install");
            callback.onFaceDetectorInit(null);
        }
    }

    private void createJavaCVFaceDetector() {
        final FaceDetector detector = new JavaCVFaceDetector();
        if (!detector.initialize(context))
        {
            callback.onFaceDetectorInit(null);
            return;
        }

        callback.onFaceDetectorInit(detector);
    }

    private boolean initSharedLibrary() {
        try {
            return OpenCVLoader.initDebug();
        } catch (Throwable tr) {
            Log.e("REST-O-GRAM", "error while loading openCV", tr);
            return false; // error while loading openCV is recoverable
        }
    }

    private boolean initOpenCVManager(final RestogramBaseLoaderCallback callback) {
        try  {
            return OpenCVLoader.initAsync(Defs.OPENCV_VERSION, context, callback);
        } catch (Throwable tr) {
            Log.e("REST-O-GRAM", "error while loading openCV", tr);
            return false; // error while loading openCV is recoverable
        }
    }

    private Context context;
    private FaceDetectorInitCallback callback;
}
