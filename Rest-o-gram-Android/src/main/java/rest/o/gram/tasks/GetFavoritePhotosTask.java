package rest.o.gram.tasks;

import android.util.Log;
import org.json.rpc.client.HttpJsonRpcClientTransport;
import org.json.rpc.client.JsonRpcInvoker;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.data_favorites.results.GetFavoritePhotosResult;
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.iservice.RestogramAuthService;
import rest.o.gram.results.PhotosResult;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Itay
 * Date: 07/09/13
 */
public class GetFavoritePhotosTask extends RestogramAsyncTask<String, Void, GetFavoritePhotosResult> {

    public GetFavoritePhotosTask(HttpJsonRpcClientTransport transport, ITaskObserver observer) {
        super(transport, observer);
    }

    @Override
    protected GetFavoritePhotosResult doInBackgroundImpl(String... params) {
        JsonRpcInvoker invoker = new JsonRpcInvoker();
        RestogramAuthService service = invoker.get(transport, "restogram", RestogramAuthService.class);

        if (RestogramClient.getInstance().isDebuggable())
            Log.d("REST-O-GRAM", "fetching favorite photos");

        return safeGetFavoritePhotos(service, params);
    }

    @Override
    protected void onPostExecute(GetFavoritePhotosResult result) {
        observer.onFinished(result);
    }

    private GetFavoritePhotosResult safeGetFavoritePhotos(RestogramAuthService service, String... params) {

        String token = params[0];
        PhotosResult result;
        List<RestogramPhoto> photos;
        try {

            result = service.getFavoritePhotos(token);

            if (result == null)
                return null;

            photos = result.getPhotos() == null ? new LinkedList<RestogramPhoto>() : Arrays.asList(result.getPhotos());

            if (RestogramClient.getInstance().isDebuggable())
                Log.d("REST-O-GRAM", "got " + photos.size() + "favorite photos");

            return new GetFavoritePhotosResult(photos, result.getToken());

        } catch (Exception e) {

            Log.e("REST-O-GRAM", "GET FAVORITE PHOTOS - FIRST ATTEMPT FAILED");
            e.printStackTrace();

            result = service.getFavoritePhotos(token);

            if (result == null)
                return null;

            photos = result.getPhotos() == null ? new LinkedList<RestogramPhoto>() : Arrays.asList(result.getPhotos());

            return new GetFavoritePhotosResult(photos, result.getToken());
        }
    }
}

