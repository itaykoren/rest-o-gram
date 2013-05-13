package rest.o.gram.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import rest.o.gram.R;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.common.Defs;
import rest.o.gram.view.VenueViewAdapter;
import rest.o.gram.entities.RestogramVenue;
import rest.o.gram.tasks.results.GetInfoResult;
import rest.o.gram.tasks.results.GetNearbyResult;
import rest.o.gram.tasks.results.GetPhotosResult;
import rest.o.gram.tasks.ITaskObserver;

import static rest.o.gram.location.Utils.distance;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 16/04/13
 */
public class NearbyActivity extends Activity implements ITaskObserver {

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
    public void onFinished(GetNearbyResult result) {
        if(result.getVenues() == null)
            return;

        addVenues(result.getVenues());
    }

    @Override
    public void onFinished(GetInfoResult result) {
        // TODO: implementation
    }

    @Override
    public void onFinished(GetPhotosResult result) {
        // TODO: implementation
    }

    private void addVenues(RestogramVenue[] venues) {
        // Traverse given venues
        for(final RestogramVenue venue : venues) {

            // TODO: get photo
            // Send get info request
            //RestogramClient.getInstance().getInfo(venue.getId(), this);

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
