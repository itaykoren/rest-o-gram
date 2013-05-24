package rest.o.gram.commands;

import com.leanengine.LeanEngine;
import org.json.rpc.client.HttpJsonRpcClientTransport;
import rest.o.gram.tasks.GetRecentPhotosTask;
import rest.o.gram.tasks.ITaskObserver;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/19/13
 */
public class GetRecentPhotosCommand extends AsyncTaskRestogramCommand {
    public GetRecentPhotosCommand(HttpJsonRpcClientTransport transport, ITaskObserver observer) {
        super(transport, observer);
    }

    @Override
    public boolean execute() {
        if(!super.execute())
            return false;

        GetRecentPhotosTask t = new GetRecentPhotosTask(transport, this, LeanEngine.getAuthToken());
        t.execute();
        task = t;
        return true;
    }
}
