package rest.o.gram.location;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import rest.o.gram.common.Defs;
import rest.o.gram.common.Utils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 6/13/13
 */
public class LocationTrackerGoogle implements ILocationTracker,
                                    GooglePlayServicesClient.ConnectionCallbacks,
                                    GooglePlayServicesClient.OnConnectionFailedListener,
                                    LocationListener {
    /**
     * Ctor
     */
    public LocationTrackerGoogle(Context context) {
        // Check for play services
        if(!Utils.isPlayServicesAvailable(context))
            return;

        canDetectLocation = true;

        client = new LocationClient(context, this, this);
        client.connect();
    }

    @Override
    public void force() {
        if(!client.isConnected())
            return;

        onLocationChanged(client.getLastLocation());
    }

    @Override
    public void start() {
        isStarted = true;

        if(!client.isConnected()) {
            return;
        }

        run();
    }

    @Override
    public void stop() {
        isStarted = false;
        client.removeLocationUpdates(this);

        if(timer != null) {
            timer.cancel();
            timer.purge();
        }
    }

    @Override
    public void setObserver(ILocationObserver observer) {
        this.observer = observer;
    }

    @Override
    public boolean canDetectLocation() {
        return canDetectLocation;
    }

    @Override
    public double getLatitude() {
        return latitude;
    }

    @Override
    public double getLongitude() {
        return longitude;
    }

    @Override
    public void onConnected(Bundle bundle) {
        canDetectLocation = true;

        if(isStarted) {
            run();
        }
    }

    @Override
    public void onDisconnected() {
        canDetectLocation = false;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        canDetectLocation = false;
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location == null)
            return;

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        final int accuracy = (int)location.getAccuracy();

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                observer.onLocationUpdated(latitude, longitude, accuracy);
            }
        });
    }

    /**
     * Sends location request
     */
    private void run() {
        timer = new Timer();
        final TimerTask task = new TrackingTimeoutTimerTask(observer);
        timer.schedule(task, Defs.Location.TRACKING_TIMEOUT);

        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        client.requestLocationUpdates(request, this);
    }

    private ILocationObserver observer;
    private boolean canDetectLocation = false;
    private boolean isStarted = false;
    private LocationClient client;
    private double latitude;
    private double longitude;
    private Timer timer;
}
