package rest.o.gram.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import rest.o.gram.R;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.common.Defs;
import rest.o.gram.data_history.IDataHistoryManager;
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.entities.RestogramVenue;
import rest.o.gram.filters.RestogramFilterType;
import rest.o.gram.location.ILocationTracker;
import rest.o.gram.tasks.ITaskObserver;
import rest.o.gram.tasks.results.*;
import rest.o.gram.view.PhotoViewAdapter;

/**
 * Created with IntelliJ IDEA.
 * User: Itay
 * Date: 01/06/13
 */
public class ExploreActivity extends RestogramActionBarActivity implements ITaskObserver {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.explore);

        // Get location parameters
        try {
            Intent intent = getIntent();

            if (!intent.hasExtra("latitude") && !intent.hasExtra("longitude")) {
                // Get last location
                ILocationTracker tracker = RestogramClient.getInstance().getLocationTracker();
                if (tracker != null) {
                    latitude = tracker.getLatitude();
                    longitude = tracker.getLongitude();
                }

                // Load venues from cache
                IDataHistoryManager cache = RestogramClient.getInstance().getCacheDataHistoryManager();
                if(cache != null) {
                    venues = cache.loadVenues();
                    initialize();
                }

            } else {
                latitude = intent.getDoubleExtra("latitude", 0.0);
                longitude = intent.getDoubleExtra("longitude", 0.0);

                // Send get nearby request
                RestogramClient.getInstance().getNearby(latitude, longitude, Defs.Location.DEFAULT_NEARBY_RADIUS, this);
            }
        } catch (Exception e) {
            // TODO: implementation
            return;
        }
    }

    @Override
    protected void onDestroy() { // Activity exiting
        super.onDestroy();

        if (viewAdapter != null)
            viewAdapter.clear();
    }

    @Override
    public void onFinished(GetNearbyResult result) {
        venues = result.getVenues();
        if (venues == null || venues.length == 0)
            return;

        IDataHistoryManager cache = RestogramClient.getInstance().getCacheDataHistoryManager();
        if(cache != null) {
            // Reset cache
            cache.clear();

            // Save to cache
            for(final RestogramVenue venue : venues) {
                cache.save(venue, Defs.Data.SortOrder.SortOrderFIFO);
            }
        }

        initialize();
    }

    @Override
    public void onFinished(GetInfoResult result) {
        // Empty
    }

    @Override
    public void onFinished(GetPhotosResult result) {

        if (result == null)
            return;

        if (RestogramClient.getInstance().isDebuggable())
            Log.d("REST-O-GRAM", "Adding " + result.getPhotos().length + " photos");

        // Add new photos
        addPhotos(result.getPhotos());

        // Update last token of current venue
        tokens[currVenueIndex] = result.getToken();

        // Update request pending flag
        isRequestPending = false;
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


    private void initialize() {
        if (venues == null || venues.length == 0)
            return;

        RestogramVenue firstVenue = venues[0];

        // Init tokens array
        tokens = new String[venues.length];

        // Init photo grid view
        GridView gv = (GridView) findViewById(R.id.gvPhotos);
        viewAdapter = new PhotoViewAdapter(this, Defs.Photos.THUMBNAIL_WIDTH, Defs.Photos.THUMBNAIL_HEIGHT);
        gv.setAdapter(viewAdapter);

        // Set scroll listener
        gv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // Empty
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (totalItemCount == 0)
                    return;

                // Check whether the last view is visible
                if (++firstVisibleItem + visibleItemCount > totalItemCount) {
                    onScrollBottom();
                }
            }
        });

        // Send get photos request for first venue
        RestogramClient.getInstance().getPhotos(firstVenue.getFoursquare_id(), RestogramFilterType.Simple, this);
    }

    private void addPhotos(RestogramPhoto[] photos) {
        // Traverse given photos
        for (final RestogramPhoto photo : photos) {
            // Download image
            RestogramClient.getInstance().downloadImage(photo.getThumbnail(), photo, viewAdapter, false, null);
        }
    }

    private void onScrollBottom() {

        if (isRequestPending)
            return;

        isRequestPending = true;
        if (RestogramClient.getInstance().isDebuggable())
            Log.d("REST-O-GRAM", "Requesting photos for next venue");

        RestogramVenue nextVenue = getNextVenue(venues);

        String currToken = tokens[currVenueIndex];

        // if we already got photos from this location, get the next ones
        if (currToken != null) {
            RestogramClient.getInstance().getNextPhotos(currToken, RestogramFilterType.Simple, this);
        } else {
            RestogramClient.getInstance().getPhotos(nextVenue.getFoursquare_id(), RestogramFilterType.Simple, this);
        }
    }

    private RestogramVenue getNextVenue(RestogramVenue[] venues) {

        currVenueIndex = (currVenueIndex + 1) % venues.length;
        return venues[currVenueIndex];

    }

    private double latitude; // Latitude
    private double longitude; // Longitude
    private PhotoViewAdapter viewAdapter; // View adapter
    private boolean isRequestPending = false;
    private RestogramVenue[] venues;
    private String[] tokens;
    private int currVenueIndex = 0;
}
