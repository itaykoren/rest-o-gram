package com.tau.commands;

import com.tau.tasks.GetNearbyTask;
import com.tau.tasks.ITaskObserver;
import org.json.rpc.client.HttpJsonRpcClientTransport;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 15/04/13
 */
public class GetNearbyCommand implements IRestogramCommand {

    public GetNearbyCommand(double latitude, double longitude, double radius) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
    }

    @Override
    public void execute(HttpJsonRpcClientTransport transport, ITaskObserver observer) {
        GetNearbyTask task = new GetNearbyTask(transport, observer);
        task.execute(latitude, longitude, radius);
    }

    private double latitude;
    private double longitude;
    private double radius;
}
