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

    class GetLocationTimerTask extends TimerTask {
        private Handler mHandler = new Handler(Looper.getMainLooper());

        @Override
        public void run() {
            // ...
            mHandler.post(new Runnable() {
                public void run() {
                    observer.onLocationUpdated(32.078145,34.781449, LocationManager.GPS_PROVIDER); // Vitrina
                    //observer.onLocationUpdated(32.095,34.782468, LocationManager.GPS_PROVIDER); // Near my house
                    stop();
                }
            });
        }
    }

    private ILocationObserver observer;
    private Timer timer;
    private GetLocationTimerTask task;
    private boolean isTracking = false;
}