package rest.o.gram.activities;

import android.content.Intent;
import android.graphics.*;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import rest.o.gram.R;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.common.Defs;
import rest.o.gram.common.Utils;
import rest.o.gram.data_history.IDataHistoryManager;
import rest.o.gram.entities.RestogramVenue;
import rest.o.gram.location.ILocationTracker;
import rest.o.gram.tasks.results.*;

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

        // Initialize map
        if(!initializeMap())
            return;

        setContentView(R.layout.map);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Initialize map if needed
        if(!isMapReady())
            initializeMap();
    }

    @Override
    protected void onDestroy() { // Activity exiting
        super.onDestroy();

        // Clear map if needed
        if(isMapReady())
            map.clear();
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
                cache.save(venue, Defs.Data.SortOrder.SortOrderFIFO);
            }
        }

        addVenues(result.getVenues());
    }

    private void onVenueSelected(RestogramVenue venue) {
        // Switch to "VenueActivity" with parameter "venue"
        Intent intent = new Intent(this, VenueActivity.class);
        intent.putExtra("venue", venue);
        Utils.changeActivity(this, intent, Defs.RequestCodes.RC_VENUE, false);
    }

    private void addVenues(RestogramVenue[] venues) {
        if(!isMapReady())
            return;

        // Traverse given venues
        for(final RestogramVenue venue : venues) {
            Marker m = map.addMarker(createMarker(venue.getLatitude(), venue.getLongitude(), venue.getName(), R.drawable.ic_map_venue));
            this.venues.put(m.getId(), venue);
        }
    }

    /**
     * Initializes map object
     * Returns true if successful, false otherwise
     */
    private boolean initializeMap() {
        // Check google play services
        final int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(status == ConnectionResult.SERVICE_MISSING ||
                status == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED ||
                status == ConnectionResult.SERVICE_DISABLED) {
            Toast.makeText(this, "Error: google play services", Toast.LENGTH_LONG).show();
            return false;
        }

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
                RestogramVenue venue = venues.get(marker.getId());
                if(venue == null)
                    return false;

                onVenueSelected(venue);
                return true;
            }
        });

        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Point point = map.getProjection().toScreenLocation(latLng);
                // TODO: show popup - explore this area
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
        int height = bounds.height() * 2 + icon.getHeight();
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);

        // Draw text
        paint.setColor(Color.BLUE);
        paint.setTextSize(bounds.height());
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(text, width >> 1, bounds.height(), paint);

        // Draw icon
        canvas.drawBitmap(icon, width >> 1, bounds.height() * 2, paint);

        return new MarkerOptions()
                .position(location)
                .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                .anchor(0.5f, 1);
    }

    private double latitude; // Latitude
    private double longitude; // Longitude
    private GoogleMap map; // Map object
    private Map<String, RestogramVenue> venues = new HashMap<>(); // Venues map
}
