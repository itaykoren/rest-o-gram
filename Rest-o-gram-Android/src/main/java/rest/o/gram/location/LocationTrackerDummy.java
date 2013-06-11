package rest.o.gram.location;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Looper;
import android.os.Handler;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 17/04/13
 */
public class LocationTrackerDummy implements ILocationTracker {

    @Override
    public void force() {
    }

    @Override
    public void start() {
        if(isTracking)
            stop();

        timer = new Timer();
        task = new GetLocationTimerTask();

        isTracking = true;
        timer.schedule(task, 2000);
    }

    @Override
    public void stop() {
        if(!isTracking)
            return;

        timer.cancel();
        isTracking = false;
    }

    @Override
    public void setObserver(ILocationObserver observer) {
        this.observer = observer;
    }

    @Override
    public boolean canDetectLocation() {
        return true;
    }

    @Override
    public double getLatitude() {
        return location[0];
    }

    @Override
    public double getLongitude() {
        return location[1];
    }

    class GetLocationTimerTask extends TimerTask {
        private Handler mHandler = new Handler(Looper.getMainLooper());

        @Override
        public void run() {
            // ...
            mHandler.post(new Runnable() {
                public void run() {
                    observer.onLocationUpdated(location[0], location[1], 0);
                    stop();
                }
            });
        }
    }

    private ILocationObserver observer;
    private Timer timer;
    private GetLocationTimerTask task;
    private boolean isTracking = false;

    private double[] location = {32.063553, 34.773078}; // Tel Aviv
    //private double[] location = {40.758891, -73.98515}; // New York
    //private double[] location = {48.853015, 2.368884}; // Paris
    //private double[] location = {41.891344, 12.468629}; // Rome
    //private double[] location = {43.325178, -37.880859}; // Atlantic Ocean
}