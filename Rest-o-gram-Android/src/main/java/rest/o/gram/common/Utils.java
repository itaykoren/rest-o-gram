package rest.o.gram.common;

import android.app.Activity;
import android.content.Intent;
import android.widget.TextView;

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
    public static void serialize(Object object, File file) {
        try{
            FileOutputStream fileOut = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);

            out.writeObject(object);

            out.close();
            fileOut.close();
        }
        catch(Exception e) {
            // TODO: report error
        }
    }

    /**
     * Deserializes object from given file
     */
    public static Object deserialize(File file) {
        Object object;
        try{
            FileInputStream fileIn = new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(fileIn);

            object = in.readObject();

            in.close();
            fileIn.close();
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
}
