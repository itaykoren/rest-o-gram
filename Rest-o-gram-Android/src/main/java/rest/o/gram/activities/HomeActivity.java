package rest.o.gram.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import rest.o.gram.R;
import rest.o.gram.entities.RestogramVenue;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.common.Defs;
import rest.o.gram.location.ILocationObserver;
import rest.o.gram.location.ILocationTracker;
import rest.o.gram.network.INetworkStateProvider;
import rest.o.gram.tasks.results.GetInfoResult;
import rest.o.gram.tasks.results.GetNearbyResult;
import rest.o.gram.tasks.results.GetPhotosResult;
import rest.o.gram.tasks.ITaskObserver;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 16/04/13
 */
public class HomeActivity extends Activity implements ILocationObserver, ITaskObserver {

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
            else
            {
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
    protected  void onDestroy() {
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

        this.latitude = latitude;
        this.longitude = longitude;

        double radius;
        if (rest.o.gram.location.Utils.isAccurate(accuracy, provider))
            radius = Defs.Location.DEFAULT_FINDME_RADIUS;
        else
            radius = Defs.Location.DEFAULT_NEARBY_RADIUS;

        // Send get nearby request
        RestogramClient.getInstance().getNearby(latitude, longitude, radius, this);
    }

    @Override
    public void onTrackingTimedOut() {
        if(tracker != null)
            tracker.stop();
        diagManager.showLocationTrackingAlert(this);
    }

    @Override
    public void onFinished(GetNearbyResult result) {
        final RestogramVenue[] venues = result.getVenues();
        if(venues == null || venues.length == 0 || venues.length >= Defs.VENUES_AMBIGOUITY_LEVEL)
        {
            if (RestogramClient.getInstance().isDebuggable())
            {
                if (venues == null)
                    Log.d("REST-O-GRAM", "an error occured while searching for venues");
                else
                    Log.d("REST-O-GRAM", "no venues found");
            }
            // Switch to "NearbyActivity" with parameters: "latitude", "longitude"
            Intent intent = new Intent(this, NearbyActivity.class);
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            startActivityForResult(intent, Defs.RequestCodes.RC_NEARBY);
            return;
        }

        venue = venues[0];

        // Send get info request
        RestogramClient.getInstance().getInfo(venue.getFoursquare_id(), this);
    }

    @Override
    public void onFinished(GetInfoResult result) {
        final RestogramVenue venue = result.getVenue();
        if(venue == null)
            return;

        // Set image url to current venue member object
        this.venue.setImageUrl(venue.getImageUrl());

        // Switch to "VenueActivity" with parameter "venue"
        Intent intent = new Intent(this, VenueActivity.class);
        intent.putExtra("venue", this.venue);
        startActivityForResult(intent, Defs.RequestCodes.RC_VENUE);
    }

    @Override
    public void onFinished(GetPhotosResult result) {
        // Empty
    }

    private void cancelProgress() {
        ProgressBar pb = (ProgressBar)findViewById(R.id.pbLoading);
        pb.setVisibility(View.GONE);
    }

    private ILocationTracker tracker; // Location tracker
    private double latitude;
    private double longitude;
    private boolean gotLocation;
    private RestogramVenue venue;
    private DialogManager diagManager;
}