package rest.o.gram.commands;

import org.json.rpc.client.HttpJsonRpcClientTransport;
import rest.o.gram.tasks.GetInfoTask;
import rest.o.gram.tasks.ITaskObserver;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 15/04/13
 */
public class GetInfoCommand extends AsyncTaskRestogramCommand {

    public GetInfoCommand(HttpJsonRpcClientTransport transport, ITaskObserver observer,
                          String venueID) {
        super(transport, observer);
        this.venueID = venueID;
    }

    @Override
    public boolean execute() {
        if(!super.execute())
            return false;

        GetInfoTask t = new GetInfoTask(transport, this);
        t.execute(venueID);
        task = t;
        return true;
    }

    private String venueID;
}
