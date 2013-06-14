package rest.o.gram.client;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.ImageView;
import rest.o.gram.authentication.AuthenticationProvider;
import rest.o.gram.authentication.IAuthenticationProvider;
import org.json.rpc.client.HttpJsonRpcClientTransport;
import rest.o.gram.cache.*;
import rest.o.gram.commands.*;
import rest.o.gram.common.Defs;
import rest.o.gram.data_favorites.*;
import rest.o.gram.data_history.*;
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.filters.DefaultBitmapFilter;
import rest.o.gram.filters.FaceBitmapFilter;
import rest.o.gram.filters.IBitmapFilter;
import rest.o.gram.filters.RestogramFilterType;
import rest.o.gram.location.*;
import rest.o.gram.network.INetworkStateProvider;
import rest.o.gram.network.NetworkStateProvider;
import rest.o.gram.tasks.ITaskObserver;
import rest.o.gram.view.IPhotoViewAdapter;

import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 15/04/13
 */
public class RestogramClient implements IRestogramClient {

    /**
     * Returns the singleton instance of restogram client
     */
    public static IRestogramClient getInstance() {
        if(instance == null)
            instance = new RestogramClient();

        return instance;
    }

    @Override
    public void initialize(Context context) {
        try
        {
            this.context = context;

            // sets debuggable flag
            PackageManager pm = context.getPackageManager();
            try
            {
                ApplicationInfo appinfo = pm.getApplicationInfo(context.getPackageName(), 0);
                debuggable = (0 != (appinfo.flags &= ApplicationInfo.FLAG_DEBUGGABLE));
            }
            catch(PackageManager.NameNotFoundException e)
            {
                // debuggable variable will remain false
            }

            if (RestogramClient.getInstance().isDebuggable())
                Log.d("REST-O-GRAM", "CLIENT UP");

            authProvider = new AuthenticationProvider(context, Defs.Transport.BASE_HOST_NAME);
            dataFavoritesManager = new DataFavoritesManager(this);
            transport = new HttpJsonRpcClientTransport(new URL(jsonServiceHostName));
            setJsonEncoding(transport);
            authTransport = new HttpJsonRpcClientTransport(new URL(jsonAuthServiceHostName));
            setJsonEncoding(authTransport);

            ILocationTrackerFactory factory = new LocationTrackerFactory(context);
            tracker = factory.create(Defs.Location.PRIMARY_TRACKER_TYPE);
            if(!tracker.canDetectLocation())
                tracker = factory.create(Defs.Location.SECONDARY_TRACKER_TYPE);

            networkStateProvider = new NetworkStateProvider(context);
            commandQueue = new RestogramCommandQueue();

            cache = new RestogramCache();

            if(Defs.Data.BITMAP_CACHE_ENABLED)
                bitmapCache = new BitmapCache(context);
            else
                bitmapCache = new DummyBitmapCache();

            if(Defs.Data.DATA_HISTORY_ENABLED)
                dataHistoryManager = new FileDataHistoryManager(context);

            if(Defs.Data.CACHE_DATA_HISTORY_ENABLED)
                cacheDataHistoryManager = new DataHistoryManager();

            if(Defs.Filtering.FACE_FILTERING_ENABLED)
                bitmapFilter = new FaceBitmapFilter(Defs.Filtering.MAX_FACES_TO_DETECT);
            else
                bitmapFilter = new DefaultBitmapFilter();
        }
        catch(Exception e) {
            System.out.println("Error in RestogramClient: " + e.getMessage());
        }
    }

    @Override
    public void dispose() {
        // Cancel all commands
        commandQueue.cancelAll();

        // Flush data
        if(dataHistoryManager != null) {
            dataHistoryManager.flush();
        }

        // Clear cache data history
        if(cacheDataHistoryManager != null)
            cacheDataHistoryManager.clear();

        // Clear cache
        if(cache != null)
            cache.clear();

        // Clear bitmap cache
        if(bitmapCache != null)
            bitmapCache.clear();

        if(dataFavoritesManager != null)
            dataFavoritesManager.dispose();

    }

    /* NON-AUTH SERVICES */

    @Override
    public void getNearby(double latitude, double longitude, ITaskObserver observer) {
        setJsonAuthToken(transport);
        IRestogramCommand command = new GetNearbyCommand(transport, observer, latitude, longitude);
        commandQueue.pushForce(command);
    }

    @Override
    public void getNearby(double latitude, double longitude, double radius, ITaskObserver observer) {
        setJsonAuthToken(transport);
        IRestogramCommand command = new GetNearbyCommand(transport, observer, latitude, longitude, radius);
        commandQueue.pushForce(command);
    }

    @Override
    public void getInfo(String venueID, ITaskObserver observer) {
        setJsonAuthToken(transport);
        IRestogramCommand command = new GetInfoCommand(transport, observer, venueID);
        commandQueue.pushForce(command);
    }

    @Override
    public void getPhotos(String venueID, ITaskObserver observer) {
        setJsonAuthToken(transport);
        IRestogramCommand command = new GetPhotosCommand(transport, observer, venueID);
        commandQueue.pushForce(command);
    }

    @Override
    public void getPhotos(String venueID, RestogramFilterType filterType, ITaskObserver observer) {
        setJsonAuthToken(transport);
        IRestogramCommand command = new GetPhotosCommand(transport, observer, venueID, filterType);
        commandQueue.pushForce(command);
    }

    @Override
    public void getNextPhotos(String token, String originVenueId, ITaskObserver observer) {
        setJsonAuthToken(transport);
        IRestogramCommand command = new GetNextPhotosCommand(transport, observer, token, originVenueId);
        commandQueue.pushForce(command);
    }

