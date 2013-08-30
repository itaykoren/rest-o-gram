package rest.o.gram.client;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.ImageView;
import org.json.rpc.client.HttpJsonRpcClientTransport;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import rest.o.gram.application.IRestogramApplication;
import rest.o.gram.authentication.AuthenticationProvider;
import rest.o.gram.authentication.IAuthenticationProvider;
import rest.o.gram.cache.*;
import rest.o.gram.commands.*;
import rest.o.gram.common.Defs;
import rest.o.gram.common.Utils;
import rest.o.gram.data_favorites.DataFavoritesManager;
import rest.o.gram.data_favorites.IDataFavoritesManager;
import rest.o.gram.data_history.DataHistoryManager;
import rest.o.gram.data_history.FileDataHistoryManager;
import rest.o.gram.data_history.IDataHistoryManager;
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.filters.*;
import rest.o.gram.location.ILocationTracker;
import rest.o.gram.location.ILocationTrackerFactory;
import rest.o.gram.location.LocationTrackerFactory;
import rest.o.gram.network.INetworkStateProvider;
import rest.o.gram.network.NetworkStateProvider;
import rest.o.gram.openCV.JavaCVFaceDetector;
import rest.o.gram.openCV.OpenCVFaceDetector;
import rest.o.gram.openCV.RestogramBaseLoaderCallback;
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
    public void initialize(final Context context, final IRestogramApplication application) {

        if (isInitialized)
        {
            if (isDebuggable())
                Log.d("REST-O-GRAM", "CLIENT ALREADY INITIALIZED");
            return;
        }

        try
        {
            this.application = application;
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
                Log.d("REST-O-GRAM", "CLIENT LOADING");

            authProvider = new AuthenticationProvider(context, Defs.Transport.BASE_HOST_NAME);
            dataFavoritesManager = new DataFavoritesManager(this);
            transport = new HttpJsonRpcClientTransport(new URL(jsonServiceHostName));
            setJsonEncoding(transport);
            authTransport = new HttpJsonRpcClientTransport(new URL(jsonAuthServiceHostName));
            setJsonEncoding(authTransport);

            ILocationTrackerFactory locationTrackerFactory = new LocationTrackerFactory(context);
            tracker = locationTrackerFactory.create(Defs.Location.PRIMARY_TRACKER_TYPE);
            if(!tracker.canDetectLocation())
                tracker = locationTrackerFactory.create(Defs.Location.SECONDARY_TRACKER_TYPE);

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

            final IBitmapFilterFactory bitmapFilterFactory = new BitmapFilterFactory();
            bitmapFilter = bitmapFilterFactory.create(Defs.Filtering.BITMAP_FILTER_TYPE);

            isInitialized = true;

            if (RestogramClient.getInstance().isDebuggable())
                Log.d("REST-O-GRAM", "CLIENT UP");
        }
        catch(Exception e)
        {
            Log.e("REST-O-GRAM", "Error in RestogramClient: " + e.getMessage());
        }
    }

    @Override
    public void initializeFilter(Context context) {
        // load openCV manager - if has to
        if (Utils.usesOpenCVBasedBitmapFilter() && Utils.canApplyBitmapFilter())
        {
            final BaseLoaderCallback loaderCallback =
                    new RestogramBaseLoaderCallback(context) {
                        @Override
                        public void onManagerConnected(int status) {
                            switch (status)
                            {
                                case LoaderCallbackInterface.SUCCESS:
                                {
                                    if (RestogramClient.getInstance().isDebuggable())
                                        Log.i("REST-O-GRAM", "OpenCV loaded successfully");

                                    initFaceDetector(mAppContext);
                                } break;
                                default:
                                {
                                    Log.e("REST-O-GRAM", "OpenCV could not be loaded");
                                    super.onManagerConnected(status);
                                } break;
                            }
                        }
                    };

            if (Defs.Filtering.OpenCVDetector.OPEN_CV_DISTRIBUTION_METHOD == Defs.Filtering.OpenCVDetector.OpenCVDistributionMethod.Dynamic)
                OpenCVLoader.initAsync(Defs.Filtering.OpenCVDetector.OPENCV_VERSION, context, loaderCallback);
            else if (Defs.Filtering.OpenCVDetector.OPEN_CV_DISTRIBUTION_METHOD == Defs.Filtering.OpenCVDetector.OpenCVDistributionMethod.Static)
            {
                if (!OpenCVLoader.initDebug()) // the static init method...
                    Log.e("REST-O-GRAM", "error while loading openCV");
                else
                    initFaceDetector(context);
            }
        }
    }

    @Override
    public void dispose() {
        isInitialized = false;

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

        if (bitmapFilter != null)
            bitmapFilter.dispose();

        if (tracker != null)
        {
            tracker.stop();
            tracker.dispose();
        }
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
    public IRestogramCommand getPhotos(String venueID, ITaskObserver observer) {
        setJsonAuthToken(transport);
        IRestogramCommand command = new GetPhotosCommand(transport, observer, venueID);
        commandQueue.pushForce(command);
        return command;
    }

    @Override
    public IRestogramCommand getPhotos(String venueID, RestogramFilterType filterType, ITaskObserver observer) {
        setJsonAuthToken(transport);
        IRestogramCommand command = new GetPhotosCommand(transport, observer, venueID, filterType);
        commandQueue.pushForce(command);
        return command;
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
        int size = (int)(Utils.getScreenWidth(context) / 6.0);
        IRestogramCommand command = new DownloadImageCommand(url, photo,
                                                             viewAdapter, size, size);

        if(observer != null)
            command.addObserver(observer);

        if(force)
            commandQueue.pushForce(command);
        else
            commandQueue.pushBack(command);

        return command;
    }

    @Override
    public IRestogramCommand downloadImage(String url, String photoId, ImageView imageView,
                              boolean force, IRestogramCommandObserver observer) {
        return downloadImage(url, photoId, imageView, force, observer, 1);
    }

    @Override
    public IRestogramCommand downloadImage(String url, String photoId, ImageView imageView,
                              boolean force, IRestogramCommandObserver observer, float sizeRatio) {
        int size = (int)(Utils.getScreenWidth(context) * sizeRatio);
        IRestogramCommand command = new DownloadImageCommand(url, photoId, imageView, size, size);

        if(observer != null)
            command.addObserver(observer);

        if(force)
            commandQueue.pushForce(command);
        else
            commandQueue.pushBack(command);

        return command;
    }

    @Override
    public void cacheVenue(String id, ITaskObserver observer) {
        setJsonAuthToken(transport);
        IRestogramCommand command = new CacheVenueCommand(transport, observer, id);
        commandQueue.pushForce(command);
    }

    /* AUTH SERVICES */

    @Override
    public void addPhotoToFavorites(String photoId, String originVenueId, ITaskObserver observer) {
        setJsonAuthToken(authTransport);
        IRestogramCommand command = new AddPhotoToFavoritesCommand(authTransport, observer,
                                                                   photoId, originVenueId);
        commandQueue.pushForce(command);
    }

    @Override
    public void removePhotoFromFavorites(String photoId, ITaskObserver observer) {
        setJsonAuthToken(authTransport);
        IRestogramCommand command = new RemovePhotoFromFavoritesCommand(authTransport, observer, photoId);
        commandQueue.pushForce(command);
    }

    /* AUX SERVICES */

    /**
     * Inits a FaceDetector according to the definitions.
     * @param context application context
     */
    private void initFaceDetector(Context context) {
       if (Defs.Filtering.BITMAP_FILTER_TYPE == Defs.Filtering.BitmapFilterType.OpenCVFaceBitmapFilter) {
           System.loadLibrary("face_detector"); //  loads native access library
           faceDetector = new OpenCVFaceDetector(context);
       }
       else if (Defs.Filtering.BITMAP_FILTER_TYPE == Defs.Filtering.BitmapFilterType.JavaCVFaceBitmapFilter)
           faceDetector = new JavaCVFaceDetector(context);

       if(bitmapFilter != null)
           bitmapFilter.setFaceDetector(faceDetector);
    }

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

    @Override
    public boolean isInitialized() {
        return isInitialized;
    }

    @Override
    public IRestogramApplication getApplication() {
        return application;
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
    private RestogramClient() {}

    private static IRestogramClient instance; // Singleton instance
    private Context context; // Context
    private IRestogramApplication application; // Application
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
    private FaceDetector faceDetector;
    private boolean debuggable = false; // debuggable flag
    private boolean isInitialized = false; // Is initialized flag
}
