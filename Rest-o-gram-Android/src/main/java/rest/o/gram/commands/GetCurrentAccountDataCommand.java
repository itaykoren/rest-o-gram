package rest.o.gram.commands;

import org.json.rpc.client.HttpJsonRpcClientTransport;
import rest.o.gram.authentication.IAuthenticationProvider;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.lean.LeanAccount;
import rest.o.gram.tasks.GetCurrentAccountDataTask;
import rest.o.gram.tasks.ITaskObserver;
import rest.o.gram.tasks.results.GetCurrentAccountDataResult;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 9/8/13
 */
public class GetCurrentAccountDataCommand extends AsyncTaskRestogramCommand {

    public GetCurrentAccountDataCommand(HttpJsonRpcClientTransport transport, ITaskObserver observer) {
        super(transport, observer);
    }

    @Override
    public boolean execute() {
        if(!super.execute())
            return false;

        final IAuthenticationProvider authProvider = RestogramClient.getInstance().getAuthenticationProvider();
        if (authProvider != null && authProvider.getAccountData() != null)
        {
            observer.onFinished(new GetCurrentAccountDataResultImpl(authProvider.getAccountData()));
            notifyFinished();
            return true;
        }

        GetCurrentAccountDataTask t = new GetCurrentAccountDataTask(transport, this);
        t.execute();
        task = t;
        return true;
    }

    class GetCurrentAccountDataResultImpl implements GetCurrentAccountDataResult {

        GetCurrentAccountDataResultImpl(final LeanAccount account) {
            this.account =  account;
        }

        @Override
        public LeanAccount getAccount() {
            return account;
        }

        private LeanAccount account;
    }
}
