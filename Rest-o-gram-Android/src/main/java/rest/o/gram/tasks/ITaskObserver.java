package rest.o.gram.tasks;

import rest.o.gram.tasks.results.*;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 05/04/13
 */
public interface ITaskObserver {

    void onFinished(GetNearbyResult venues);

    void onFinished(GetInfoResult venue);

    void onFinished(GetPhotosResult result);

    void onFinished(CachePhotoResult result);

    void onFinished(FetchPhotosFromCacheResult result);

    void onFinished(CacheVenueResult result);

    void onFinished(FetchVenuesFromCacheResult result);

    void onCanceled();
}
