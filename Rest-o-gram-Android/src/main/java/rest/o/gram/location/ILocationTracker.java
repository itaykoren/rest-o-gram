package rest.o.gram.location;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 16/04/13
 */
public interface ILocationTracker {
    /**
     * Forces an immidiate estimation of the current location
     */
    void force();

    /**
     * Starts location tracking
     */
    void start();

    /**
     * Stops location tracking
     */
    void stop();

    /**
     * Sets location observer
     */
    void setObserver(ILocationObserver observer);

    /**
     * Can detect the current location?
     */
    boolean canDetectLocation();

    /**
     * Returns last known latitude
     */
    double getLatitude();

    /**
     * Returns last known longitude
     */
    double getLongitude();
}
