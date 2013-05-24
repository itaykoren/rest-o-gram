package rest.o.gram.commands;

import org.json.rpc.client.HttpJsonRpcClientTransport;
import rest.o.gram.tasks.FetchPhotosFromCacheTask;
import rest.o.gram.tasks.ITaskObserver;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/24/13
 */
public class FetchPhotosFromCacheCommand extends AsyncTaskRestogramCommand {
    public FetchPhotosFromCacheCommand(HttpJsonRpcClientTransport transport, ITaskObserver observer, String... ids) {
        super(transport, observer);
        this.ids = ids;
    }

    @Override
    public boolean execute() {
        if(!super.execute())
            return false;

        FetchPhotosFromCacheTask t = new FetchPhotosFromCacheTask(transport, this);
        t.execute(ids);

        task = t;

        return true;
    }

    private String[] ids;
}
