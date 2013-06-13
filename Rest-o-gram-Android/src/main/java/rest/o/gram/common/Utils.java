package rest.o.gram.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 20/04/13
 */
public class Utils {

    /**
     * Updates given text view with given text
     */
    public static void updateTextView(TextView tv, String text) {
        if(tv == null || text == null)
            return;

        tv.setText(text);
    }

    /**
     * Converts given unix timestamp to formatted date
     */
    public static String convertDate(String timestamp) {
        String date = "";

        if(timestamp == null)
            return date;

        try {
            long seconds = Long.decode(timestamp);
            Date d = new Date(seconds * 1000);
            date = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(d);
        }
        catch(Exception e) {
            return date;
        }

        return date;
    }

    /**
     * Compares two Date objects
     * Returns: -1 if lhs < rhs
     *           1 if lhs > rhs
     *           0 otherwise
     */
    public static int compare(Date lhs, Date rhs) {
        if(lhs == null || rhs == null)
            return 0;

        int res = lhs.compareTo(rhs);
        if(res < 0)
            return -1;
        else if(res > 0)
            return 1;
        else
            return 0;
    }

    /**
     * Serializes object to given file
     */
    public static void serialize(Object object, FileOutputStream stream) {
        try{
            ObjectOutputStream out = new ObjectOutputStream(stream);

            out.writeObject(object);

            out.close();
            stream.close();
        }
        catch(Exception e) {
            // TODO: report error
        }
    }

    /**
     * Deserializes object from given file
     */
    public static Object deserialize(FileInputStream stream) {
        Object object;
        try{
            ObjectInputStream in = new ObjectInputStream(stream);

            object = in.readObject();

            in.close();
            stream.close();
        }
        catch(Exception e) {
            // TODO: report error
            return null;
        }

        return object;
    }

    /**
     * Changes activity to a new one according to given parameters
     */
    public static void changeActivity(Activity oldActivity, Intent intent, int requestCode, boolean finishOld) {
        if(finishOld)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        oldActivity.startActivityForResult(intent, requestCode);
    }

    /**
     * Starts navigation to given destination
     */
    public static void startNavigation(Activity activity, double latitude, double longitude) {
        // Create intent with location parameters
        String destination = latitude + "," + longitude;
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?daddr=" + destination));

        // Launch navigation
        activity.startActivity(intent);
    }

    /**
     * Returns true whether welcome screen should be shown for the given activity
     */
    public static boolean isShowWelcomeScreen(Activity activity) {
        SharedPreferences preferences = activity.getPreferences(0);
        return preferences.getBoolean("show_welcome", true);
    }

    /**
     * Sets is show welcome screen state for the given activity
     */
    public static void setIsShowWelcomeScreen(Activity activity, boolean isShow) {
        SharedPreferences preferences = activity.getPreferences(0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("show_welcome", isShow);
        editor.commit();
    }

    /**
     * Returns true whether google play services is available, false otherwise
     */
    public static boolean isPlayServicesAvailable(Context context) {
        // Check google play services
        final int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if(status == ConnectionResult.SERVICE_MISSING ||
                status == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED ||
                status == ConnectionResult.SERVICE_DISABLED) {
            return false;
        }
        else {
            return true;
        }
    }
}
