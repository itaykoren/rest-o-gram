package rest.o.gram.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import com.leanengine.LeanError;
import com.leanengine.LoginDialog;
import com.leanengine.LoginListener;
import rest.o.gram.R;
import rest.o.gram.client.RestogramClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/9/13
 */
public final class DialogManager {
    public void showLocationTrackingAlert(final Activity activity) {
        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                showErrorAlert(activity, R.string.cannot_track_location_err_msg,
                               Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            }
        }, 500);
    }

    public void showNetworkStateAlert(final Activity activity) {
        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                showErrorAlert(activity, R.string.no_connectivity_err_msg,
                        Settings.ACTION_NETWORK_OPERATOR_SETTINGS);
            }
        }, 500);
    }

    public void showLoginDialog(final Activity activity, LoginListener loginListener) {
        Uri loginUri = RestogramClient.getInstance().getAuthenticationProvider().getFacebookLoginUri();

        final LoginDialog fbDialog =
                new LoginDialog(activity, loginUri.toString(), loginListener);

        dialogs.add(fbDialog);
        fbDialog.show();
    }

    public void clear() {
        for (DialogInterface diag : dialogs)
            diag.cancel();
        dialogs.clear();
    }

    private void showErrorAlert(final Activity activity, final int message, final String action) {
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
                        dialogs.remove(dialog);
                        activity.finish();
                    }
                });

        dialogs.add(alertDialog.show());
    }

    private final List<DialogInterface> dialogs = new ArrayList<>();
}