    @Override
    public void getNextPhotos(String token, RestogramFilterType filterType, String originVenueId, ITaskObserver observer) {
        setJsonAuthToken(transport);
        IRestogramCommand command = new GetNextPhotosCommand(transport, observer, token, filterType, originVenueId);
        commandQueue.pushForce(command);
    }

    @Override
    public void getProfilePhotoUrl(String facebookId, ITaskObserver observer) {
        setJsonAuthToken(transport);
        IRestogramCommand command = new GetProfilePhotoUrlCommand(transport, observer, facebookId);
        commandQueue.pushForce(command);
    }

    @Override
    public IRestogramCommand downloadImage(String url, RestogramPhoto photo, IPhotoViewAdapter viewAdapter,
                              boolean force, IRestogramCommandObserver observer) {
        IRestogramCommand command = new DownloadImageCommand(context, url, photo.getInstagram_id(), viewAdapter);

        if(observer != null)
            command.addObserver(observer);

        if(force)
            commandQueue.pushForce(command);
        else
            commandQueue.pushBack(command);

        return command;
    }

    @Override
    public IRestogramCommand downloadImage(String url, String id, ImageView imageView,
                              boolean force, IRestogramCommandObserver observer) {
        IRestogramCommand command = new DownloadImageCommand(context, url, id, imageView);

        if(observer != null)
            command.addObserver(observer);

        if(force)
            commandQueue.pushForce(command);
        else
            commandQueue.pushBack(command);

        return command;
    }

    @Override
    public void addPhotoToFavorites(String photoId, ITaskObserver observer) {
        setJsonAuthToken(transport);
        IRestogramCommand command = new AddPhotoToFavoritesCommand(transport, observer, photoId);
        commandQueue.pushForce(command);
    }

    @Override
    public void removePhotoFromFavorites(String photoId, ITaskObserver observer) {
        setJsonAuthToken(transport);
        IRestogramCommand command = new RemovePhotoFromFavoritesCommand(transport, observer, photoId);
        commandQueue.pushForce(command);
    }

    @Override
    public void cachePhoto(String id, String originVenueId, ITaskObserver observer) {
        setJsonAuthToken(transport);
        IRestogramCommand command = new CachePhotoCommand(transport, observer, id, originVenueId);
        commandQueue.pushForce(command);
    }

    @Override
    public void fetchPhotosFromCache(ITaskObserver observer, String... ids) {
        setJsonAuthToken(transport);
        IRestogramCommand command = new FetchPhotosFromCacheCommand(transport, observer, ids);
        commandQueue.pushForce(command);
    }

    @Override
    public void cacheVenue(String id, ITaskObserver observer) {
        setJsonAuthToken(transport);
        IRestogramCommand command = new CacheVenueCommand(transport, observer, id);
        commandQueue.pushForce(command);
    }

    @Override
    public void fetchVenuesFromCache(ITaskObserver observer, String... ids) {
        setJsonAuthToken(transport);
        IRestogramCommand command = new FetchVenuesFromCacheCommand(transport, observer, ids);
        commandQueue.pushForce(command);
    }

    /* AUTH SERVICES */


    /* PROVIDERS */

    @Override
    public ILocationTracker getLocationTracker() {
        return tracker;
    }

    @Override
    public INetworkStateProvider getNetworkStateProvider() {
        return networkStateProvider;
    }

    @Override
    public IAuthenticationProvider getAuthenticationProvider() {
        return authProvider;
    }

    @Override
    public IDataHistoryManager getDataHistoryManager() {
        return dataHistoryManager;
    }

    @Override
    public IDataHistoryManager getCacheDataHistoryManager() {
        return cacheDataHistoryManager;
    }

    @Override
    public IBitmapFilter getBitmapFilter() {
        return bitmapFilter;
    }

    @Override
    public IDataFavoritesManager getDataFavoritesManager() {
        return dataFavoritesManager;
    }

    @Override
    public IRestogramCache getCache() {
        return cache;
    }

    @Override
    public IBitmapCache getBitmapCache() {
        return bitmapCache;
    }

    @Override
    public boolean isDebuggable() {
        return debuggable;
    }

    private void setJsonEncoding(HttpJsonRpcClientTransport transport) {
        transport.setHeader("charset", "UTF-8");
    }

    private void setJsonAuthToken(HttpJsonRpcClientTransport transport) {
        if (authProvider.isUserLoggedIn())
            transport.setHeader("lean_token", authProvider.getAuthToken());
    }

    /**
     * Ctor
     */
    private RestogramClient() {
    }

    private static IRestogramClient instance; // Singleton instance
    private Context context; // Context
    private final String jsonServiceHostName = Defs.Transport.BASE_HOST_NAME + "/service"; // json rpc non-auth URL
    private final String jsonAuthServiceHostName = Defs.Transport.BASE_HOST_NAME + "/auth-service"; // json rpc non-auth URL
    private IAuthenticationProvider authProvider;
    private IDataFavoritesManager dataFavoritesManager;
    private HttpJsonRpcClientTransport transport; // Transport object
    private HttpJsonRpcClientTransport authTransport; // Auth Transport object
    private ILocationTracker tracker; // Location tracker
    private INetworkStateProvider networkStateProvider;
    private IDataHistoryManager dataHistoryManager; // Data history manager
    private IDataHistoryManager cacheDataHistoryManager; // Cache data history manager
    private IBitmapFilter bitmapFilter; // Bitmap filter
    private IRestogramCommandQueue commandQueue; // Command queue
    private IRestogramCache cache; // Cache object
    private IBitmapCache bitmapCache; // Bitmap cache object
    private boolean debuggable = false; // debuggable flag
}
