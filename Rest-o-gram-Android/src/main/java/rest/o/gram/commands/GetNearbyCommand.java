package rest.o.gram.commands;

import org.json.rpc.client.HttpJsonRpcClientTransport;
import rest.o.gram.tasks.GetNearbyTask;
import rest.o.gram.tasks.ITaskObserver;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 15/04/13
 */
public class GetNearbyCommand extends AsyncTaskRestogramCommand {

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
    public boolean execute() {
        if(!super.execute())
            return false;

        GetNearbyTask t = new GetNearbyTask(transport, this);
        if(radius != null)
            t.execute(latitude, longitude, radius);
        else
            t.execute(latitude, longitude);

        task = t;

        return true;
    }

    private Double latitude;
    private Double longitude;
    private Double radius;
}
