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

    public LocationTrackerDummy() {
        isTracking = false;
        timer = new Timer();
        task = new GetLocationTimerTask();
    }

    @Override
    public void start() {
        if(isTracking)
            stop();

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
                    observer.onLocationUpdated(32.078145,34.781449); // Vitrina
                    stop();
                }
            });
        }
    }

    private ILocationObserver observer;
    private Timer timer;
    private GetLocationTimerTask task;
    private boolean isTracking;
}