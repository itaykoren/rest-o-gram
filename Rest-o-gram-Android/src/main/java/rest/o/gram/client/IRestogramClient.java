package rest.o.gram.client;

import android.content.Context;
import rest.o.gram.location.ILocationTracker;
import rest.o.gram.tasks.ITaskObserver;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 15/04/13
 */
public interface IRestogramClient {

    /**
     * Initializes this client
     */
    void initialize(Context context);

    /**
     * Executes get nearby request
     */
    void getNearby(double latitude, double longitude, ITaskObserver observer);

    /**
     * Executes get nearby request
     */
    void getNearby(double latitude, double longitude, double radius, ITaskObserver observer);

    /**
     * Executes get info request
     */
    void getInfo(String venueID, ITaskObserver observer);

    /**
     * Executes get photos request
     */
    void getPhotos(String venueID, ITaskObserver observer);

    /**
     * Returns location tracker
     */
    ILocationTracker getLocationTracker();
}
