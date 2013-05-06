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
public class GetNextPhotosCommand extends AbstractRestogramCommand {
    public GetNextPhotosCommand(HttpJsonRpcClientTransport transport, ITaskObserver observer,
                                String token) {
        super(transport, observer);
        this.token = token;
    }

    public GetNextPhotosCommand(HttpJsonRpcClientTransport transport, ITaskObserver observer,
                                String token, RestogramFilterType filterType) {
        this(transport, observer, token);
        this.filterType = filterType;
    }

    @Override
    public void execute() {
        GetNextPhotosTask task = new GetNextPhotosTask(transport, observer);
        task.execute(token, filterType.toString());
    }

    private String token;
    private RestogramFilterType filterType = RestogramFilterType.None;
}
