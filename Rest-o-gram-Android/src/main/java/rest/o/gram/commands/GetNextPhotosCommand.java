package rest.o.gram.commands;

import org.json.rpc.client.HttpJsonRpcClientTransport;
import rest.o.gram.tasks.GetNextPhotosTask;
import rest.o.gram.tasks.ITaskObserver;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 4/22/13
 */
public class GetNextPhotosCommand implements IRestogramCommand {
    public GetNextPhotosCommand(String token) {
        this.token = token;
    }

    @Override
    public void execute(HttpJsonRpcClientTransport transport, ITaskObserver observer) {
        GetNextPhotosTask task = new GetNextPhotosTask(transport, observer);
        task.execute(token);
    }

    private String token;
}
