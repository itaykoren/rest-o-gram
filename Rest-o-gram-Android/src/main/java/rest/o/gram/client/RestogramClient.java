package rest.o.gram.client;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.ImageView;
import org.json.rpc.client.HttpJsonRpcClientTransport;
import rest.o.gram.commands.*;
import rest.o.gram.filters.RestogramFilterType;
import rest.o.gram.location.ILocationTracker;
import rest.o.gram.location.LocationTracker;
import rest.o.gram.network.INetworkStateProvider;
import rest.o.gram.network.NetworkStateProvider;
import rest.o.gram.tasks.ITaskObserver;
import rest.o.gram.view.IViewAdapter;

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

            transport = new HttpJsonRpcClientTransport(new URL(url));
            tracker = new LocationTracker(context);
            //tracker = new LocationTrackerDummy();
            networkStateProvider = new NetworkStateProvider(context);
            commandQueue = new RestogramCommandQueue();
        }
        catch(Exception e) {
            System.out.println("Error in RestogramClient: " + e.getMessage());
        }
    }

    @Override
    public void getNearby(double latitude, double longitude, ITaskObserver observer) {
        IRestogramCommand command = new GetNearbyCommand(transport, observer, latitude, longitude);
        command.execute();
    }

    @Override
    public void getNearby(double latitude, double longitude, double radius, ITaskObserver observer) {
        IRestogramCommand command = new GetNearbyCommand(transport, observer, latitude, longitude, radius);
        command.execute();
    }

    @Override
    public void getInfo(String venueID, ITaskObserver observer) {
        IRestogramCommand command = new GetInfoCommand(transport, observer, venueID);
        command.execute();
    }

    @Override
    public void getPhotos(String venueID, ITaskObserver observer) {
        IRestogramCommand command = new GetPhotosCommand(transport, observer, venueID);
        command.execute();
    }

    @Override
    public void getPhotos(String venueID, RestogramFilterType filterType, ITaskObserver observer) {
        IRestogramCommand command = new GetPhotosCommand(transport, observer, venueID, filterType);
        command.execute();
    }

    @Override
    public void getNextPhotos(String token, ITaskObserver observer) {
        IRestogramCommand command = new GetNextPhotosCommand(transport, observer, token);
        command.execute();
    }

    @Override
    public void getNextPhotos(String token, RestogramFilterType filterType, ITaskObserver observer) {
        IRestogramCommand command = new GetNextPhotosCommand(transport, observer, token, filterType);
        command.execute();
    }

    @Override
    public void downloadImage(String url, ImageView imageView, IViewAdapter viewAdapter,
                              boolean force, IRestogramCommandObserver observer) {
        IRestogramCommand command = new DownloadImageCommand(url, imageView, viewAdapter);

        if(observer != null)
            command.addObserver(observer);

        if(force)
            commandQueue.pushForce(command);
        else
            commandQueue.pushBack(command);
    }

    @Override
    public void downloadImage(String url, ImageView imageView,
                              boolean force, IRestogramCommandObserver observer) {
        IRestogramCommand command = new DownloadImageCommand(url, imageView);

        if(observer != null)
            command.addObserver(observer);

        if(force)
            commandQueue.pushForce(command);
        else
            commandQueue.pushBack(command);
    }

    @Override
    public ILocationTracker getLocationTracker() {
        return tracker;
    }

    @Override
    public INetworkStateProvider getNetworkStateProvider() {
        return networkStateProvider;
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
    private final String url = "http://rest-o-gram.appspot.com/service"; // Server URL
    private HttpJsonRpcClientTransport transport; // Transport object
    private ILocationTracker tracker; // Location tracker
    private INetworkStateProvider networkStateProvider;
    private IRestogramCommandQueue commandQueue; // Command queue
    private boolean debuggable = false; // debuggable flag
}
