package rest.o.gram.tasks;

import org.json.rpc.client.HttpJsonRpcClientTransport;
import org.json.rpc.client.JsonRpcInvoker;
import rest.o.gram.lean.UsersService;
import rest.o.gram.tasks.results.LogoutResult;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 9/8/13
 */
public class LogoutTask extends RestogramAsyncTask<String, Void, LogoutResult> {


    /**
     * Ctor
     */
    public LogoutTask(HttpJsonRpcClientTransport transport, ITaskObserver observer) {
        super(transport, observer);
    }

    @Override
    protected LogoutResult doInBackgroundImpl(String... params) {
        final JsonRpcInvoker invoker = new JsonRpcInvoker();
        final UsersService service = invoker.get(transport, "users", UsersService.class);

       return  new LogoutResultImpl(service.logout());
    }

    @Override
    protected void onPostExecute(LogoutResult result) {
        observer.onFinished(result);
    }

    class LogoutResultImpl implements LogoutResult {

        LogoutResultImpl(boolean succeded) {
            this.succeded = succeded;
        }

        @Override
        public boolean getSucceded() {
            return succeded;
        }

        private boolean succeded;
    }
}
