package rest.o.gram.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.provider.Settings;
import rest.o.gram.R;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/9/13
 */
public final class Utils {
    public static void showLocationTrackingAlert(final Activity activity) {
        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                showErrorAlert(activity, R.string.cannot_track_location_err_msg,
                               Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            }
        }, 500);
    }

    public static void showNetworkStateAlert(final Activity activity) {
        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                showErrorAlert(activity, R.string.no_connectivity_err_msg,
                        Settings.ACTION_NETWORK_OPERATOR_SETTINGS);
            }
        }, 500);
    }

    private static void showErrorAlert(final Activity activity, final int message, final String action) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);

        // Setting Dialog Title
        alertDialog.setTitle(R.string.restogram_error_title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(action);
                        activity.startActivity(intent);
                    }
                });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        activity.finish();
                    }
                });

        alertDialog.show();
    }
}
