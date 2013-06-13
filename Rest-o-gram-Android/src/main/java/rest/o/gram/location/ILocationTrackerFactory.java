package rest.o.gram.location;

import rest.o.gram.common.Defs;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 6/13/13
 */
public interface ILocationTrackerFactory {
    /**
     * Returns location tracker according to given type
     */
    ILocationTracker create(Defs.Location.TrackerType type);
}
