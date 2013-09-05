package rest.o.gram.tasks;

import android.util.Log;
import org.json.rpc.client.HttpJsonRpcClientTransport;
import org.json.rpc.client.JsonRpcInvoker;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.data_favorites.results.AddPhotoToFavoritesResult;
import rest.o.gram.iservice.RestogramAuthService;

/**
 * Created with IntelliJ IDEA.
 * User: Itay
 * Date: 14/06/13
 */
public class AddPhotoToFavoritesTask extends RestogramAsyncTask<String, Void, AddPhotoToFavoritesResult> {

    public AddPhotoToFavoritesTask(HttpJsonRpcClientTransport transport, ITaskObserver observer) {
        super(transport, observer);
    }

    @Override
    protected AddPhotoToFavoritesResult doInBackgroundImpl(String... params) {

        final String photoId = params[0];
        final  String originVenueId =  params[1];

        JsonRpcInvoker invoker = new JsonRpcInvoker();
        RestogramAuthService service = invoker.get(transport, "restogram", RestogramAuthService.class);

        if (RestogramClient.getInstance().isDebuggable())
            Log.d("REST-O-GRAM", "adding photo to favorites");

        return safeAddPhotoToFavorites(service, photoId, originVenueId);

    }

    @Override
    protected void onPostExecute(AddPhotoToFavoritesResult result) {
        observer.onFinished(result);
    }

    private AddPhotoToFavoritesResult safeAddPhotoToFavorites(RestogramAuthService service, String photoId,
                                                              String originVenueId) {
        try {
            return new AddPhotoToFavoritesResult(service.addPhotoToFavorites(photoId, originVenueId), photoId);
        } catch (Exception e) {
            Log.e("REST-O-GRAM", "ADDING PHOTO TO FAVORITES - FIRST ATTEMPT FAILED");
            return new AddPhotoToFavoritesResult(service.addPhotoToFavorites(photoId, originVenueId), photoId);
        }
    }
}
