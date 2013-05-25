package rest.o.gram.tasks;

import android.os.AsyncTask;
import android.util.Log;
import org.json.rpc.client.HttpJsonRpcClientTransport;
import org.json.rpc.client.JsonRpcInvoker;
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.iservice.RestogramService;
import rest.o.gram.tasks.results.FetchPhotosFromCacheResult;

import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/24/13
 */
public class FetchPhotosFromCacheTask extends AsyncTask<String,Void,FetchPhotosFromCacheResult> {
    public FetchPhotosFromCacheTask(HttpJsonRpcClientTransport transport, ITaskObserver observer) {
        this.transport = transport;
        this.observer = observer;
    }

    @Override
    protected FetchPhotosFromCacheResult doInBackground(String... params) {
        JsonRpcInvoker invoker = new JsonRpcInvoker();
        RestogramService service = invoker.get(transport, "restogram", RestogramService.class);
        return safeFetchPhotosFromCache(service, params);
    }

    @Override
    protected void onPostExecute(FetchPhotosFromCacheResult result) {
        observer.onFinished(result);
    }

    private FetchPhotosFromCacheResult safeFetchPhotosFromCache(RestogramService service, String... ids) {
        try
        {
            return new FetchPhotosFromCacheResultImpl(service.fetchPhotosFromCache(ids));
        }
        catch (Exception e)
        {
            Log.e("REST-O-GRAM", "FETCH PHOTOS - FIRST ATTEMPT FAILED");
            e.printStackTrace();
            return new FetchPhotosFromCacheResultImpl(service.fetchPhotosFromCache(ids));
        }
    }

    class FetchPhotosFromCacheResultImpl implements FetchPhotosFromCacheResult {

        FetchPhotosFromCacheResultImpl(RestogramPhoto[] photos) {
            this.photos = Arrays.asList(photos);
            if (this.photos != null)
            {
                for (RestogramPhoto currPhoto : this.photos)
                    currPhoto.decodeStrings();
            }
        }

        @Override
        public List<RestogramPhoto> getPhotos() {
            return photos;
        }

        private List<RestogramPhoto> photos;
    }

    private HttpJsonRpcClientTransport transport;
    private ITaskObserver observer;
}
