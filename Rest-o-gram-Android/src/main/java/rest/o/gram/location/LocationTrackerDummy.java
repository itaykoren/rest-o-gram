package rest.o.gram.location;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 17/04/13
 */
public class LocationTrackerDummy implements ILocationTracker {

    public LocationTrackerDummy() {
        isTracking = false;
        timer = new Timer();
    }

    @Override
    public void start() {
        if(isTracking)
            stop();

        TimerTask task = new TimerTask(){
            @Override
            public void run() {
                observer.onLocationUpdated(32.078145,34.781449); // Vitrina
                stop();
            }
        };

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

    private ILocationObserver observer;
    private Timer timer;
    private boolean isTracking;
}
