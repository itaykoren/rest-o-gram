package rest.o.gram.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import rest.o.gram.R;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.common.Defs;
import rest.o.gram.common.Utils;
import rest.o.gram.entities.RestogramVenue;
import rest.o.gram.location.ILocationObserver;
import rest.o.gram.location.ILocationTracker;
import rest.o.gram.network.INetworkStateProvider;
import rest.o.gram.tasks.results.GetNearbyResult;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 16/04/13
 */
public class HomeActivity extends RestogramActivity implements ILocationObserver {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home);

        diagManager = new DialogManager();

        // Get location tracker
        tracker = RestogramClient.getInstance().getLocationTracker();
        if (tracker != null) {
            if (!tracker.canDetectLocation())
                diagManager.showLocationTrackingAlert(this);
            else {
                tracker.setObserver(this);
                tracker.start();
            }
        }
        else {
            // TODO: implementation
            return;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        cancelProgress();
    }

    @Override
    protected void onDestroy() { // Activity exiting
        super.onDestroy();

        if(tracker != null)
            tracker.stop();

        diagManager.clear();
    }

    @Override
    public void onLocationUpdated(double latitude, double longitude, int accuracy, String provider) {
        if (gotLocation)
            return;
        gotLocation = true;

        if(tracker != null)
            tracker.stop();

        final INetworkStateProvider netStateProvider =
                RestogramClient.getInstance().getNetworkStateProvider();
        if (netStateProvider != null)
        {
            if (!netStateProvider.isOnline())
            {
                diagManager.showNetworkStateAlert(this);
                return;
            }
        }
        else {
            // TODO: implementation
            return;
        }

        // Send get nearby request
        RestogramClient.getInstance().getNearby(latitude, longitude, Defs.Location.DEFAULT_NEARBY_RADIUS, this);
    }

    @Override
    public void onTrackingTimedOut() {
        if(tracker != null)
            tracker.stop();
        diagManager.showLocationTrackingAlert(this);
    }

    @Override
    public void onFinished(GetNearbyResult result) {
        super.onFinished(result);

        final RestogramVenue[] venues = result.getVenues();
        if(venues == null || venues.length == 0) {
            if (RestogramClient.getInstance().isDebuggable()) {
                if (venues == null)
                    Log.d("REST-O-GRAM", "an error occurred while searching for venues");
                else
                    Log.d("REST-O-GRAM", "no venues found");
            }

            isFoundVenues = false;
        }
        else {
            isFoundVenues = true;
        }

        if(Defs.Flow.WELCOME_SCREENS_ENABLED) {
            if(Utils.isShowWelcomeScreen(this)) {
                setContentView(R.layout.welcome);
                return;
            }
        }

        start();
    }

    public void onOkClicked(View view) {
        Utils.setIsShowWelcomeScreen(this, false);
        start();
    }

    private void start() {
        if(!isFoundVenues) {
            // Show error dialog and switch to map after "ok"
            diagManager.showNoVenuesAlert(this, true);
        }
        else {
            // Switch to "ExploreActivity" with no parameters
            Intent intent = new Intent(this, ExploreActivity.class);
            Utils.changeActivity(this, intent, Defs.RequestCodes.RC_EXPLORE, true);
        }
    }

    private void cancelProgress() {
        View v = findViewById(R.id.pbLoading);
        if(v == null)
            return;

        ProgressBar pb = (ProgressBar)v;
        pb.setVisibility(View.GONE);
    }

    private ILocationTracker tracker; // Location tracker
    private boolean gotLocation;
    private DialogManager diagManager;
    private boolean isFoundVenues = false;
}