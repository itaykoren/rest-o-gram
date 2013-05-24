package rest.o.gram.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import rest.o.gram.R;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.common.Defs;
import rest.o.gram.data.IDataHistoryManager;
import rest.o.gram.entities.RestogramVenue;
import rest.o.gram.location.ILocationTracker;
import rest.o.gram.tasks.ITaskObserver;
import rest.o.gram.tasks.results.GetInfoResult;
import rest.o.gram.tasks.results.GetNearbyResult;
import rest.o.gram.tasks.results.GetPhotosResult;
import rest.o.gram.view.VenueViewAdapter;

import static rest.o.gram.location.Utils.distance;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 16/04/13
 */
public class NearbyActivity extends RestogramActivity implements ITaskObserver {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.nearby);

        // Init venue list view
        ListView lv = (ListView)findViewById(R.id.lvVenues);
        viewAdapter = new VenueViewAdapter(this);
        lv.setAdapter(viewAdapter);

        // Get location parameters
        try {
            Intent intent = getIntent();

            if(!intent.hasExtra("latitude") && !intent.hasExtra("longitude")) {
                // Get last location
                ILocationTracker tracker = RestogramClient.getInstance().getLocationTracker();
                if (tracker != null) {
                    latitude = tracker.getLatitude();
                    longitude = tracker.getLongitude();
                }

                // Load venues from cache
                IDataHistoryManager cache = RestogramClient.getInstance().getCacheDataHistoryManager();
                if(cache != null) {
                    addVenues(cache.loadVenues(Defs.Data.SortOrder.SortOrderFIFO));
                }
            }
            else {
                latitude = intent.getDoubleExtra("latitude", 0.0);
                longitude = intent.getDoubleExtra("longitude", 0.0);

                // Send get nearby request
                RestogramClient.getInstance().getNearby(latitude, longitude, Defs.Location.DEFAULT_NEARBY_RADIUS, this);
            }
        }
        catch(Exception e) {
            // TODO: implementation
            return;
        }
    }

    @Override
    protected void onDestroy() { // Application exiting
        super.onDestroy();

        // Dispose client
        RestogramClient.getInstance().dispose();
    }

    @Override
    public void onFinished(GetNearbyResult result) {
        if(result.getVenues() == null)
            return;

        IDataHistoryManager cache = RestogramClient.getInstance().getCacheDataHistoryManager();
        if(cache != null) {
            // Reset cache
            cache.clear();

            // Save to cache
            for(final RestogramVenue venue : result.getVenues()) {
                cache.save(venue);
            }
        }

        addVenues(result.getVenues());
    }

    @Override
    public void onFinished(GetInfoResult result) {
        // Empty
    }

    @Override
    public void onFinished(GetPhotosResult result) {
        // Empty
    }

    @Override
    public void onCanceled() {
        // Empty
    }

    private void addVenues(RestogramVenue[] venues) {
        // Traverse given venues
        for(final RestogramVenue venue : venues) {

            // TODO: get photo
            // Send get info request
            //RestogramClient.getInstance().getInfo(venue.getFoursquare_id(), this);

            // Calculate distance
            double d = distance(latitude, longitude, venue.getLatitude(), venue.getLongitude());
            if(d != 0.0)
                venue.setDistance(d);

            // Add venue
            viewAdapter.addVenue(venue);
        }

        viewAdapter.refresh();
    }

    public void onVenueClicked(RestogramVenue venue) {
        // Switch to "VenueActivity" with parameter "venue"
        Intent intent = new Intent(this, VenueActivity.class);
        intent.putExtra("venue", venue);
        startActivityForResult(intent, Defs.RequestCodes.RC_VENUE);
    }

    private double latitude; // Latitude
    private double longitude; // Longitude
    private VenueViewAdapter viewAdapter; // View adapter
}
