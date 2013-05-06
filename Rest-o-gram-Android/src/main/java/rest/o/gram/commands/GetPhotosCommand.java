package rest.o.gram.commands;

import rest.o.gram.filters.RestogramFilterType;
import rest.o.gram.tasks.GetPhotosTask;
import rest.o.gram.tasks.ITaskObserver;
import org.json.rpc.client.HttpJsonRpcClientTransport;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 15/04/13
 */
public class GetPhotosCommand extends AbstractRestogramCommand {

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
    public void execute() {
        GetPhotosTask task = new GetPhotosTask(transport, observer);
        task.execute(venueID, filterType.toString());
    }

    private String venueID;
    private RestogramFilterType filterType = RestogramFilterType.None;
}
