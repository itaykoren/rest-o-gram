package rest.o.gram.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibraryConstants;
import rest.o.gram.BuildConfig;
import rest.o.gram.R;
import rest.o.gram.RestogramPhoto;
import rest.o.gram.RestogramVenue;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.common.Defs;
import rest.o.gram.tasks.ITaskObserver;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 16/04/13
 */
public class HomeActivity extends Activity implements ITaskObserver {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home);
        initProgress("");
        isInit =  true;
    }

    @Override
    public void onResume() {
        super.onResume();

        final IntentFilter lftIntentFilter =
                new IntentFilter(LocationLibraryConstants.getLocationChangedPeriodicBroadcastAction());
        registerReceiver(locationBroadcastReceiver, lftIntentFilter);
        if (isInit)
        {
            LocationLibrary.forceLocationUpdate(this);
            isInit = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        cancelProgress();
    }

    @Override
    public void onFinished(RestogramVenue[] venues) {
        updateProgress = false;

        if(venues == null || venues.length == 0)
        {
            // Switch to "NearbyActivity" with parameters: "latitude", "longitude"
            Intent intent = new Intent(this, NearbyActivity.class);
            intent.putExtra("latitude", (double)location.lastLat);
            intent.putExtra("longitude", (double)location.lastLong);
            startActivityForResult(intent, Defs.RequestCodes.RC_NEARBY);
            return;
        }

        venue = venues[0]; // TODO: fix

        // Send get info request
        RestogramClient.getInstance().getInfo(venue.getId(), this);
    }

    @Override
    public void onFinished(RestogramVenue venue) {
        // Set image url to current venue member object
        this.venue.setImageUrl(venue.getImageUrl());

        // Switch to "VenueActivity" with parameter "venue"
        Intent intent = new Intent(this, VenueActivity.class);
        intent.putExtra("venue", this.venue);
        startActivityForResult(intent, Defs.RequestCodes.RC_VENUE);
    }

    @Override
    public void onFinished(RestogramPhoto[] photos) {
        // Empty
    }

    private void initProgress(String message) {
        ProgressBar pb = (ProgressBar)findViewById(R.id.pbLoading);
        pb.setProgress(0);

        updateProgress = true;

        // Start update progress operation in a background thread
        new Thread(new Runnable() {
            public void run() {
                while (updateProgress) {
                    // Update the progress bar
                    handler.post(new Runnable() {
                        public void run() {
                            incrementProgress("");
                        }
                    });

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        // Empty
                    }
                }
            }
        }).start();
    }

    private void incrementProgress(String message) {
        ProgressBar pb = (ProgressBar)findViewById(R.id.pbLoading);

        int newProgress = pb.getProgress() + 1;
        if(newProgress == pb.getMax())
            newProgress = 0;

        pb.setProgress(newProgress);
    }

    private void cancelProgress() {
        ProgressBar pb = (ProgressBar)findViewById(R.id.pbLoading);
        pb.setVisibility(View.GONE);
    }

    private double getSearchRadius(LocationInfo location) {
        if (location.originProvider == null ||
            location.originProvider  == LocationManager.NETWORK_PROVIDER)
            return Defs.Location.DEFAULT_NEARBY_RADIUS;
        else // if location.originProvider  == LocationManager.GPS_PROVIDER
            return Defs.Location.DEFAULT_FINDME_RADIUS;
    }

    private final BroadcastReceiver locationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // extract the location info in the broadcast
            location =
                    (LocationInfo) intent.getSerializableExtra(
                            LocationLibraryConstants.LOCATION_BROADCAST_EXTRA_LOCATIONINFO);
            if (BuildConfig.DEBUG)
                Log.d("REST-O-GRAM", "GOT LOCATION!!! LAT:" + location.lastLat + " LONG:" + location.lastLong);
            RestogramClient.getInstance().getNearby((double)location.lastLat, (double)location.lastLong,
                                                    getSearchRadius(location), HomeActivity.this);
        }
    };

    private LocationInfo location;
    private Handler handler = new Handler();
    private boolean updateProgress = false;
    private boolean isInit = false;
    private RestogramVenue venue;
}
