package rest.o.gram.data_favorites;

import android.util.Log;
import com.leanengine.*;
import rest.o.gram.cache.IRestogramCache;
import rest.o.gram.client.IRestogramClient;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.data_favorites.results.*;
import rest.o.gram.entities.*;
import rest.o.gram.tasks.ITaskObserver;
import rest.o.gram.tasks.results.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/22/13
 */
public class DataFavoritesManager implements IDataFavoritesManager {


    public DataFavoritesManager(final IRestogramClient client) {
        this.client = client;
        favoritePhotos = new HashSet<>();
    }

    @Override
    public void getFavoritePhotos(final IDataFavoritesOperationsObserver observer) {
        doGetFavoritePhotos(null, observer);
    }

    @Override
    public void addFavoritePhoto(String photoId) {
        favoritePhotos.add(photoId);
        final IRestogramCache cache =  client.getCache();
        if (cache != null)
        {
            final RestogramPhoto photo = cache.findPhoto(photoId);
            if (photo != null)
            {
                photo.set_favorite(true);
                photo.setYummies(photo.getYummies() + 1);
            }
        }
    }

    @Override
    public boolean removeFavoritePhoto(String photoId) {
        if (!favoritePhotos.remove(photoId))
            return false;

        final IRestogramCache cache =  client.getCache();
        if (cache != null)
        {
            final RestogramPhoto photo = cache.findPhoto(photoId);
            if (photo != null)
            {
                photo.set_favorite(false);
                photo.setYummies(photo.getYummies() - 1);
                return true;
            }
        }
        return  false;
    }

    @Override
    public Set<String> getFavoritePhotos() {
        return favoritePhotos;
    }

    @Override
    public void getNextFavoritePhotos(final GetFavoritePhotosResult previous, final IDataFavoritesOperationsObserver observer) {
        doGetFavoritePhotos(previous, observer);
    }

    private void doGetFavoritePhotos(final GetFavoritePhotosResult previous, final IDataFavoritesOperationsObserver observer) {
        LeanQuery query;
        if (previous != null) // get next
            query = previous.getQuery();
        else // no previous results
        {
            query = new LeanQuery(Kinds.PHOTO_REFERENCE);
            query.addFilter(Props.PhotoRef.IS_FAVORITE, LeanQuery.FilterOperator.EQUAL, true);
            query.setReference(new QueryReference(Props.PhotoRef.INSTAGRAM_ID,  Kinds.PHOTO));
        }

        final LeanQuery actualQuery = query;
        NetworkCallback<LeanEntity> callback =
                new NetworkCallback<LeanEntity>() {
                    @Override
                    public void onResult(LeanEntity... result) {
                        if (RestogramClient.getInstance().isDebuggable())
                            Log.d("REST-O-GRAM", "fetching fav photos - from DS succeded");

                        final List<RestogramPhoto> photos =
                                result == null ? null : Converters.leanEntitiesToPhotos(result);

                        if(photos != null) {
                            favoritePhotos.clear();
                            for(RestogramPhoto p : photos) {
                                favoritePhotos.add(p.getInstagram_id());

                                IRestogramCache cache = client.getCache();
                                if(cache != null) {
                                    RestogramPhoto photo = cache.findPhoto(p.getInstagram_id());
                                    if(photo != null)
                                        photo.set_favorite(true);
                                }
                            }
                        }

                        observer.onFinished(new GetFavoritePhotosResult(photos, actualQuery));
                    }

                    @Override
                    public void onFailure(LeanError error) {
                        if (RestogramClient.getInstance().isDebuggable())
                            Log.d("REST-O-GRAM", "fetching fav photos - from DS failed:"+ error.getErrorMessage() +
                                    ", error_code:" + error.getErrorCode() + ", error_type:" + error.getErrorType());
                        observer.onFinished(new GetFavoritePhotosResult(null, null));
                    }
                };

        if (previous != null)
        {
            if (RestogramClient.getInstance().isDebuggable())
                Log.d("REST-O-GRAM", "fetching next fav photos - from DS");
            query.fetchNextInBackground(callback);
        }
        else
        {
            if (RestogramClient.getInstance().isDebuggable())
                Log.d("REST-O-GRAM", "fetching fav photos - from DS");
            query.fetchInBackground(callback);
        }
    }

    @Override
    public void dispose() {
        LeanEngine.dispose();
    }

    private class TaskObserverImpl implements ITaskObserver {

        @Override
        public void onFinished(GetNearbyResult venues) { }

        @Override
        public void onFinished(GetInfoResult venue) { }

        @Override
        public void onFinished(GetPhotosResult result) { }

        @Override
        public void onFinished(CachePhotoResult result) {
            if (!result.hasSucceded())
            {
                if (RestogramClient.getInstance().isDebuggable())
                    Log.d("REST-O-GRAM", "adding photo to fav - caching failed");
            }
            else // caching succceded
            {
                if (RestogramClient.getInstance().isDebuggable())
                    Log.d("REST-O-GRAM", "adding photo to fav - caching succeded");
            }
        }

        @Override
        public void onFinished(FetchPhotosFromCacheResult result) { }

        @Override
        public void onFinished(CacheVenueResult result) {
            if (!result.hasSucceded())
            {
                if (RestogramClient.getInstance().isDebuggable())
                    Log.d("REST-O-GRAM", "adding venue to fav - caching venue failed");
            }
            else // caching succceded
            {
                if (RestogramClient.getInstance().isDebuggable())
                    Log.d("REST-O-GRAM", "adding venue to fav - caching venue succeded");
            }
        }

        @Override
        public void onFinished(FetchVenuesFromCacheResult result) { }

        @Override
        public void onFinished(GetProfilePhotoUrlResult result) { }

        @Override
        public void onFinished(AddPhotoToFavoritesResult result) { }

        @Override
        public void onFinished(RemovePhotoFromFavoritesResult result) { }

        @Override
        public void onCanceled() { }

        @Override
        public void onError() { }
    }

    private IRestogramClient client;
    private Set<String> favoritePhotos;
}