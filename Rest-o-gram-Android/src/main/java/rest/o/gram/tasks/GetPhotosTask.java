package rest.o.gram.tasks;

import android.os.AsyncTask;
import android.util.Log;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.filters.RestogramFilterType;
import rest.o.gram.results.PhotosResult;
import rest.o.gram.iservice.RestogramService;
import org.json.rpc.client.HttpJsonRpcClientTransport;
import org.json.rpc.client.JsonRpcInvoker;
import rest.o.gram.tasks.results.GetPhotosResult;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 4/5/13
 */
public class GetPhotosTask extends AsyncTask<String, Void, GetPhotosResult> {

    public GetPhotosTask(HttpJsonRpcClientTransport transport, ITaskObserver observer) {
        this.transport = transport;
        this.observer = observer;
    }

    @Override
    protected GetPhotosResult doInBackground(String... params) {
        String venueID = params[0];
        RestogramFilterType filterType = RestogramFilterType.valueOf(params[1]);

        JsonRpcInvoker invoker = new JsonRpcInvoker();
        RestogramService service = invoker.get(transport, "restogram", RestogramService.class);

        if (RestogramClient.getInstance().isDebuggable())
            Log.d("REST-O-GRAM", "Getting photos");

        PhotosResult result = safeGetPhotos(service, venueID, filterType);
        if (result == null)
            return null;
        return (new GetPhotosResultImpl(result.getPhotos(), result.getToken()));
    }

    private PhotosResult safeGetPhotos(RestogramService service, String venueID, RestogramFilterType filterType) {
        try
        {
            return service.getPhotos(venueID, filterType);
        }
        catch (Exception e)
        {
            Log.e("REST-O-GRAM", "GET PHOTOS - FIRST ATTEMPT FAILED");
            e.printStackTrace();
            return service.getPhotos(venueID, filterType);
        }
    }

    @Override
    protected void onPostExecute(GetPhotosResult result) {
        if (RestogramClient.getInstance().isDebuggable())
            Log.d("REST-O-GRAM", "Got photos");
        observer.onFinished(result);
    }

    @Override
    protected void onCancelled() {
        observer.onCanceled();
    }

    protected class GetPhotosResultImpl implements GetPhotosResult {
        public GetPhotosResultImpl(RestogramPhoto[] photos, String token) {
            this.photos = photos;
            this.token = token;
        }

        @Override
        public RestogramPhoto[] getPhotos() {
            return photos;
        }

        @Override
        public String getToken() {
            return token;
        }

        private RestogramPhoto[] photos;
        private String token;
    }


    protected HttpJsonRpcClientTransport transport;
    protected ITaskObserver observer;
}

