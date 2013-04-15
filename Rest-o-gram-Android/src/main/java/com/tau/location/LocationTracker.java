package com.tau.location;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.util.Calendar;

public class LocationTracker extends Service implements LocationListener {

    private final Context mContext;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    Location location = null; // location
    double latitude  = Double.NaN; // latitude
    double longitude = Double.NaN ; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 20 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 10 * 1; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;

    public LocationTracker(Context context) {
        this.mContext = context;
        init();
    }

    protected void init() {
        try
        {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            this.canGetLocation = true;
            if (isNetworkEnabled)
            {
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                Log.d("Network", "Network Enabled");
                if (locationManager != null)
                    onLocationChanged(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
            }

            // if GPS Enabled get lat/long using GPS Services
            if (isGPSEnabled)
            {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("GPS", "GPS Enabled");
                    if (locationManager != null)
                        onLocationChanged(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopTracking() {
        if (locationManager != null) {
            locationManager.removeUpdates(LocationTracker.this);
        }
    }

    /**
     * Function to get latitude
     * */
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     * */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public boolean isLocationValid() {
        return !Double.isNaN(latitude) && !Double.isNaN(longitude);
    }

    private long minTime = Calendar.getInstance().getTimeInMillis() - 30*MIN_TIME_BW_UPDATES;
    private float bestAccuracy = Float.MAX_VALUE;
    private long bestTime = minTime;

    @Override
    public void onLocationChanged(Location location) {
        if (location != null)
        {
            float accuracy = location.getAccuracy();
            long time = location.getTime();

            if ((time > minTime && accuracy < bestAccuracy)) {
                this.location = location;
                bestAccuracy = accuracy;
                bestTime = time;
            }
            else if (time < minTime &&
                    bestAccuracy == Float.MAX_VALUE && time > bestTime){
                this.location = location;
                bestTime = time;
            }
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}
