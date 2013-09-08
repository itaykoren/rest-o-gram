package rest.o.gram.commands;

import org.json.rpc.client.HttpJsonRpcClientTransport;
import rest.o.gram.tasks.ITaskObserver;
import rest.o.gram.tasks.LogoutTask;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 9/8/13
 */
public class LogoutCommand extends AsyncTaskRestogramCommand {

    public LogoutCommand(HttpJsonRpcClientTransport transport, ITaskObserver observer) {
        super(transport, observer);
    }

    @Override
    public boolean execute() {
        if(!super.execute())
            return false;

        LogoutTask t = new LogoutTask(transport, this);
        t.execute();
        task = t;
        return true;
    }
}
