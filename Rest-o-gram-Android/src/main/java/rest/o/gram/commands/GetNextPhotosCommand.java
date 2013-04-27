package rest.o.gram.commands;

import org.json.rpc.client.HttpJsonRpcClientTransport;
import rest.o.gram.filters.RestogramFilterType;
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

    public GetNextPhotosCommand(String token, RestogramFilterType filterType) {
        this.token = token;
        this.filterType = filterType;
    }

    @Override
    public void execute(HttpJsonRpcClientTransport transport, ITaskObserver observer) {
        GetNextPhotosTask task = new GetNextPhotosTask(transport, observer);
        task.execute(token, filterType.toString());
    }

    private String token;
    private RestogramFilterType filterType = RestogramFilterType.None;
}
