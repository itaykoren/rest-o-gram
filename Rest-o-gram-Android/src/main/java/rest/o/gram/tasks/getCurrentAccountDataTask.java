package rest.o.gram.tasks;

import android.util.Log;
import com.leanengine.JsonDecode;
import com.leanengine.LeanException;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.rpc.client.HttpJsonRpcClientTransport;
import org.json.rpc.client.JsonRpcInvoker;
import rest.o.gram.lean.LeanAccount;
import rest.o.gram.lean.UsersService;
import rest.o.gram.tasks.results.GetCurrentAccountDataResult;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 9/8/13
 */
public class GetCurrentAccountDataTask extends RestogramAsyncTask<String, Void, GetCurrentAccountDataResult> {

    public GetCurrentAccountDataTask(HttpJsonRpcClientTransport transport, ITaskObserver observer) {
        super(transport, observer);
    }

    @Override
    protected GetCurrentAccountDataResult doInBackgroundImpl(String... params) {

        final  JsonRpcInvoker invoker = new JsonRpcInvoker();
        final UsersService service = invoker.get(transport, "users", UsersService.class);

        LeanAccount account = null;
        try
        {
            final String accountJSONString = service.getCurrentAccountData();
            final JSONObject accountJSON = new JSONObject(accountJSONString);
            account = JsonDecode.accountFromJson(accountJSON);

        }
        catch (LeanException|JSONException e)
        {
            Log.e("REST-O-GRAM", "cannot convert json string to lean account: " + e.getMessage());
            return null;
        }
        return new GetCurrentAccountDataResultImpl(account);
    }

    @Override
    protected void onPostExecute(GetCurrentAccountDataResult result) {
        observer.onFinished(result);
    }

    class GetCurrentAccountDataResultImpl implements GetCurrentAccountDataResult  {

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
