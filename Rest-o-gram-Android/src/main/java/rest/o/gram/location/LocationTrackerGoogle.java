package rest.o.gram.location;

import android.content.Context;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 6/13/13
 */
public class LocationTrackerGoogle implements ILocationTracker {
    /**
     * Ctor
     */
    public LocationTrackerGoogle(Context context) {
        this.context = context;
    }

    @Override
    public void force() {
        // TODO
    }

    @Override
    public void start() {
        // TODO
    }

    @Override
    public void stop() {
        // TODO
    }

    @Override
    public void setObserver(ILocationObserver observer) {
        // TODO
    }

    @Override
    public boolean canDetectLocation() {
        return false; // TODO
    }

    @Override
    public double getLatitude() {
        return 0; // TODO
    }

    @Override
    public double getLongitude() {
        return 0; // TODO
    }

    private Context context;
}
