package rest.o.gram.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import rest.o.gram.R;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.common.Defs;
import rest.o.gram.common.Utils;

/**
 * Created with IntelliJ IDEA.
 * User: Hen
 * Date: 26/05/13
 */
public class RestogramActionBarActivity extends RestogramActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Force options menu recreation
        invalidateOptionsMenu();
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
                if(super.getClass() == NearbyActivity.class)
                    break;

                // Switch to "NearbyActivity" with no parameters
                Intent intent = new Intent(this, NearbyActivity.class);
                Utils.changeActivity(this, intent, Defs.RequestCodes.RC_NEARBY, false);
                break;
            }
            case R.id.action_explore: {
                if(super.getClass() == ExploreActivity.class)
                    break;

                // Switch to "ExploreActivity" with no parameters
                Intent intent = new Intent(this, ExploreActivity.class);
                Utils.changeActivity(this, intent, Defs.RequestCodes.RC_EXPLORE, false);
                break;
            }
            case R.id.action_map: {
                if(super.getClass() == MapActivity.class)
                    break;

                // Switch to "MapActivity" with no parameters
                Intent intent = new Intent(this, MapActivity.class);
                Utils.changeActivity(this, intent, Defs.RequestCodes.RC_MAP, false);
                break;
            }
            case R.id.action_personal: {
                if(super.getClass() == PersonalActivity.class)
                    break;

                if(!RestogramClient.getInstance().getAuthenticationProvider().isUserLoggedIn()) {
                    loginHelper.login(true);
                }
                else {
                    // Switch to "PersonalActivity" with no parameters
                    Intent intent = new Intent(this, PersonalActivity.class);
                    Utils.changeActivity(this, intent, Defs.RequestCodes.RC_PERSONAL, false);
                }
                break;
            }
            case R.id.action_logout: {
                if(!RestogramClient.getInstance().getAuthenticationProvider().isUserLoggedIn())
                    break;
                else {
                    loginHelper.logout();
                    break;
                }
            }
            default:
                break;
        }

        return true;
    }
}
