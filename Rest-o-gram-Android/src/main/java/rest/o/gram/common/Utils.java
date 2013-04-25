package rest.o.gram.common;

import android.widget.TextView;

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
}
