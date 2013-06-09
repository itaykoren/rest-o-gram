package rest.o.gram.commands;

import org.json.rpc.client.HttpJsonRpcClientTransport;
import rest.o.gram.tasks.GetProfilePhotoUrlTask;
import rest.o.gram.tasks.ITaskObserver;
import rest.o.gram.tasks.results.GetProfilePhotoUrlResult;

/**
 * Created with IntelliJ IDEA.
 * User: Itay
 * Date: 09/06/13
 */
public class GetProfilePhotoUrlCommand extends AsyncTaskRestogramCommand {

    public GetProfilePhotoUrlCommand(HttpJsonRpcClientTransport transport, ITaskObserver observer, String facebookId) {
        super(transport, observer);
        this.facebookId = facebookId;
    }

    @Override
    public boolean execute() {
        if(!super.execute())
            return false;

        GetProfilePhotoUrlTask t = new GetProfilePhotoUrlTask(transport, this);
        t.execute(facebookId);
        task = t;
        return true;
    }

    private String facebookId; // facebook Id

}
