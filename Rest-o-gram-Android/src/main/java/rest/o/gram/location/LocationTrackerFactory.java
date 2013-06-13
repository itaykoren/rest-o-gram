package rest.o.gram.location;

import android.content.Context;
import rest.o.gram.common.Defs;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 6/13/13
 */
public class LocationTrackerFactory implements ILocationTrackerFactory {
    /**
     * Ctor
     */
    public LocationTrackerFactory(Context context) {
        this.context = context;
    }

    @Override
    public ILocationTracker create(Defs.Location.TrackerType type) {
        switch(type) {
            case TrackerTypeDummy:
                return new LocationTrackerDummy();
            case TrackerTypeFluffy:
                return new LocationTracker(context);
            case TrackerTypeSimple:
                return new LocationTrackerSimple(context);
            case TrackerTypeGoogle:
                return new LocationTrackerGoogle(context);
            default:
                return null;
        }
    }

    private Context context;
}
