package rest.o.gram.commands;

import org.json.rpc.client.HttpJsonRpcClientTransport;
import rest.o.gram.tasks.GetRecentPhotosTask;
import rest.o.gram.tasks.ITaskObserver;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/19/13
 */
public class GetRecentPhotosCommand extends AbstractRestogramCommand {
    public GetRecentPhotosCommand(HttpJsonRpcClientTransport transport, ITaskObserver observer) {
        super(transport, observer);
    }

    @Override
    public void execute() {
        GetRecentPhotosTask task = new GetRecentPhotosTask(transport, observer);
        task.execute();
    }
}
