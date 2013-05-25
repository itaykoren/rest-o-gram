package rest.o.gram.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.leanengine.LeanAccount;
import rest.o.gram.R;
import rest.o.gram.common.Defs;
import rest.o.gram.activities.helpers.LoginHelper;

/**
 * Created with IntelliJ IDEA.
 * User: Hen
 * Date: 24/05/13
 */
public class RestogramActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize login helper
        loginHelper = new LoginHelper(this);
    }

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

                if(!LeanAccount.isUserLoggedIn()) {
                    loginHelper.login(true);
                }
                else {
                    loginHelper.switchToPersonalActivity();
                }
                break;
            }
            default:
                break;
        }

        return true;
    }

    private LoginHelper loginHelper; // Login helper
}
