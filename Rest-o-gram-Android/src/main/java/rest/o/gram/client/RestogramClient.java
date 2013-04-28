package rest.o.gram.client;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import rest.o.gram.commands.*;
import rest.o.gram.filters.RestogramFilterType;
import rest.o.gram.location.ILocationTracker;
import rest.o.gram.location.LocationTracker;
import rest.o.gram.location.LocationTrackerDummy;
import rest.o.gram.tasks.ITaskObserver;
import org.json.rpc.client.HttpJsonRpcClientTransport;

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
        try {
            transport = new HttpJsonRpcClientTransport(new URL(url));
            tracker = new LocationTracker(context);
            //tracker = new LocationTrackerDummy();
        }
        catch(Exception e) {
            System.out.println("Error in RestogramClient: " + e.getMessage());
        }

        PackageManager pm = context.getPackageManager();
        try
        {
            ApplicationInfo appinfo = pm.getApplicationInfo(context.getPackageName(), 0);
            debuggable = (0 != (appinfo.flags &= ApplicationInfo.FLAG_DEBUGGABLE));
        }
        catch(PackageManager.NameNotFoundException e)
        {
        /*debuggable variable will remain false*/
        }
    }

    @Override
    public void getNearby(double latitude, double longitude, ITaskObserver observer) {
        IRestogramCommand command = new GetNearbyCommand(latitude, longitude);
        command.execute(transport, observer);
    }

    @Override
    public void getNearby(double latitude, double longitude, double radius, ITaskObserver observer) {
        IRestogramCommand command = new GetNearbyCommand(latitude, longitude, radius);
        command.execute(transport, observer);
    }

    @Override
    public void getInfo(String venueID, ITaskObserver observer) {
        IRestogramCommand command = new GetInfoCommand(venueID);
        command.execute(transport, observer);
    }

    @Override
    public void getPhotos(String venueID, ITaskObserver observer) {
        IRestogramCommand command = new GetPhotosCommand(venueID);
        command.execute(transport, observer);
    }

    @Override
    public void getPhotos(String venueID, RestogramFilterType filterType, ITaskObserver observer) {
        IRestogramCommand command = new GetPhotosCommand(venueID, filterType);
        command.execute(transport, observer);
    }

    @Override
    public void getNextPhotos(String token, ITaskObserver observer) {
        IRestogramCommand command = new GetNextPhotosCommand(token);
        command.execute(transport, observer);
    }

    @Override
    public void getNextPhotos(String token, RestogramFilterType filterType, ITaskObserver observer) {
        IRestogramCommand command = new GetNextPhotosCommand(token, filterType);
        command.execute(transport, observer);
    }

    @Override
    public ILocationTracker getLocationTracker() {
        return tracker;
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
    private boolean debuggable = false;
}
