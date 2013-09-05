package rest.o.gram.tasks;

import rest.o.gram.data_favorites.results.AddPhotoToFavoritesResult;
import rest.o.gram.data_favorites.results.RemovePhotoFromFavoritesResult;
import rest.o.gram.tasks.results.*;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 05/04/13
 */
public interface ITaskObserver {

    void onFinished(GetNearbyResult result);

    void onFinished(GetInfoResult result);

    void onFinished(GetPhotosResult result);

    void onFinished(CachePhotoResult result);

    void onFinished(FetchPhotosFromCacheResult result);

    void onFinished(CacheVenueResult result);

    void onFinished(FetchVenuesFromCacheResult result);

    void onFinished(GetProfilePhotoUrlResult result);

    void onFinished(AddPhotoToFavoritesResult result);

    void onFinished(RemovePhotoFromFavoritesResult result);

    void onCanceled();

    void onError();
}
