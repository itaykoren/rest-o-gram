package rest.o.gram.commands;

import org.json.rpc.client.HttpJsonRpcClientTransport;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.tasks.GetFavoritePhotosTask;
import rest.o.gram.tasks.ITaskObserver;

/**
 * Created with IntelliJ IDEA.
 * User: Itay
 * Date: 07/09/13
 */
public class GetFavoritePhotosCommand extends AsyncTaskRestogramCommand {

    public GetFavoritePhotosCommand(HttpJsonRpcClientTransport transport, ITaskObserver observer, String token) {
        super(transport, observer);
        this.token = token;
    }

    @Override
    public boolean execute() {
        if(!super.execute())
            return false;

        GetFavoritePhotosTask t = new GetFavoritePhotosTask(transport, this);
        t.executeOnExecutor(RestogramClient.getInstance().getExecutor(), token);
        task = t;
        return true;
    }

    private String token;

}
