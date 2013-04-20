package rest.o.gram.location;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class LocationTrackerSimple extends Service implements ILocationTracker, LocationListener {

    /**
     * Ctor
     */
    public LocationTrackerSimple(Context context) {
        this.context = context;

        // Create timer
        timer = new Timer();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void start() {
        if(isTracking)
            return;

        try
        {
            // Get location manager
            locationManager = (LocationManager)context.getSystemService(LOCATION_SERVICE);

            if(locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER))
            {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                Log.d("GPS", "GPS Enabled");
            }

            if(locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            {
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                Log.d("Network", "Network Enabled");
            }

            // Start get location timer task
            TimerTask task = new GetLocationTimerTask();
            timer.schedule(task, 5000, 5000); // Execute every 5 seconds

            // Set tracking flag to true
            isTracking = true;

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        if(!isTracking)
            return;

        // Set tracking flag to false
        isTracking = false;

        try {
            // Remove location updates
            locationManager.removeUpdates(this);
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        // Cancel timer
        timer.cancel();
    }

    @Override
    public void setObserver(ILocationObserver observer) {
        this.observer = observer;
    }

    @Override
    public void onLocationChanged(Location location) {
        updateLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Empty
    }

    @Override
    public void onProviderEnabled(String provider) {
        // Empty
    }

    @Override
    public void onProviderDisabled(String provider) {
        // Empty
    }

    /**
     * Get location time task
     */
    private class GetLocationTimerTask extends TimerTask {
        @Override
        public void run() {
            handler.post(new Runnable() {
                public void run() {
                    if(lastLocation != null) { // TODO: minimal accuracy?
                        observer.onLocationUpdated(lastLocation.getLatitude(), lastLocation.getLongitude(), lastLocation.getProvider());
                        stop();
                    }
                }
            });
        }

        private Handler handler = new Handler(Looper.getMainLooper());
    }

    /**
     * Updates last location according to given location (if required)
     */
    private void updateLocation(Location location) {
        if(location == null)
            return;

        float accuracy = location.getAccuracy();
        long time = location.getTime();

        if((time > minTime && accuracy < bestAccuracy)) {
            lastLocation = location;
            bestAccuracy = accuracy;
            bestTime = time;
        }
        else if(time < minTime &&
                bestAccuracy == Float.MAX_VALUE && time > bestTime){
            lastLocation = location;
            bestTime = time;
        }
    }

    // Location observer
    private ILocationObserver observer;

    // Context object
    private Context context;

    // Timer object
    private Timer timer;

    // Flag for tracking state
    private boolean isTracking = false;

    // Last location
    private Location lastLocation = null;

    // The minimal distance to change between updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;

    // The minimal time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 0;

    // Location Manager
    private LocationManager locationManager;

    private long minTime = Calendar.getInstance().getTimeInMillis() - 30*MIN_TIME_BW_UPDATES;
    private float bestAccuracy = Float.MAX_VALUE;
    private long bestTime = minTime;
}
