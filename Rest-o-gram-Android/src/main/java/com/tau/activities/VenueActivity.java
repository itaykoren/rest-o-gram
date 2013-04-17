package com.tau.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.tau.R;
import com.tau.RestogramPhoto;
import com.tau.RestogramVenue;
import com.tau.client.RestogramClient;
import com.tau.tasks.ITaskObserver;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 16/04/13
 */
public class VenueActivity extends Activity implements ITaskObserver {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.venue);

        Intent intent = getIntent();

        RestogramVenue venue;

        // Get venue parameter
        try {
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
        // TODO: set ui (venue image)
    }

    @Override
    public void onFinished(RestogramPhoto[] photos) {
        // TODO: set ui (photos)
    }

    public void onPhotoClicked(View view) {
        // Switch to "PhotoActivity" with parameter "photo"
        // Intent intent = new Intent(this, PhotoActivity.class);
        // intent.putExtra("photo", photo);
        // startActivityForResult(intent, Defs.RequestCodes.RC_PHOTO);
    }

    /**
     * Initializes using given venue
     */
    private void initialize(RestogramVenue venue) {
        this.venue = venue;

        // TODO: set ui (venue information)

        // Send get info request if needed
        if(venue.getImageUrl().isEmpty())
            RestogramClient.getInstance().getInfo(venue.getId(), this);

        // Send get photos request
        RestogramClient.getInstance().getPhotos(venue.getId(), this);
    }

    private RestogramVenue venue; // Venue object
}
