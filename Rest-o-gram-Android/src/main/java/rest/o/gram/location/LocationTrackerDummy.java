package rest.o.gram.location;

import java.util.Timer;
import java.util.TimerTask;

import android.location.LocationManager;
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
                    observer.onLocationUpdated(location[0], location[1], 0, LocationManager.GPS_PROVIDER);
                    stop();
                }
            });
        }
    }

    private ILocationObserver observer;
    private Timer timer;
    private GetLocationTimerTask task;
    private boolean isTracking = false;

    private double[] location = {32.078145, 34.781449}; // Vitrina
    //private double[] location = {32.10732, 34.834299}; // Frame
}