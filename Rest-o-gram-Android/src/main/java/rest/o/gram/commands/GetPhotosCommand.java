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
public class GetPhotosCommand implements IRestogramCommand {

    public GetPhotosCommand(String venueID) {
        this.venueID = venueID;
    }

    public GetPhotosCommand(String venueID, RestogramFilterType filterType) {
        this.venueID = venueID;
        this.filterType =  filterType;
    }

    @Override
    public void execute(HttpJsonRpcClientTransport transport, ITaskObserver observer) {
        GetPhotosTask task = new GetPhotosTask(transport, observer);
        task.execute(venueID, filterType.toString());
    }

    private String venueID;
    private RestogramFilterType filterType = RestogramFilterType.None;
}
