package rest.o.gram.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import rest.o.gram.R;
import rest.o.gram.RestogramPhoto;
import rest.o.gram.RestogramVenue;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.common.Defs;
import rest.o.gram.location.ILocationObserver;
import rest.o.gram.location.ILocationTracker;
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

        // Get location tracker
        tracker = RestogramClient.getInstance().getLocationTracker();
        if(tracker != null) {
            tracker.setObserver(this);
            tracker.start();
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
    public void onLocationUpdated(double latitude, double longitude) {
        if(tracker != null) {
            tracker.stop();
        }

        this.latitude = latitude;
        this.longitude = longitude;

        // Send get nearby request
        RestogramClient.getInstance().getNearby(latitude, longitude, Defs.Location.DEFAULT_FINDME_RADIUS, this);
    }

    @Override
    public void onFinished(RestogramVenue[] venues) {
        if(venues == null || venues.length == 0)
        {
            // Switch to "NearbyActivity" with parameters: "latitude", "longitude"
            Intent intent = new Intent(this, NearbyActivity.class);
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            startActivityForResult(intent, Defs.RequestCodes.RC_NEARBY);
            return;
        }

        venue = venues[0]; // TODO: fix

        // Send get info request
        RestogramClient.getInstance().getInfo(venue.getId(), this);
    }

    @Override
    public void onFinished(RestogramVenue venue) {
        // Set image url to current venue member object
        this.venue.setImageUrl(venue.getImageUrl());

        // Switch to "VenueActivity" with parameter "venue"
        Intent intent = new Intent(this, VenueActivity.class);
        intent.putExtra("venue", this.venue);
        startActivityForResult(intent, Defs.RequestCodes.RC_VENUE);
    }

    @Override
    public void onFinished(RestogramPhoto[] photos) {
        // Empty
    }

    private void cancelProgress() {
        ProgressBar pb = (ProgressBar)findViewById(R.id.pbLoading);
        pb.setVisibility(View.GONE);
    }

    private ILocationTracker tracker; // Location tracker
    private double latitude;
    private double longitude;

    private RestogramVenue venue;
}
