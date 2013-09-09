package rest.o.gram.commands;

import org.json.rpc.client.HttpJsonRpcClientTransport;
import rest.o.gram.client.RestogramClient;
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
                                String token, String originVenueId) {
        super(transport, observer);
        this.token = token;
        this.originVenueId = originVenueId;
    }

    public GetNextPhotosCommand(HttpJsonRpcClientTransport transport, ITaskObserver observer,
                                String token, RestogramFilterType filterType, String originVenueId) {
        this(transport, observer, token, originVenueId);
        this.filterType = filterType;
    }

    @Override
    public boolean execute() {
        if(!super.execute())
            return false;

        GetNextPhotosTask t = new GetNextPhotosTask(transport, this);
        t.executeOnExecutor(RestogramClient.getInstance().getExecutor(), token, filterType.toString(), originVenueId);
        task = t;
        return true;
    }

    private String token;
    private RestogramFilterType filterType = RestogramFilterType.None;
    private String originVenueId;
}
