package rest.o.gram.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import rest.o.gram.R;
import rest.o.gram.activities.visitors.IActivityVisitor;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.common.Defs;
import rest.o.gram.common.IRestogramListener;
import rest.o.gram.common.Utils;
import rest.o.gram.data_history.IDataHistoryManager;
import rest.o.gram.entities.RestogramVenue;
import rest.o.gram.location.ILocationTracker;
import rest.o.gram.tasks.results.GetNearbyResult;
import rest.o.gram.view.VenueViewAdapter;

import static rest.o.gram.location.Utils.distance;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 16/04/13
 */
public class NearbyActivity extends RestogramActionBarActivity implements IRestogramListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.nearby);

        // Init venue list view
        ListView lv = (ListView)findViewById(R.id.lvVenues);
        viewAdapter = new VenueViewAdapter(this, this);
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
                    addVenues(cache.loadVenues());
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
    protected void onDestroy() { // Activity exiting
        super.onDestroy();
    }

    @Override
    public void onFinished(GetNearbyResult result) {
        super.onFinished(result);

        if(result.getVenues() == null)
            return;

        addVenues(result.getVenues());
    }

    @Override
    public void onVenueSelected(RestogramVenue venue) {
        // Switch to "VenueActivity" with parameter "venue"
        Intent intent = new Intent(this, VenueActivity.class);
        intent.putExtra("venue", venue.getFoursquare_id());
        Utils.changeActivity(this, intent, Defs.RequestCodes.RC_VENUE, false);
    }

    @Override
    public void accept(IActivityVisitor visitor) {
        visitor.visit(this);
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
            viewAdapter.addVenue(venue.getFoursquare_id());
        }

        viewAdapter.refresh();
    }

    private double latitude; // Latitude
    private double longitude; // Longitude
    private VenueViewAdapter viewAdapter; // View adapter
}
