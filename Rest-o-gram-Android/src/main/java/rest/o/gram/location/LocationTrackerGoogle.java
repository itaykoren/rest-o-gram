package rest.o.gram.location;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import rest.o.gram.common.Utils;

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

        client = new LocationClient(context, this, this);
        client.connect();
    }

    @Override
    public void force() {
        if(!canDetectLocation)
            return;

        onLocationChanged(client.getLastLocation());
    }

    @Override
    public void start() {
        if(!canDetectLocation)
            return;

        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        client.requestLocationUpdates(request, this);
    }

    @Override
    public void stop() {
        client.removeLocationUpdates(this);
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

        observer.onLocationUpdated(latitude, longitude, (int)location.getAccuracy());
    }

    private ILocationObserver observer;
    private boolean canDetectLocation = false;
    private LocationClient client;
    private double latitude;
    private double longitude;
}
