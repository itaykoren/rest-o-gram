package com.tau.client;

import com.tau.commands.GetInfoCommand;
import com.tau.commands.GetNearbyCommand;
import com.tau.commands.GetPhotosCommand;
import com.tau.commands.IRestogramCommand;
import com.tau.tasks.ITaskObserver;
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

    /**
     * Executes get nearby request
     */
    public void getNearby(double latitude, double longitude, double radius, ITaskObserver observer) {
        IRestogramCommand command = new GetNearbyCommand(latitude, longitude, radius);
        command.execute(transport, observer);
    }

    /**
     * Executes get info request
     */
    public void getInfo(String venueID, ITaskObserver observer) {
        IRestogramCommand command = new GetInfoCommand(venueID);
        command.execute(transport, observer);
    }

    /**
     * Executes get photos request
     */
    public void getPhotos(String venueID, ITaskObserver observer) {
        IRestogramCommand command = new GetPhotosCommand(venueID);
        command.execute(transport, observer);
    }

    /**
     * Ctor
     */
    private RestogramClient() {
        try {
            transport = new HttpJsonRpcClientTransport(new URL(url));
        }
        catch(Exception e) {
            System.out.println("Error in RestogramClient: " + e.getMessage());
        }
    }

    private static IRestogramClient instance; // Singleton instance
    private final String url = "http://rest-o-gram.appspot.com/service"; // Server URL
    private HttpJsonRpcClientTransport transport; // Transport object
}
