package rest.o.gram.client;

import android.content.Context;
import android.widget.ImageView;
import rest.o.gram.authentication.IAuthenticationProvider;
import rest.o.gram.commands.IRestogramCommand;
import rest.o.gram.commands.IRestogramCommandObserver;
import rest.o.gram.data_history.IDataHistoryManager;
import rest.o.gram.data.IDataFavoritesManager;
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.filters.IBitmapFilter;
import rest.o.gram.filters.RestogramFilterType;
import rest.o.gram.location.ILocationTracker;
import rest.o.gram.network.INetworkStateProvider;
import rest.o.gram.tasks.ITaskObserver;
import rest.o.gram.view.IPhotoViewAdapter;

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
     * Disposes this client
     */
    void dispose();

    /* NON-AUTH SERVICES */

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
     * Returns command object
     */
    IRestogramCommand downloadImage(String url, RestogramPhoto photo, IPhotoViewAdapter viewAdapter, boolean force, IRestogramCommandObserver observer);

    /**
     * Executes download image request
     * Returns command object
     */
    IRestogramCommand downloadImage(String url, ImageView imageView, boolean force, IRestogramCommandObserver observer);

    /**
     * Executes cache photo request
     * Returns command object
     */
    void cachePhoto(String id, ITaskObserver observer);

    /**
     * Executes fetch photos from cache request
     * Returns command object
     */
    void fetchPhotosFromCache(ITaskObserver observer, String... ids);

    /**
     * Executes cache venue request
     * Returns command object
     */
    void cacheVenue(String id, ITaskObserver observer);

    /**
     * Executes fetch venues from cache request
     * Returns command object
     */
    void fetchVenuesFromCache(ITaskObserver observer, String... ids);


    /* AUTH SERVICES */

    /* PROVIDERS */

    /**
     * Returns location tracker
     */
    ILocationTracker getLocationTracker();

    /**
     * Returns the network state provider
     */
    INetworkStateProvider getNetworkStateProvider();

    /**
     * Returns  the authentication provider
     */
    IAuthenticationProvider getAuthenticationProvider();

    /**
     * Returns the data manager
     */
    IDataHistoryManager getDataHistoryManager();

    /**
     * Returns the cache data manager
     */
    IDataHistoryManager getCacheDataHistoryManager();

    /**
     * Returns the bitmap filter
     */
    IBitmapFilter getBitmapFilter();

    /**
     * Returns the data favorites manager
     */
    IDataFavoritesManager getDataFavoritesManager();

    /**
     * @return is the application in debug mode?
     */
    boolean isDebuggable();
}
