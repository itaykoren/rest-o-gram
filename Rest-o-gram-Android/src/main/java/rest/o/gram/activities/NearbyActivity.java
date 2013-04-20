package rest.o.gram.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import rest.o.gram.R;
import rest.o.gram.RestogramPhoto;
import rest.o.gram.RestogramVenue;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.common.Defs;
import rest.o.gram.common.Utils;
import rest.o.gram.common.ViewAdapter;
import rest.o.gram.tasks.ITaskObserver;

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
        viewAdapter = new ViewAdapter();
        lv.setAdapter(viewAdapter);

        // Get location parameters
        double latitude;
        double longitude;
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
    public void onFinished(RestogramVenue[] venues) {
        addVenues(venues);
    }

    @Override
    public void onFinished(RestogramVenue venue) {
        // TODO: implementation
    }

    @Override
    public void onFinished(RestogramPhoto[] photos) {
        // TODO: implementation
    }

    private void addVenues(RestogramVenue[] venues) {
        // Traverse given venues
        for(final RestogramVenue venue : venues) {
            // Create new image view
            VenueView vv = new VenueView(this, venue);
            vv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onVenueClicked(venue);
                }
            });

            // Add view
            viewAdapter.addView(vv);

            // TODO: get venue photo...
        }

        viewAdapter.refresh();
    }

    public void onVenueClicked(RestogramVenue venue) {
        // Switch to "VenueActivity" with parameter "venue"
        Intent intent = new Intent(this, VenueActivity.class);
        intent.putExtra("venue", venue);
        startActivityForResult(intent, Defs.RequestCodes.RC_VENUE);
    }

    /**
     * Created with IntelliJ IDEA.
     * User: Roi
     * Date: 20/04/13
     */
    private class VenueView extends View {
        private VenueView(Context context, RestogramVenue venue) {
            super(context);

            setContentView(R.layout.nearby_list_item);

            // Set UI with venue information
            Utils.updateTextView((TextView)findViewById(R.id.tvName), venue.getName());
            Utils.updateTextView((TextView)findViewById(R.id.tvAddress), venue.getAddress());
            Utils.updateTextView((TextView)findViewById(R.id.tvPhone), venue.getPhone());
        }
    }

    private ViewAdapter viewAdapter; // View adapter
}
