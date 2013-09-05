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
public class ExploreActivity extends RestogramActionBarActivity {

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
    public void onBackPressed() {
        if(RestogramClient.getInstance().getApplication().isInLastActivity())
            dialogManager.showExitAlert(this);
        else
            finish();
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

        if(result == null || !result.hasMorePhotos())
        {
            // Update last token of current venue
            venues[currVenueIndex].lastToken = null;

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

        final String venueId = venues[currVenueIndex].venueId;
        final String token = result.getToken();

        // Update last token of current venue
        venues[currVenueIndex].lastToken = token;
        updateToken(venueId, token);

        if (result.getPhotos().length < Defs.Feed.PHOTOS_PACKET_THRESHOLD && result.hasMorePhotos())
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
                if (totalItemCount == 0)
                    return;

                // Check whether enough views are visible
                final int n = (int)(totalItemCount * 0.75);
                if (++firstVisibleItem + visibleItemCount > n) {
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

        isRequestPending = true;

        if (RestogramClient.getInstance().isDebuggable())
            Log.d("REST-O-GRAM", "Requesting photos for next venue");

        final VenueData nextVenue = getNextVenue();
        final String nextVenueId = nextVenue.venueId;
        final String nextToken = nextVenue.lastToken;

        if(nextToken != null) {
            pendingCommand = RestogramClient.getInstance().getNextPhotos(nextToken, RestogramFilterType.Simple, nextVenueId, this);
        }
        else {
            pendingCommand = RestogramClient.getInstance().getPhotos(nextVenueId, RestogramFilterType.Simple, this);
        }
    }

    private void updateToken(String venueId, String token) {
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
                if(!Utils.isActivityValid(activity))
                    return;

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
    private IRestogramCommand pendingCommand;
    private VenueData[] venues;
    private int currVenueIndex = -1;
}
