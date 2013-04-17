package com.tau.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.tau.RestogramPhoto;
import com.tau.RestogramVenue;
import com.tau.client.RestogramClient;
import com.tau.common.Defs;
import com.tau.location.ILocationObserver;
import com.tau.location.ILocationTracker;
import com.tau.tasks.ITaskObserver;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 16/04/13
 */
public class HomeActivity extends Activity implements ILocationObserver, ITaskObserver {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setContentView(R.layout.home);

        tracker = RestogramClient.getInstance().getLocationTracker();
        if(tracker != null) {
            tracker.setObserver(this);
            tracker.start();
        }
        else {
            // TODO: implementation
        }
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

        RestogramVenue venue = venues[0]; // TODO: fix

        // Switch to "FindMeActivity" with parameter "venue"
        Intent intent = new Intent(this, FindMeActivity.class);
        intent.putExtra("venue", venue);
        startActivityForResult(intent, Defs.RequestCodes.RC_FINDME);
    }

    @Override
    public void onFinished(RestogramVenue venue) {
        // Empty
    }

    @Override
    public void onFinished(RestogramPhoto[] photos) {
        // Empty
    }

    private ILocationTracker tracker; // Location tracker
    private double latitude;
    private double longitude;
}
