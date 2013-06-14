package rest.o.gram.location;

import android.os.Handler;
import android.os.Looper;

import java.util.TimerTask;

/**
 * Created with IntelliJ IDEA.
 * User: Hen
 * Date: 14/06/13
 */
public class TrackingTimeoutTimerTask extends TimerTask {
    /**
     * Ctor
     */
    public TrackingTimeoutTimerTask(ILocationObserver observer) {
        this.observer = observer;
    }

    @Override
    public void run() {
        handler.post(new Runnable() {
            public void run() {
                observer.onTrackingTimedOut();
            }
        });
    }

    private ILocationObserver observer;
    private Handler handler = new Handler(Looper.getMainLooper());
}
