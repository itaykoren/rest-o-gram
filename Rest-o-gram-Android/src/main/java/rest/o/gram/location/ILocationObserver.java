package rest.o.gram.location;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 16/04/13
 */
public interface ILocationObserver {
    /**
     * Called after location was discovered
     */
    void onLocationUpdated(double latitude, double longitude, int accuracy);

    /**
     * Called when a location tracking operation has timed-out.
     */
    void onTrackingTimedOut();
}
