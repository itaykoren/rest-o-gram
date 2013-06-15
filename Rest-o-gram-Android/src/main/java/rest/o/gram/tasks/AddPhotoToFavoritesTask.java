package rest.o.gram.tasks;

import android.os.AsyncTask;
import android.util.Log;
import org.json.rpc.client.HttpJsonRpcClientTransport;
import org.json.rpc.client.JsonRpcInvoker;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.data_favorites.results.AddPhotoToFavoritesResult;
import rest.o.gram.iservice.RestogramAuthService;
import rest.o.gram.iservice.RestogramService;

/**
 * Created with IntelliJ IDEA.
 * User: Itay
 * Date: 14/06/13
 */
public class AddPhotoToFavoritesTask extends AsyncTask<String, Void, AddPhotoToFavoritesResult> {

    public AddPhotoToFavoritesTask(HttpJsonRpcClientTransport transport, ITaskObserver observer) {
        this.transport = transport;
        this.observer = observer;
    }

    @Override
    protected AddPhotoToFavoritesResult doInBackground(String... params) {

        String photoId = params[0];

        JsonRpcInvoker invoker = new JsonRpcInvoker();
        RestogramAuthService service = invoker.get(transport, "restogram", RestogramAuthService.class);

        if (RestogramClient.getInstance().isDebuggable())
            Log.d("REST-O-GRAM", "adding photo to favorites");
        service.addPhotoToFavorites(photoId);

        return safeAddPhotoToFavorites(service, photoId);

    }

    @Override
    protected void onPostExecute(AddPhotoToFavoritesResult result) {
        observer.onFinished(result);
    }

    private AddPhotoToFavoritesResult safeAddPhotoToFavorites(RestogramAuthService service, String photoId) {
        try {
            return new AddPhotoToFavoritesResult(service.addPhotoToFavorites(photoId), photoId);
        } catch (Exception e) {
            Log.e("REST-O-GRAM", "ADDING PHOTO TO FAVORITES - FIRST ATTEMPT FAILED");
            return new AddPhotoToFavoritesResult(service.addPhotoToFavorites(photoId), photoId);
        }
    }

    private HttpJsonRpcClientTransport transport;
    private ITaskObserver observer;
}
