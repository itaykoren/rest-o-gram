package rest.o.gram.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibraryConstants;
import rest.o.gram.BuildConfig;
import rest.o.gram.common.Defs;

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

        LocationLibrary.showDebugOutput(BuildConfig.DEBUG);
        if (Defs.Location.INTENSE_LOCATION_UPDATES)
            LocationLibrary.initialiseLibrary(context, true, "rest.o.gram");
        else // uses defined intervals
            LocationLibrary.initialiseLibrary(context, Defs.Location.LOCATION_UPDATE_INTERVAL,
                    Defs.Location.MAX_LOCATION_AGE, "rest.o.gram");
    }

    @Override
    public void start() {
        if(isTracking)
            return;

        final IntentFilter lftIntentFilter =
                new IntentFilter(LocationLibraryConstants.getLocationChangedPeriodicBroadcastAction());
        context.registerReceiver(locationBroadcastReceiver, lftIntentFilter);
        LocationLibrary.forceLocationUpdate(context);

        isTracking = true;
    }

    @Override
    public void stop() {
        if(!isTracking)
            return;

        isTracking = false;
        context.unregisterReceiver(locationBroadcastReceiver);
    }

    @Override
    public void setObserver(ILocationObserver observer) {
        this.observer = observer;
    }

    private final BroadcastReceiver locationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // extract the location info in the broadcast
            location =
                    (LocationInfo) intent.getSerializableExtra(
                            LocationLibraryConstants.LOCATION_BROADCAST_EXTRA_LOCATIONINFO);
            if(BuildConfig.DEBUG)
                Log.d("REST-O-GRAM", "GOT LOCATION!!! LAT:" + location.lastLat + " LONG:" + location.lastLong);

            if(observer != null)
                observer.onLocationUpdated((double)location.lastLat, (double)location.lastLong, location.originProvider);
        }
    };

    private boolean isTracking = false;
    private Context context;
    private LocationInfo location;
    private ILocationObserver observer;
}
