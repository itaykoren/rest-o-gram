package rest.o.gram.common;

import android.widget.TextView;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 20/04/13
 */
public class Utils {
    public static void updateTextView(TextView tv, String text) {
        if(tv == null)
            return;

        tv.setText(text);
    }
}
