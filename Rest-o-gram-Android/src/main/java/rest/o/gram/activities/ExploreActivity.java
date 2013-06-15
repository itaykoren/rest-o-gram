package rest.o.gram.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.*;
import rest.o.gram.R;
import rest.o.gram.activities.visitors.IActivityVisitor;
import rest.o.gram.cache.IRestogramCache;
import rest.o.gram.cache.RestogramPhotos;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.common.Defs;
import rest.o.gram.common.Utils;
import rest.o.gram.data_history.IDataHistoryManager;
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.entities.RestogramVenue;
import rest.o.gram.filters.RestogramFilterType;
import rest.o.gram.location.ILocationTracker;
import rest.o.gram.tasks.results.*;
import rest.o.gram.view.GenericPopupView;
import rest.o.gram.view.IPopupView;
import rest.o.gram.view.PhotoViewAdapter;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Itay
 * Date: 01/06/13
 */
public class ExploreActivity extends RestogramActionBarActivity {

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
            }
            else {
                latitude = intent.getDoubleExtra("latitude", 0.0);
                longitude = intent.getDoubleExtra("longitude", 0.0);
            }

            // Load venues from cache
            IDataHistoryManager cache = RestogramClient.getInstance().getCacheDataHistoryManager();
            if(cache != null) {
                RestogramVenue[] venues = cache.loadVenues();
                if(venues != null && venues.length > 0) {
                    initialize(venues);
                }
            }
        } catch (Exception e) {
            // TODO: implementation
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(Defs.Flow.WELCOME_SCREENS_ENABLED) {
            if(Utils.isShowWelcomeScreen(this)) {
                showWelcomeScreen();
            }
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
        super.onFinished(result);

        final RestogramVenue[] venues = result.getVenues();
        if(venues == null || venues.length == 0)
            return;

        initialize(venues);
    }

    @Override
    public void onFinished(GetPhotosResult result) {
        super.onFinished(result);

        // Update request pending flag
        isRequestPending = false;

        if(result == null || result.getPhotos() == null) {
            // Update last token of current venue
            venues[currVenueIndex].lastToken = null;

            // Get photos from next venue (if possible)
            getMorePhotos();
            return;
        }

        if(RestogramClient.getInstance().isDebuggable())
            Log.d("REST-O-GRAM", "Adding " + result.getPhotos().length + " photos");

        // Add new photos
        addPhotos(result.getPhotos());

        final String venueId = venues[currVenueIndex].venueId;
        final String token = result.getToken();
        updateToken(venueId, token);

        if (result.getPhotos().length < Defs.Feed.PHOTOS_PACKET_THRESHOLD)
            getMorePhotos();
    }

    @Override
    public void accept(IActivityVisitor visitor) {
        visitor.visit(this);
    }

    private void initialize(RestogramVenue[] venues) {
        if(venues == null || venues.length == 0)
            return;

        showLocation();

        // Init venues array
        this.venues = new VenueData[venues.length];
        for(int i = 0; i < venues.length; i++) {
            this.venues[i] = new VenueData(venues[i].getFoursquare_id(), null, -1);
        }

        // Init photo grid view
        GridView gv = (GridView)findViewById(R.id.gvPhotos);
        viewAdapter = new PhotoViewAdapter(this);
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

        getMorePhotos();
    }

    private void addPhotos(RestogramPhoto[] photos) {
        // Traverse given photos
        for(final RestogramPhoto photo : photos) {
            // Download image
            RestogramClient.getInstance().downloadImage(photo.getThumbnail(), photo, viewAdapter, false, null);
        }
    }

    private void onScrollBottom() {
        getMorePhotos();
    }

    private void getMorePhotos() {
        if(isRequestPending)
            return;

        if (RestogramClient.getInstance().isDebuggable())
            Log.d("REST-O-GRAM", "Requesting photos for next venue");

        final VenueData nextVenue = getNextVenue();
        final String nextVenueId = nextVenue.venueId;
        final String nextToken = nextVenue.lastToken;
        final int lastPhotoIndex = nextVenue.lastPhotoIndex;

        // Try to load previous photos from cache
        IRestogramCache cache = RestogramClient.getInstance().getCache();
        RestogramPhotos venuePhotos = cache.findPhotos(nextVenueId);
        if(venuePhotos == null) { // No photos found
            isRequestPending = true;

            if(nextToken != null) {
                RestogramClient.getInstance().getNextPhotos(nextToken, RestogramFilterType.Complex, nextVenueId, this);
            }
            else {
                RestogramClient.getInstance().getPhotos(nextVenueId, RestogramFilterType.Complex, this);
            }
        }
        else { // Photos were found
            // Get all photos of this venue
            final List<RestogramPhoto> photos = venuePhotos.getPhotos();

            // Calculate index of first photo to download
            int startIndex = lastPhotoIndex + 1;

            // Calculate index of last photo to download
            int endIndex = Math.min(photos.size(), startIndex + maxPhotosPerVenue - 1);

            for(int i = startIndex; i <= endIndex; i++) {
                try {
                    RestogramPhoto photo = photos.get(i);

                    // Download image
                    RestogramClient.getInstance().downloadImage(photo.getThumbnail(), photo, viewAdapter, false, null);
                }
                catch(Exception e) {
                    // Empty
                }
            }
            nextVenue.lastPhotoIndex = endIndex;
        }
    }

    private void updateToken(String venueId, String token) {
        // Update last token of current venue
        venues[currVenueIndex].lastToken = token;

        // Update last token in cache
        IRestogramCache cache = RestogramClient.getInstance().getCache();
        RestogramPhotos venuePhotos = cache.findPhotos(venueId);
        if(venuePhotos != null)
            venuePhotos.setToken(token);
    }

    private VenueData getNextVenue() {
        currVenueIndex = (currVenueIndex + 1) % venues.length;
        return venues[currVenueIndex];
    }

    private void showWelcomeScreen() {
        final Handler h = new Handler();
        final Activity activity = this;
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                IPopupView popupView = new GenericPopupView(activity, R.layout.explore_welcome, R.id.popup_explore, 400, 350);
                popupView.open();
                Utils.setIsShowWelcomeScreen(activity, false);
            }
        }, 1000);
    }

    private void showLocation() {
        final Activity activity = this;

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                try {
                    String address = (String)message.obj;
                    Toast.makeText(activity, "Exploring restaurant photos near: \n" + address, Toast.LENGTH_LONG).show();
                }
                catch (Exception e) {
                    // Empty
                }
            }
        };

        Thread thread = new Thread() {
            @Override
            public void run() {
                String address = Utils.getAddress(activity, latitude, longitude);
                if(address != null) {
                    Message message = handler.obtainMessage(1, address);
                    handler.sendMessage(message);
                }
            }
        };
        thread.start();
    }

    private class VenueData {
        private VenueData(String venueId, String lastToken, int lastPhotoIndex) {
            this.venueId = venueId;
            this.lastToken = lastToken;
            this.lastPhotoIndex = lastPhotoIndex;
        }

        public String venueId;
        public String lastToken;
        public int lastPhotoIndex;
    }

    private double latitude; // Latitude
    private double longitude; // Longitude
    private PhotoViewAdapter viewAdapter; // View adapter
    private boolean isRequestPending = false;
    private VenueData[] venues;
    private int currVenueIndex = -1;
    private final int maxPhotosPerVenue = 15;
}
