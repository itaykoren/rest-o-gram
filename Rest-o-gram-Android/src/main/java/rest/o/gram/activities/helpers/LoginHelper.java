package rest.o.gram.activities.helpers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import com.leanengine.LeanError;
import com.leanengine.LoginListener;
import com.leanengine.NetworkCallback;
import rest.o.gram.R;
import rest.o.gram.activities.DialogManager;
import rest.o.gram.activities.NearbyActivity;
import rest.o.gram.activities.PersonalActivity;
import rest.o.gram.activities.RestogramActivity;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.common.Defs;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 5/25/13
 */
public class LoginHelper {
    /**
     * Ctor
     */
    public LoginHelper(RestogramActivity activity) {
        this.activity = activity;
        dialogManager = new DialogManager();
    }

    public void login(boolean switchToPersonalActivity) {
        this.switchToPersonalActivity = switchToPersonalActivity;

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder
                .setTitle(R.string.restogram_login_title)
                .setMessage(R.string.not_logged_in_err_msg)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        showLoginScreen();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    public void logout(boolean switchToNearbyActivity) {
        this.switchToNearbyActivity = switchToNearbyActivity;

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder
                .setTitle(R.string.restogram_logout_title)
                .setMessage(R.string.logout_msg)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        doLogout();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    public void switchToPersonalActivity() {
        // Switch to "PersonalActivity" with no parameters
        Intent intent = new Intent(activity, PersonalActivity.class);
        activity.startActivityForResult(intent, Defs.RequestCodes.RC_PERSONAL);
    }

    private void showLoginScreen() {
        dialogManager.showLoginDialog(activity, new LoginListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(activity, "Successfully logged in.", Toast.LENGTH_LONG).show();

                activity.onUserLoggedIn();

                if (RestogramClient.getInstance().isDebuggable())
                    Log.d("REST-O-GRAM", "login successful");

                if(switchToPersonalActivity)
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

    private void doLogout() {
        RestogramClient.getInstance().getAuthenticationProvider().logoutInBackground(new NetworkCallback<Boolean>() {
            @Override
            public void onResult(Boolean... result) {
                Toast.makeText(activity, "Successfully logged out.", Toast.LENGTH_LONG).show();

                activity.onUserLoggedOut();

                if (RestogramClient.getInstance().isDebuggable())
                    Log.d("REST-O-GRAM", "logout successful");

                if(switchToNearbyActivity)
                    switchToNearbyActivity();
            }

            @Override
            public void onFailure(LeanError error) {
                final String errorMsg = error.getErrorMessage();
                if (RestogramClient.getInstance().isDebuggable())
                    Log.d("REST-O-GRAM", "logout - Error: " + error.getErrorType() + "Error desc: " + errorMsg);
                if (errorMsg != null && !errorMsg.isEmpty())
                    Toast.makeText(activity, error.getErrorMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void switchToNearbyActivity() {
        // Switch to "NearbyActivity" with no parameters
        Intent intent = new Intent(activity, NearbyActivity.class);
        activity.startActivityForResult(intent, Defs.RequestCodes.RC_NEARBY);
    }

    private DialogManager dialogManager;
    private RestogramActivity activity;
    private boolean switchToPersonalActivity = false;
    private boolean switchToNearbyActivity = false;
}
