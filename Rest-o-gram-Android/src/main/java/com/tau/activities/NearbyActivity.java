package com.tau.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
public class NearbyActivity extends Activity implements ITaskObserver {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setContentView(R.layout.nearby);

        Intent intent = getIntent();

        double latitude;
        double longitude;

        // Get location parameters
        try {
            latitude = intent.getDoubleExtra("latitude", 0.0);
            longitude = intent.getDoubleExtra("longitude", 0.0);
        }
        catch(Exception e) {
            // TODO: implementation
            return;
        }

        // Send get nearby request
        RestogramClient.getInstance().getNearby(latitude, longitude, Defs.Location.DEFAULT_NEARBY_RADIUS, this);
    }

    @Override
    public void onFinished(RestogramVenue[] venues) {
        // TODO: implementation
    }

    @Override
    public void onFinished(RestogramVenue venue) {
        // TODO: implementation
    }

    @Override
    public void onFinished(RestogramPhoto[] photos) {
        // TODO: implementation
    }
}
