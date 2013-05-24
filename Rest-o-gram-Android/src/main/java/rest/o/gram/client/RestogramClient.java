package rest.o.gram.client;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.ImageView;
import rest.o.gram.authentication.AuthenticationProvider;
import rest.o.gram.authentication.IAuthenticationProvider;
import org.json.rpc.client.HttpJsonRpcClientTransport;
import rest.o.gram.commands.*;
import rest.o.gram.common.Defs;
import rest.o.gram.data.FileDataHistoryManager;
import rest.o.gram.data.IDataHistoryManager;
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.filters.DefaultBitmapFilter;
import rest.o.gram.filters.FaceBitmapFilter;
import rest.o.gram.filters.IBitmapFilter;
import rest.o.gram.filters.RestogramFilterType;
import rest.o.gram.location.ILocationTracker;
import rest.o.gram.location.LocationTracker;
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

            authProvider = new AuthenticationProvider(context, baseHostname);
            transport = new HttpJsonRpcClientTransport(new URL(jsonServiceHostName));
            authTransport = new HttpJsonRpcClientTransport(new URL(jsonAuthServiceHostName));
            tracker = new LocationTracker(context);
            //tracker = new LocationTrackerDummy();
            networkStateProvider = new NetworkStateProvider(context);
            commandQueue = new RestogramCommandQueue();

            if(Defs.Data.DATA_HISTORY_ENABLED)
                dataHistoryManager = new FileDataHistoryManager(context);

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
            dataHistoryManager.clear();
        }

        // TODO: dispose
    }

    /* NON-AUTH SERVICES */

    @Override
    public void getNearby(double latitude, double longitude, ITaskObserver observer) {
        IRestogramCommand command = new GetNearbyCommand(transport, observer, latitude, longitude);
        commandQueue.pushForce(command);
    }

    @Override
    public void getNearby(double latitude, double longitude, double radius, ITaskObserver observer) {
        IRestogramCommand command = new GetNearbyCommand(transport, observer, latitude, longitude, radius);
        commandQueue.pushForce(command);
    }

    @Override
    public void getInfo(String venueID, ITaskObserver observer) {
        IRestogramCommand command = new GetInfoCommand(transport, observer, venueID);
        commandQueue.pushForce(command);
    }

    @Override
    public void getPhotos(String venueID, ITaskObserver observer) {
        IRestogramCommand command = new GetPhotosCommand(transport, observer, venueID);
        commandQueue.pushForce(command);
    }

    @Override
    public void getPhotos(String venueID, RestogramFilterType filterType, ITaskObserver observer) {
        IRestogramCommand command = new GetPhotosCommand(transport, observer, venueID, filterType);
        commandQueue.pushForce(command);
    }

    @Override
    public void getNextPhotos(String token, ITaskObserver observer) {
        IRestogramCommand command = new GetNextPhotosCommand(transport, observer, token);
        commandQueue.pushForce(command);
    }

    @Override
    public void getNextPhotos(String token, RestogramFilterType filterType, ITaskObserver observer) {
        IRestogramCommand command = new GetNextPhotosCommand(transport, observer, token, filterType);
        commandQueue.pushForce(command);
    }

    @Override
    public IRestogramCommand downloadImage(String url, RestogramPhoto photo, IPhotoViewAdapter viewAdapter,
                              boolean force, IRestogramCommandObserver observer) {
        IRestogramCommand command = new DownloadImageCommand(url, photo, viewAdapter);

        if(observer != null)
            command.addObserver(observer);

        if(force)
            commandQueue.pushForce(command);
        else
            commandQueue.pushBack(command);

        return command;
    }

    @Override
    public IRestogramCommand downloadImage(String url, ImageView imageView,
                              boolean force, IRestogramCommandObserver observer) {
        IRestogramCommand command = new DownloadImageCommand(url, imageView);

        if(observer != null)
            command.addObserver(observer);

        if(force)
            commandQueue.pushForce(command);
        else
            commandQueue.pushBack(command);

        return command;
    }

    /* AUTH SERVICES */

    @Override
    public void getRecentPhotos(ITaskObserver observer) {
        IRestogramCommand command = new GetRecentPhotosCommand(authTransport, observer);
        commandQueue.pushForce(command);
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
    public IBitmapFilter getBitmapFilter() {
        return bitmapFilter;
    }

    @Override
    public boolean isDebuggable() {
        return debuggable;
    }

    /**
     * Ctor
     */
    private RestogramClient() {
    }

    private static IRestogramClient instance; // Singleton instance
    private final String baseHostname = "http://restogramapp.appspot.com"; // base Server URL
    private final String jsonServiceHostName = baseHostname + "/service"; // json rpc non-auth URL
    private final String jsonAuthServiceHostName = baseHostname + "/auth-service"; // json rpc non-auth URL
    private IAuthenticationProvider authProvider;
    private HttpJsonRpcClientTransport transport; // Transport object
    private HttpJsonRpcClientTransport authTransport; // Auth Transport object
    private ILocationTracker tracker; // Location tracker
    private INetworkStateProvider networkStateProvider;
    private IDataHistoryManager dataHistoryManager; // Data history manager
    private IBitmapFilter bitmapFilter; // Bitmap filter
    private IRestogramCommandQueue commandQueue; // Command queue
    private boolean debuggable = false; // debuggable flag
}
