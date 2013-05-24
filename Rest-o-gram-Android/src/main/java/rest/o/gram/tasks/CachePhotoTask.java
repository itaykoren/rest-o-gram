package rest.o.gram.tasks;

import android.os.AsyncTask;
import android.util.Log;
import org.json.rpc.client.HttpJsonRpcClientTransport;
import org.json.rpc.client.JsonRpcInvoker;
import rest.o.gram.iservice.RestogramService;
import rest.o.gram.tasks.results.CachePhotoResult;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/24/13
 */
public class CachePhotoTask extends AsyncTask<String,Void,CachePhotoResult> {
    public CachePhotoTask(HttpJsonRpcClientTransport transport, ITaskObserver observer) {
        this.transport = transport;
        this.observer = observer;
    }

    @Override
    protected CachePhotoResult doInBackground(String... params) {
        JsonRpcInvoker invoker = new JsonRpcInvoker();
        RestogramService service = invoker.get(transport, "restogram", RestogramService.class);
        return safeCachePhoto(service, params[0]);
    }

    @Override
    protected void onPostExecute(CachePhotoResult result) {
        observer.onFinished(result);
    }

    private CachePhotoResult safeCachePhoto(RestogramService service, String id) {
        try
        {
            return new CachePhotoResultImpl(service.cachePhoto(id));
        }
        catch (Exception e)
        {
            Log.e("REST-O-GRAM", "CACHE PHOTO - FIRST ATTEMPT FAILED");
            e.printStackTrace();
            return new CachePhotoResultImpl(service.cachePhoto(id));
        }
    }

    class CachePhotoResultImpl implements CachePhotoResult {

        CachePhotoResultImpl(boolean hasSucceded) {
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
