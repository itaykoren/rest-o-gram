package rest.o.gram.client;

import android.content.Context;
import android.widget.ImageView;
import rest.o.gram.commands.IRestogramCommandObserver;
import rest.o.gram.filters.RestogramFilterType;
import rest.o.gram.location.ILocationTracker;
import rest.o.gram.network.INetworkStateProvider;
import rest.o.gram.tasks.ITaskObserver;
import rest.o.gram.view.IViewAdapter;

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
     * Executes get photos request with a filter
     */
    void getPhotos(String venueID, RestogramFilterType filterType, ITaskObserver observer);

    /**
     * Executes get next photos request
     */
    void getNextPhotos(String token, ITaskObserver observer);

    /**
     * Executes get next photos request with filter
     */
    void getNextPhotos(String token, RestogramFilterType filterType, ITaskObserver observer);

    /**
     * Executes download image request
     */
    void downloadImage(String url, ImageView imageView, IViewAdapter viewAdapter, boolean force, IRestogramCommandObserver observer);

    /**
     * Executes download image request
     */
    void downloadImage(String url, ImageView imageView, boolean force, IRestogramCommandObserver observer);

    /**
     * Returns location tracker
     */
    ILocationTracker getLocationTracker();

    /**
     * Returns the network state provider
     */
    INetworkStateProvider getNetworkStateProvider();

    /**
     * @return is the application in debug mode?
     */
    boolean isDebuggable();
}
