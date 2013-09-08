package rest.o.gram.commands;

import org.json.rpc.client.HttpJsonRpcClientTransport;
import rest.o.gram.filters.RestogramFilterType;
import rest.o.gram.tasks.GetPhotosTask;
import rest.o.gram.tasks.ITaskObserver;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 15/04/13
 */
public class GetPhotosCommand extends AsyncTaskRestogramCommand {

    public GetPhotosCommand(HttpJsonRpcClientTransport transport, ITaskObserver observer,
                            String venueID) {
        super(transport, observer);
        this.venueID = venueID;
    }

    public GetPhotosCommand(HttpJsonRpcClientTransport transport, ITaskObserver observer,
                            String venueID, RestogramFilterType filterType) {
        this(transport, observer, venueID);
        this.filterType =  filterType;
    }

    @Override
    public boolean execute() {
        if(!super.execute())
            return false;

        GetPhotosTask t = new GetPhotosTask(transport, this);
        t.execute(venueID, filterType.toString());
        task = t;
        return true;
    }

    private String venueID;
    private RestogramFilterType filterType = RestogramFilterType.None;
}
