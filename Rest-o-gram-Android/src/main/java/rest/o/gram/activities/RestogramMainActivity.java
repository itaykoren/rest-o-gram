package rest.o.gram.activities;

import android.os.Handler;
import android.widget.Toast;
import rest.o.gram.client.RestogramClient;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 2/1/14
 */
public class RestogramMainActivity extends RestogramActionBarActivity {

    @Override
    public void onBackPressed() {
        if (RestogramClient.getInstance().getApplication().isInLastActivity()
                && isBackPressedOnce) {
            super.onBackPressed();
            return;
        }

        isBackPressedOnce = true;
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isBackPressedOnce = false;
            }
        }, 2000);
    }

    private boolean isBackPressedOnce = false;
}
