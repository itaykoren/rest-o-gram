package rest.o.gram.tasks;

import android.os.AsyncTask;
import android.util.Log;
import org.json.rpc.client.HttpJsonRpcClientTransport;
import org.json.rpc.client.JsonRpcInvoker;
import rest.o.gram.iservice.RestogramService;
import rest.o.gram.tasks.results.CacheVenueResult;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/24/13
 */
public class CacheVenueTask extends AsyncTask<String,Void,CacheVenueResult> {
    public CacheVenueTask(HttpJsonRpcClientTransport transport, ITaskObserver observer) {
        this.transport = transport;
        this.observer = observer;
    }

    @Override
    protected CacheVenueResult doInBackground(String... params) {
        JsonRpcInvoker invoker = new JsonRpcInvoker();
        RestogramService service = invoker.get(transport, "restogram", RestogramService.class);
        return safeCacheVenue(service, params[0]);
    }

    @Override
    protected void onPostExecute(CacheVenueResult result) {
        observer.onFinished(result);
    }

    private CacheVenueResult safeCacheVenue(RestogramService service, String id) {
        try
        {
            return new CacheVenueResultImpl(service.cacheVenue(id));
        }
        catch (Exception e)
        {
            Log.e("REST-O-GRAM", "CACHE VENUE - FIRST ATTEMPT FAILED");
            e.printStackTrace();
            return new CacheVenueResultImpl(service.cacheVenue(id));
        }
    }

    class CacheVenueResultImpl implements CacheVenueResult {

        CacheVenueResultImpl(boolean hasSucceded) {
            this.hasSucceded = hasSucceded;
        }

        @Override
        public boolean hasSucceded() {
            return hasSucceded;
        }

        private boolean hasSucceded;
    }

    private HttpJsonRpcClientTransport transport;
    private ITaskObserver observer;
}
