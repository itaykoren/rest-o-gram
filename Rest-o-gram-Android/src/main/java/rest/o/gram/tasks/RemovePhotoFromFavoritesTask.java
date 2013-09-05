package rest.o.gram.tasks;

import android.util.Log;
import org.json.rpc.client.HttpJsonRpcClientTransport;
import org.json.rpc.client.JsonRpcInvoker;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.data_favorites.results.RemovePhotoFromFavoritesResult;
import rest.o.gram.iservice.RestogramAuthService;

/**
 * Created with IntelliJ IDEA.
 * User: Itay
 * Date: 14/06/13
 */
public class RemovePhotoFromFavoritesTask extends RestogramAsyncTask<String, Void, RemovePhotoFromFavoritesResult> {

    public RemovePhotoFromFavoritesTask(HttpJsonRpcClientTransport transport, ITaskObserver observer) {
        super(transport, observer);
    }

    @Override
    protected RemovePhotoFromFavoritesResult doInBackgroundImpl(String... params) {

        String photoId = params[0];

        JsonRpcInvoker invoker = new JsonRpcInvoker();
        RestogramAuthService service = invoker.get(transport, "restogram", RestogramAuthService.class);

        if (RestogramClient.getInstance().isDebuggable())
            Log.d("REST-O-GRAM", "removing photo from favorites");

        return safeRemovePhotoFromFavorites(service, photoId);

    }

    @Override
    protected void onPostExecute(RemovePhotoFromFavoritesResult result) {
        observer.onFinished(result);
    }

    private RemovePhotoFromFavoritesResult safeRemovePhotoFromFavorites(RestogramAuthService service, String photoId) {
        try
        {
            return new RemovePhotoFromFavoritesResult(service.removePhotoFromFavorites(photoId), photoId);
        } catch (Exception e)
        {
            Log.e("REST-O-GRAM", "REMOVING PHOTO FROM FAVORITES - FIRST ATTEMPT FAILED");
            return new RemovePhotoFromFavoritesResult(service.removePhotoFromFavorites(photoId), photoId);
        }
    }
}
