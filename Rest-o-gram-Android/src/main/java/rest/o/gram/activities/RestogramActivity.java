package rest.o.gram.activities;

import android.content.Intent;
import rest.o.gram.R;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import rest.o.gram.common.Defs;

/**
 * Created with IntelliJ IDEA.
 * User: Hen
 * Date: 24/05/13
 */
public class RestogramActivity extends Activity {
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch(item.getItemId()) {
            case R.id.action_nearby: {
                if(getClass() == NearbyActivity.class)
                    break;

                // Switch to "NearbyActivity" with no parameters
                Intent intent = new Intent(this, NearbyActivity.class);
                startActivityForResult(intent, Defs.RequestCodes.RC_NEARBY);
                break;
            }
            case R.id.action_personal: {
                if(getClass() == PersonalActivity.class)
                    break;

                // Switch to "PersonalActivity" with no parameters
                Intent intent = new Intent(this, PersonalActivity.class);
                startActivityForResult(intent, Defs.RequestCodes.RC_PERSONAL);
                break;
            }
            default:
                break;
        }

        return true;
    }
}
