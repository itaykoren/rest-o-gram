package rest.o.gram.commands;

import org.json.rpc.client.HttpJsonRpcClientTransport;
import rest.o.gram.tasks.CacheVenueTask;
import rest.o.gram.tasks.ITaskObserver;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/24/13
 */
public class CacheVenueCommand extends AsyncTaskRestogramCommand {
    public CacheVenueCommand(HttpJsonRpcClientTransport transport, ITaskObserver observer, String id) {
        super(transport, observer);
        this.id = id;
    }

    @Override
    public boolean execute() {
        if(!super.execute())
            return false;

        CacheVenueTask t = new CacheVenueTask(transport, this);
        t.execute(id);

        task = t;

        return true;
    }

    private String id;
}
