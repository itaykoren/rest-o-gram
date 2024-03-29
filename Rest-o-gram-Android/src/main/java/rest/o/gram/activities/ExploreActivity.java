package rest.o.gram.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.Toast;
import rest.o.gram.R;
import rest.o.gram.activities.visitors.IActivityVisitor;
import rest.o.gram.cache.IRestogramCache;
import rest.o.gram.cache.RestogramPhotos;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.commands.IRestogramCommand;
import rest.o.gram.common.Defs;
import rest.o.gram.common.Utils;
import rest.o.gram.data_history.IDataHistoryManager;
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.entities.RestogramVenue;
import rest.o.gram.filters.RestogramFilterType;
import rest.o.gram.location.ILocationTracker;
import rest.o.gram.shared.CommonDefs;
import rest.o.gram.tasks.results.GetNearbyResult;
import rest.o.gram.tasks.results.GetPhotosResult;
import rest.o.gram.view.GenericPopupView;
import rest.o.gram.view.IPopupView;
import rest.o.gram.view.PhotoViewAdapter;

/**
 * Created with IntelliJ IDEA.
 * User: Itay
 * Date: 01/06/13
 */
public class ExploreActivity extends RestogramMainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Restart application (if needed)
        if(Utils.restartIfNeeded(this))
            return;

        setContentView(R.layout.explore);

        try {
            // Load venues and location from cache
            IDataHistoryManager cache = RestogramClient.getInstance().getCacheDataHistoryManager();
            if(cache != null) {
                double[] location = cache.loadLocation();
                if(location != null) {
                    latitude = location[0];
                    longitude = location[1];
                }

                RestogramVenue[] venues = cache.loadVenues();
                if(venues != null && venues.length > 0) {
                    initialize(venues);
                }
            }
            else {
                // Get last location
                ILocationTracker tracker = RestogramClient.getInstance().getLocationTracker();
                if (tracker != null) {
                    latitude = tracker.getLatitude();
                    longitude = tracker.getLongitude();
                }
            }
        } catch (Exception e) {
            // TODO: implementation
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(Defs.Flow.WELCOME_SCREENS_ENABLED) {
            if(Utils.isShowWelcomeScreen(this)) {
                showWelcomeScreen();
            }
        }
    }

    @Override
    protected void onDestroy() { // Activity exiting
        super.onDestroy();

        if(pendingCommand != null) {
            pendingCommand.cancel();
            pendingCommand = null;
        }

        if (viewAdapter != null)
            viewAdapter.clear();
    }

    @Override
    public void onFinished(GetNearbyResult result) {
        super.onFinished(result);

        if(result == null)
            return;

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

        // Set pending command to null
        pendingCommand = null;

        if(result == null)
        {
            // Update last token of current venue
            updateToken(CommonDefs.Tokens.FINISHED_FETCHING_FROM_INSTAGRAM);

            if(RestogramClient.getInstance().isDebuggable())
                Log.d("REST-O-GRAM", "Got null response");

            // Get photos from next venue (if possible)
            getMorePhotos();
            return;
        }

        if(RestogramClient.getInstance().isDebuggable())
            Log.d("REST-O-GRAM", "Adding " + result.getPhotos().length + " photos");

        // Add new photos
        addPhotos(result.getPhotos());

        // Update last token of current venue
        updateToken(result.getToken());

        if (result.getPhotos().length < Defs.Feed.PHOTOS_PACKET_THRESHOLD)
            getMorePhotos();
    }

    @Override
    public void onCanceled() {
        super.onCanceled();

        // Update request pending flag
        isRequestPending = false;

        // Set pending command to null
        pendingCommand = null;
    }

    @Override
    public void onError() {
        super.onError();

        // Update request pending flag
        isRequestPending = false;

        // Set pending command to null
        pendingCommand = null;
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
            this.venues[i] = new VenueData(venues[i].getFoursquare_id(), null);
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
                if(totalItemCount == 0)
                    return;

                // Check whether all photos are visible
                if((firstVisibleItem + visibleItemCount >= totalItemCount)
                        && !isRequestPending
                        && !hasMorePhotos
                        && showNoMorePhotosMessage) {
                    showNoMorePhotosMessage = false;
                    Toast.makeText(ExploreActivity.this, R.string.explore_no_more_photos, Toast.LENGTH_LONG).show();
                }

                // Check whether enough views are visible
                if(++firstVisibleItem + visibleItemCount > totalItemCount - 20) {
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
        if (isRequestPending)
            return;

        if (RestogramClient.getInstance().isDebuggable())
            Log.d("REST-O-GRAM", "Requesting photos for next venue");

        final VenueData nextVenue = getNextVenueWithPhotos();

        if (nextVenue != null) {

            final String nextVenueId = nextVenue.venueId;
            final String nextToken = nextVenue.lastToken;

            // Try to load previous photos from cache
            final IRestogramCache cache = RestogramClient.getInstance().getCache();
            final RestogramPhotos venuePhotos = cache.findPhotos(nextVenueId);

            if(nextToken == null) { // First search for photos for this venue
                if(venuePhotos == null) { // No photos found in cache
                    pendingCommand = RestogramClient.getInstance().getPhotos(nextVenueId, RestogramFilterType.Simple, this);
                    isRequestPending = true;
                }
                else { // Photos were found in cache
                    // Save last token
                    nextVenue.lastToken = venuePhotos.getFirstToken();

                    // Download first batch of photos
                    for(final RestogramPhoto photo : venuePhotos.getFirstPhotos()) {
                        // Download image
                        RestogramClient.getInstance().downloadImage(photo.getThumbnail(), photo, viewAdapter, false, null);
                    }
                }
            } else { // We received photos from this venue before, and there are more photos
                pendingCommand = RestogramClient.getInstance().getNextPhotos(nextToken, RestogramFilterType.Simple, nextVenueId, this);
                isRequestPending = true;
            }
        }
    }

    private VenueData getNextVenueWithPhotos() {

        for(int i = 0; i < venues.length; i++) {
            VenueData nextVenue = getNextVenue();
            String token = nextVenue.lastToken;
            if(token == null || !token.equals(CommonDefs.Tokens.FINISHED_FETCHING_FROM_INSTAGRAM))
                return nextVenue;
        }
        // no more venues with photos
        hasMorePhotos = false;
        return null;
    }

    private void updateToken(String token) {

        // get current venue, update its last token
        VenueData venue = venues[currVenueIndex];
        venue.lastToken = token;
        // update in cache as well
        updateTokenInCache(venue.venueId, token);
    }

    private void updateTokenInCache(String venueId, String token) {
        IRestogramCache cache = RestogramClient.getInstance().getCache();
        RestogramPhotos venuePhotos = cache.findPhotos(venueId);
        if (venuePhotos != null)
            venuePhotos.setToken(token);
    }

    private VenueData getNextVenue() {
        currVenueIndex = (currVenueIndex + 1) % venues.length;
        return venues[currVenueIndex];
    }

    private void showWelcomeScreen() {
        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!Utils.isActivityValid(ExploreActivity.this))
                    return;

                int w = (int)(Utils.getScreenDensity(ExploreActivity.this) * 200);
                int h = (int)(Utils.getScreenDensity(ExploreActivity.this) * 210);

                IPopupView popupView = new GenericPopupView(ExploreActivity.this, R.layout.explore_welcome, R.id.popup_explore, w, h);
                popupView.open();
                Utils.setIsShowWelcomeScreen(ExploreActivity.this, false);
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
        private VenueData(String venueId, String lastToken) {
            this.venueId = venueId;
            this.lastToken = lastToken;
        }

        public String venueId;
        public String lastToken;
    }

    private double latitude; // Latitude
    private double longitude; // Longitude
    private PhotoViewAdapter viewAdapter; // View adapter
    private boolean isRequestPending = false;
    private boolean showNoMorePhotosMessage = true; // No more photos message flag
    private boolean hasMorePhotos = true; // Has more photos flag
    private IRestogramCommand pendingCommand;
    private VenueData[] venues;
    private int currVenueIndex = -1;
}
