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
public class GetNextPhotosCommand extends AsyncTaskRestogramCommand {
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
    public boolean execute() {
        if(!super.execute())
            return false;

        GetNextPhotosTask t = new GetNextPhotosTask(transport, this);
        t.execute(token, filterType.toString());
        task = t;
        return true;
    }

    private String token;
    private RestogramFilterType filterType = RestogramFilterType.None;
}
