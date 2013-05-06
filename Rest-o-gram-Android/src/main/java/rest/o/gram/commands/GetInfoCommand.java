package rest.o.gram.commands;

import rest.o.gram.tasks.GetInfoTask;
import rest.o.gram.tasks.ITaskObserver;
import org.json.rpc.client.HttpJsonRpcClientTransport;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 15/04/13
 */
public class GetInfoCommand extends AbstractRestogramCommand {

    public GetInfoCommand(HttpJsonRpcClientTransport transport, ITaskObserver observer,
                          String venueID) {
        super(transport, observer);
        this.venueID = venueID;
    }

    @Override
    public void execute() {
        GetInfoTask task = new GetInfoTask(transport, observer);
        task.execute(venueID);
    }

    private String venueID;
}
