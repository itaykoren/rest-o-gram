package rest.o.gram.client;

import android.content.Context;
import android.widget.ImageView;
import rest.o.gram.application.IRestogramApplication;
import rest.o.gram.authentication.IAuthenticationProvider;
import rest.o.gram.cache.IBitmapCache;
import rest.o.gram.cache.IRestogramCache;
import rest.o.gram.commands.IRestogramCommand;
import rest.o.gram.commands.IRestogramCommandObserver;
import rest.o.gram.data_favorites.IDataFavoritesManager;
import rest.o.gram.data_history.IDataHistoryManager;
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
    void initialize(Context context, IRestogramApplication application);

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
     * Returns command object
     */
    IRestogramCommand getPhotos(String venueID, ITaskObserver observer);

    /**
     * Executes get photos request with a filter
     * Returns command object
     */
    IRestogramCommand getPhotos(String venueID, RestogramFilterType filterType, ITaskObserver observer);

    /**
     * Executes get next photos request
     * Returns command object
     */
    IRestogramCommand getNextPhotos(String token, String originVenueId, ITaskObserver observer);

    /**
     * Executes get next photos request with filter
     * Returns command object
     */
    IRestogramCommand getNextPhotos(String token, RestogramFilterType filterType, String originVenueId, ITaskObserver observer);

    /**
     * Executes get get profile photo url request
     */
    void getProfilePhotoUrl(String facebookId, ITaskObserver observer);
    /**
     * Executes download image request
     * Returns command object
     */
    IRestogramCommand downloadImage(String url, RestogramPhoto photo, IPhotoViewAdapter viewAdapter, boolean force, IRestogramCommandObserver observer);

    /**
     * Executes download image request
     * Returns command object
     */
    IRestogramCommand downloadImage(String url, String photoId, ImageView imageView, boolean force, IRestogramCommandObserver observer);

    /**
     * Executes download image request
     * Returns command object
     */
    IRestogramCommand downloadImage(String url, String photoId, ImageView imageView, boolean force, IRestogramCommandObserver observer, float sizeRatio);

    /* AUTH SERVICES */

    /**
     * Executes add photo to favorites request
     */
    void addPhotoToFavorites(String photoId, String originVenueId, ITaskObserver observer);

    /**
     * Executes remove photo from favorites request
     */
    void removePhotoFromFavorites(String photoId, ITaskObserver observer);

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
     * Returns the cache
     */
    IRestogramCache getCache();

    /**
     * Returns the bitmap cache
     */
    IBitmapCache getBitmapCache();

    /**
     * @return is the application in debug mode?
     */
    boolean isDebuggable();

    /**
     * Returns true whether this client is initialized, false otherwise
     */
    boolean isInitialized();

    /**
     * Returns the restogram application
     */
    IRestogramApplication getApplication();
}
