package rest.o.gram.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created with IntelliJ IDEA.
 * User: Itay
 * Date: 19/04/13
 */
public class StartupBroadcastReceiver extends BroadcastReceiver {
    static final String TAG = "StartupBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (LocationLibrary.showDebugOutput)
            Log.d(LocationLibraryConstants.TAG, TAG + ": onReceive: phone rebooted -> start alarm and listener");
        LocationLibrary.startAlarmAndListener(context);
    }
}


