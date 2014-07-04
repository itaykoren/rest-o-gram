package rest.o.gram.openCV;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 8/22/13
 */
// TODO: redfactor error reporting to either usa of ordered broadcast(handled by activity or causes notification) if
// called from main activity or if called from app init - maybe create a dialog-like activity instead of AlertDialog.
public abstract class RestogramBaseLoaderCallback extends BaseLoaderCallback {
    public RestogramBaseLoaderCallback(Context appContext) {
        super(appContext);
        mAppContext = appContext;
    }

    public void onManagerConnected(int status)
    {
        switch (status)
        {
            /** OpenCV initialization was successful. **/
            case LoaderCallbackInterface.SUCCESS:
            {
                onSuccess();
                /** Application must override this method to handle successful library initialization. **/
            } break;
            /** OpenCV loader can not start Google Play Market. **/
            case LoaderCallbackInterface.MARKET_ERROR:
            {
                Log.w(TAG, "Package installation failed!");
                AlertDialog MarketErrorMessage = new AlertDialog.Builder(mAppContext).create();
                MarketErrorMessage.setTitle("OpenCV Manager");
                MarketErrorMessage.setMessage("Package installation failed!");
                MarketErrorMessage.setCancelable(false); // This blocks the 'BACK' button
                MarketErrorMessage.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        onFailure();
                    }
                });
                MarketErrorMessage.show();
            } break;
            /** Package installation has been canceled. **/
            case LoaderCallbackInterface.INSTALL_CANCELED:
            {
                Log.w(TAG, "OpenCV library instalation was canceled by user");
                onFailure();
            } break;
            /** Application is incompatible with this version of OpenCV Manager. Possibly, a service update is required. **/
            case LoaderCallbackInterface.INCOMPATIBLE_MANAGER_VERSION:
            {
                Log.w(TAG, "OpenCV Manager Service is uncompatible with this app!");
                AlertDialog IncomatibilityMessage = new AlertDialog.Builder(mAppContext).create();
                IncomatibilityMessage.setTitle("OpenCV Manager");
                IncomatibilityMessage.setMessage("OpenCV Manager service is incompatible with this app. Try to update it via Google Play.");
                IncomatibilityMessage.setCancelable(false); // This blocks the 'BACK' button
                IncomatibilityMessage.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        onFailure();
                    }
                });
                IncomatibilityMessage.show();
            } break;
            /** Other status, i.e. INIT_FAILED. **/
            default:
            {
                Log.w(TAG, "OpenCV loading failed!");
                AlertDialog InitFailedDialog = new AlertDialog.Builder(mAppContext).create();
                InitFailedDialog.setTitle("OpenCV error");
                InitFailedDialog.setMessage("OpenCV was not initialised correctly. Application will be shut down");
                InitFailedDialog.setCancelable(false); // This blocks the 'BACK' button
                InitFailedDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        onFailure();
                    }
                });

                InitFailedDialog.show();
            } break;
        }
    }

    protected void onSuccess() { }

    protected void onFailure()
    {
        // NOTE: context is not guaranteed to be an activity and
        // init failure does not necessarily mean shutting down
        //((Activity) mAppContext).finish();
    }

    protected Context mAppContext;
    private final static String TAG = "rest-o-gram";
}