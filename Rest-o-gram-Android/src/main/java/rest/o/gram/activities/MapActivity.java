package rest.o.gram.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.*;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import rest.o.gram.R;
import rest.o.gram.activities.visitors.IActivityVisitor;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.common.Defs;
import rest.o.gram.common.Utils;
import rest.o.gram.data_history.IDataHistoryManager;
import rest.o.gram.entities.RestogramVenue;
import rest.o.gram.location.ILocationTracker;
import rest.o.gram.tasks.results.GetNearbyResult;
import rest.o.gram.view.GenericPopupView;
import rest.o.gram.view.IPopupView;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 29/05/13
 */
public class MapActivity extends RestogramActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Restart application (if needed)
        if(Utils.restartIfNeeded(this))
            return;

        // Check google play services
        isAvailable = Utils.isPlayServicesAvailable(this);
        if(!isAvailable) {
            Toast.makeText(this, "Error: failed to load map", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Google play services is unavailable", Toast.LENGTH_LONG).show();
            return;
        }

        setContentView(R.layout.map);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check google play services
        if(!isAvailable)
            return;

        if(Defs.Flow.WELCOME_SCREENS_ENABLED) {
            if(Utils.isShowWelcomeScreen(this)) {
                showWelcomeScreen();
            }
        }

        // Initialize map if needed
        if(!isMapReady())
            initializeMap();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Set is request pending flag to false
        isRequestPending = false;
    }

    @Override
    protected void onDestroy() { // Activity exiting
        super.onDestroy();

        // Clear map if needed
        if(isMapReady())
            map.clear();
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

        // Set is request pending flag to false
        isRequestPending = false;

        if(result == null || result.getVenues() == null) {
            Toast.makeText(this, "No restaurants found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Clear current data
        clear();

        // Add new venues
        addVenues(result.getVenues());

        // Update current marker
        if(currentMarkerOptions != null) {
            currentMarker = map.addMarker(currentMarkerOptions);
        }
    }

    @Override
    public void onCanceled() {
        super.onCanceled();

        // Set is request pending flag to false
        isRequestPending = false;
    }

    @Override
    public void onError() {
        super.onError();

        // Set is request pending flag to false
        isRequestPending = false;
    }

    @Override
    public void accept(IActivityVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Called after a venue was selected on map
     */
    private void onVenueSelected(String venueId) {
        // Switch to "VenueActivity" with parameter "venue"
        Intent intent = new Intent(this, VenueActivity.class);
        intent.putExtra("venue", venueId);
        Utils.changeActivity(this, intent, Defs.RequestCodes.RC_VENUE, false);
    }

    /**
     * Adds a marker for each given venue
     */
    private void addVenues(RestogramVenue[] venues) {
        if(venues == null)
            return;

        if(!isMapReady())
            return;

        // Traverse given venues
        for(final RestogramVenue venue : venues) {
            Marker m = map.addMarker(createMarker(venue.getLatitude(), venue.getLongitude(), venue.getName(), R.drawable.ic_map_venue));
            this.venues.put(m.getId(), venue.getFoursquare_id());
        }
    }

    /**
     *  Clears all data
     */
    private void clear() {
        if(!isMapReady())
            return;

        map.clear();
        venues.clear();
    }

    /**
     * Initializes map object
     * Returns true if successful, false otherwise
     */
    private boolean initializeMap() {
        // Load map
        MapLoader loader = new MapLoader();
        loader.loadMap();
        return true;
    }

    /**
     * Returns true whether map is ready, false otherwise
     */
    private boolean isMapReady() {
        return map != null;
    }

    /**
     * Attempts to load map
     */
    private void tryLoad() {
        if(map == null) {
            SupportMapFragment fragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
            if(fragment != null)
                map = fragment.getMap();
        }
    }

    /**
     * Called after map was loaded
     */
    private void onMapLoaded() {
        try {
            // Load venues from cache
            IDataHistoryManager cache = RestogramClient.getInstance().getCacheDataHistoryManager();
            if(cache != null) {
                addVenues(cache.loadVenues());

                // Load location
                double[] location = cache.loadLocation();
                if(location == null || location[0] == 0.0 || location[1] == 0.0) {
                    ILocationTracker tracker = RestogramClient.getInstance().getLocationTracker();
                    latitude = tracker.getLatitude();
                    longitude = tracker.getLongitude();
                }
                else {
                    latitude = location[0];
                    longitude = location[1];
                }
            }

            currentMarkerOptions = createMarker(latitude, longitude);
            currentMarker = map.addMarker(currentMarkerOptions);
        }
        catch(Exception e) {
            // TODO: implementation
            return;
        }

        // Update map according to location
        LatLng location = new LatLng(latitude, longitude);
        CameraUpdate update = CameraUpdateFactory.newLatLng(location);
        map.moveCamera(update); // Move to location
        update = CameraUpdateFactory.zoomTo(15);
        map.animateCamera(update); // Zoom to location

        // Set marker click listener
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String venueId = venues.get(marker.getId());
                if(venueId == null)
                    return false;

                onVenueSelected(venueId);
                return true;
            }
        });

        final MapActivity activity = this;
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if(isRequestPending)
                    return;

                // Set is request pending flag to true
                isRequestPending = true;

                // Update current marker
                if(currentMarker != null)
                    currentMarker.remove();
                currentMarkerOptions = createMarker(latLng.latitude, latLng.longitude);
                currentMarker = map.addMarker(currentMarkerOptions);

                // Send get nearby request
                RestogramClient.getInstance().getNearby(latLng.latitude, latLng.longitude, Defs.Location.DEFAULT_NEARBY_RADIUS, activity);

                // Show message
                Toast.makeText(activity, "Loading restaurants...", Toast.LENGTH_SHORT).show();
            }
        });

        // Enable user location display (with standard top right button)
        map.setMyLocationEnabled(true);
    }

    /**
     * Map Loader
     */
    private class MapLoader {
        public void loadMap() {
            timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    if(isMapReady()) {
                        // Cancel timer
                        cancel();

                        // Call on map loaded from main thread
                        handler.post(new Runnable() {
                            public void run() {
                                onMapLoaded();
                            }
                        });
                    }
                    else {
                        // Try loading map
                        tryLoad();
                    }
                }
                private Handler handler = new Handler(Looper.getMainLooper());
            };
            timer.schedule(task, 100, 1000);
        }

        private Timer timer;
    }

    /**
     * Creates a custom marker at given location with given text and resource
     */
    MarkerOptions createMarker(double latitude, double longitude, String text, int resource) {
        // Set location
        LatLng location = new LatLng(latitude, longitude);

        // Create paint object
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);

        // Calculate text bounds
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        // Create icon from given resource
        Bitmap icon = BitmapFactory.decodeResource(getResources(), resource);

        // Create canvas with proper width and height
        int width = Math.max(bounds.width(), icon.getWidth()) * 2;
        int height = bounds.height() * 3 + icon.getHeight();
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);

        // Draw text
        paint.setColor(Color.BLUE);
        paint.setTextSize(bounds.height() * 1.5f);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(text, width / 1.7f, bounds.height() * 1.5f, paint);

        // Draw icon
        canvas.drawBitmap(icon, width >> 1, bounds.height() * 2, paint);

        return new MarkerOptions()
                .position(location)
                .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                .anchor(0.5f, 1);
    }

    /**
     * Creates a default marker at the given location
     */
    MarkerOptions createMarker(double latitude, double longitude) {
        // Set location
        LatLng location = new LatLng(latitude, longitude);

        return new MarkerOptions()
                .position(location)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
    }

    private void showWelcomeScreen() {
        final Handler h = new Handler();
        final Activity activity = this;
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!Utils.isActivityValid(activity))
                    return;

                IPopupView popupView = new GenericPopupView(activity, R.layout.map_welcome, R.id.popup_map, 400, 350);
                popupView.open();
                Utils.setIsShowWelcomeScreen(activity, false);
            }
        }, 1000);
    }

    private double latitude; // Latitude
    private double longitude; // Longitude
    private GoogleMap map; // Map object
    private boolean isAvailable = false; // Is available flag
    private Map<String, String> venues = new HashMap<>(); // Venues map
    private Marker currentMarker; // Current marker
    private MarkerOptions currentMarkerOptions; // Current marker options
    private boolean isRequestPending = false; // Request pending flag
}
