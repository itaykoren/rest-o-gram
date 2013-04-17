package com.tau.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.tau.R;
import com.tau.RestogramPhoto;
import com.tau.RestogramVenue;
import com.tau.client.RestogramClient;
import com.tau.common.Defs;
import com.tau.tasks.ITaskObserver;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 16/04/13
 */
public class FindMeActivity extends Activity implements ITaskObserver {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.findme);

        // Get venue parameter
        RestogramVenue venue;
        try {
            Intent intent = getIntent();
            venue = (RestogramVenue)intent.getSerializableExtra("venue");
        }
        catch(Exception e) {
            // TODO: implementation
            return;
        }

        // Initialize using venue parameter
        initialize(venue);
    }

    @Override
    public void onFinished(RestogramVenue[] venues) {
        // Empty
    }

    @Override
    public void onFinished(RestogramVenue venue) {
        // TODO: set image url to current venue member object
        // TODO: set ui (venue image)
    }

    @Override
    public void onFinished(RestogramPhoto[] photos) {
        // Empty
    }

    public void onVenueClicked(View view) {
        // Switch to "VenueActivity" with parameter "venue"
        Intent intent = new Intent(this, VenueActivity.class);
        intent.putExtra("venue", venue);
        startActivityForResult(intent, Defs.RequestCodes.RC_VENUE);
    }

    public void onNotHereClicked(View view) {
        // Switch to "NearbyActivity" with parameters: "latitude", "longitude"
        Intent intent = new Intent(this, NearbyActivity.class);
        intent.putExtra("latitude", venue.getLatitude());
        intent.putExtra("longitude", venue.getLongitude());
        startActivityForResult(intent, Defs.RequestCodes.RC_NEARBY);
    }

    /**
     * Initializes using given venue
     */
    private void initialize(RestogramVenue venue) {
        this.venue = venue;

        // TODO: set ui (venue information)

        // Send get info request
        RestogramClient.getInstance().getInfo(venue.getId(), this);
    }

    private RestogramVenue venue; // Venue object
}
