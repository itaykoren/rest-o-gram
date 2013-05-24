package rest.o.gram.commands;

import org.json.rpc.client.HttpJsonRpcClientTransport;
import rest.o.gram.tasks.FetchVenuesFromCacheTask;
import rest.o.gram.tasks.ITaskObserver;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/24/13
 */
public class FetchVenuesFromCacheCommand extends AsyncTaskRestogramCommand {
    public FetchVenuesFromCacheCommand(HttpJsonRpcClientTransport transport, ITaskObserver observer, String... ids) {
        super(transport, observer);
        this.ids = ids;
    }

    @Override
    public boolean execute() {
        if(!super.execute())
            return false;

        FetchVenuesFromCacheTask t = new FetchVenuesFromCacheTask(transport, this);
        t.execute(ids);

        task = t;

        return true;
    }

    private String[] ids;
}
