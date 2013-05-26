package rest.o.gram.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import rest.o.gram.R;
import rest.o.gram.entities.RestogramVenue;
import rest.o.gram.common.Defs;
import rest.o.gram.tasks.results.*;
import rest.o.gram.tasks.ITaskObserver;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 16/04/13
 */
public class FindMeActivity extends RestogramActivity implements ITaskObserver {

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
    public void onFinished(GetNearbyResult result) {
        // Empty
    }

    @Override
    public void onFinished(GetInfoResult venue) {
        // Empty
    }

    @Override
    public void onFinished(GetPhotosResult result) {
        // Empty
    }

    @Override
    public void onFinished(CachePhotoResult result) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onFinished(FetchPhotosFromCacheResult result) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onFinished(CacheVenueResult result) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onFinished(FetchVenuesFromCacheResult result) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onCanceled() {
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

        // TODO: set ui (venue information, image, etc)
    }

    private RestogramVenue venue; // Venue object
}
