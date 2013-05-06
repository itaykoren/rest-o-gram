package rest.o.gram.commands;

import rest.o.gram.tasks.GetNearbyTask;
import rest.o.gram.tasks.ITaskObserver;
import org.json.rpc.client.HttpJsonRpcClientTransport;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 15/04/13
 */
public class GetNearbyCommand extends AbstractRestogramCommand {

    public GetNearbyCommand(HttpJsonRpcClientTransport transport, ITaskObserver observer,
                            double latitude, double longitude) {
        super(transport, observer);
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = null;
    }

    public GetNearbyCommand(HttpJsonRpcClientTransport transport, ITaskObserver observer,
                            double latitude, double longitude, double radius) {
        this(transport, observer, latitude, longitude);
        this.radius = radius;
    }

    @Override
    public void execute() {
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
