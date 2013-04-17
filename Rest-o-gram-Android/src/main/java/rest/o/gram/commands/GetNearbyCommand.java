package rest.o.gram.commands;

import rest.o.gram.tasks.GetNearbyTask;
import rest.o.gram.tasks.ITaskObserver;
import org.json.rpc.client.HttpJsonRpcClientTransport;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 15/04/13
 */
public class GetNearbyCommand implements IRestogramCommand {

    public GetNearbyCommand(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = null;
    }

    public GetNearbyCommand(double latitude, double longitude, double radius) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
    }

    @Override
    public void execute(HttpJsonRpcClientTransport transport, ITaskObserver observer) {
        GetNearbyTask task = new GetNearbyTask(transport, observer);
        if(radius != null)
            task.execute(latitude, longitude, radius);
        else
            task.execute(latitude, longitude);
    }

    private Double latitude;
    private Double longitude;
    private Double radius;
}
