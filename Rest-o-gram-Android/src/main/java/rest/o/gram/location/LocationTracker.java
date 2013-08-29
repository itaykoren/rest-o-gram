package rest.o.gram.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibraryConstants;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.common.Defs;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 19/04/13
 */
public class LocationTracker implements ILocationTracker {

    /**
     * Ctor
     */
    public LocationTracker(Context context) {
        this.context = context;

        LocationLibrary.showDebugOutput(RestogramClient.getInstance().isDebuggable());

        try
        {
            LocationLibrary.startAlarmAndListener(context);
            if (Defs.Location.INTENSE_LOCATION_UPDATES)
                LocationLibrary.initialiseLibrary(context, true, "rest.o.gram");
            else // uses defined intervals
                LocationLibrary.initialiseLibrary(context, Defs.Location.LOCATION_UPDATE_INTERVAL,
                        Defs.Location.MAX_LOCATION_AGE, "rest.o.gram");
            canDetectLocation = true;
        }
        catch (UnsupportedOperationException e)
        {
            if (RestogramClient.getInstance().isDebuggable())
                Log.d("REST-O-GRAM", "cannot detect the current location");
            canDetectLocation = false;
        }
    }

    @Override
    public void force() {
        LocationLibrary.forceLocationUpdate(context);
    }

    @Override
    public void start() {
        if(isTracking)
            return;

        timer = new Timer();
        final TimerTask task = new TrackingTimeoutTimerTask(observer);
        timer.schedule(task, Defs.Location.TRACKING_TIMEOUT, Defs.Location.TRACKING_TIMEOUT);


        register(LocationLibraryConstants.getLocationChangedPeriodicBroadcastAction());
        register(LocationLibraryConstants.getLocationChangedTickerBroadcastAction());
        force(); // forces an initial update

        isTracking = true;
    }

    private void register(final String action) {
        final IntentFilter lflPeriodicIntentFilter =
                new IntentFilter(action);
        context.registerReceiver(locationBroadcastReceiver, lflPeriodicIntentFilter);
    }

    @Override
    public void stop() {
        if(!isTracking)
            return;

        isTracking = false;
        context.unregisterReceiver(locationBroadcastReceiver);
        observer = null;
        if (timer != null)
        {
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
        if(location != null)
            return location.lastLat;

        return 0.0;
    }

    @Override
    public double getLongitude() {
        if(location != null)
            return location.lastLong;

        return 0.0;
    }

    @Override
    public void dispose() {
        LocationLibrary.stopAlarmAndListener(context);
        timer = null;
    }

    private void informObserver() {
        observer.onLocationUpdated((double)location.lastLat, (double)location.lastLong,
                                   location.lastAccuracy);
    }

    private final BroadcastReceiver locationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            timer.cancel();
            // extract the location info in the broadcast
            location =
                    (LocationInfo) intent.getSerializableExtra(
                            LocationLibraryConstants.LOCATION_BROADCAST_EXTRA_LOCATIONINFO);
            if (RestogramClient.getInstance().isDebuggable())
                Log.d("REST-O-GRAM", "GOT LOCATION!!! LAT:" + location.lastLat + " LONG:" + location.lastLong + " ACCURACY: " + location.lastAccuracy);

            if(observer != null)
                LocationTracker.this.informObserver();
        }
    };

    private boolean isTracking = false;
    private Context context;
    private LocationInfo location;
    private ILocationObserver observer;
    private boolean canDetectLocation;
    private Timer timer;
}
