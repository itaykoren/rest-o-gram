package rest.o.gram.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import com.leanengine.LeanError;
import com.leanengine.LoginListener;
import rest.o.gram.R;
import rest.o.gram.activities.DialogManager;
import rest.o.gram.activities.PersonalActivity;
import rest.o.gram.client.RestogramClient;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 5/25/13
 */
public class LoginHelper {
    /**
     * Ctor
     */
    public LoginHelper(Activity activity) {
        this.activity = activity;
        dialogManager = new DialogManager();
    }

    public void login() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder
                .setTitle(R.string.restogram_error_title)
                .setMessage(R.string.not_logged_in_err_msg)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        showLoginScreen();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void showLoginScreen() {
        dialogManager.showLoginDialog(activity, new LoginListener() {
            @Override
            public void onSuccess() {
                if (RestogramClient.getInstance().isDebuggable())
                    Log.d("REST-O-GRAM", "login successful");

                switchToPersonalActivity();
            }

            @Override
            public void onCancel() {
                if (RestogramClient.getInstance().isDebuggable())
                    Log.d("REST-O-GRAM", "login cancelled");
            }

            @Override
            public void onError(LeanError error) {
                final String errorMsg = error.getErrorMessage();
                if (RestogramClient.getInstance().isDebuggable())
                    Log.d("REST-O-GRAM", "login - Error: " +  error.getErrorType().toString() +  " Error desc: " + errorMsg);
                if (errorMsg != null && !errorMsg.isEmpty())
                    Toast.makeText(activity, error.getErrorMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void switchToPersonalActivity() {
        // Switch to "PersonalActivity" with no parameters
        Intent intent = new Intent(activity, PersonalActivity.class);
        activity.startActivityForResult(intent, Defs.RequestCodes.RC_PERSONAL);
    }

    private DialogManager dialogManager;
    private Activity activity;
}
