package rest.o.gram.data_favorites;

import com.leanengine.*;
import rest.o.gram.cache.IRestogramCache;
import rest.o.gram.client.IRestogramClient;
import rest.o.gram.entities.*;

import java.util.HashSet;
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
    public void updateFavoritePhotos(String photoId) {

        favoritePhotos.add(photoId);
        final IRestogramCache cache = client.getCache();
        if (cache != null) {
            final RestogramPhoto photo = cache.findPhoto(photoId);
            if (photo != null) {
                photo.set_favorite(true);
            }
        }
    }

    @Override
    public void dispose() {
        LeanEngine.dispose();
    }

//    private class TaskObserverImpl implements ITaskObserver {
//
//        @Override
//        public void onFinished(GetNearbyResult venues) { }
//
//        @Override
//        public void onFinished(GetInfoResult venue) { }
//
//        @Override
//        public void onFinished(GetPhotosResult result) { }
//
//        @Override
//        public void onFinished(CachePhotoResult result) {
//            if (!result.hasSucceded())
//            {
//                if (RestogramClient.getInstance().isDebuggable())
//                    Log.d("REST-O-GRAM", "adding photo to fav - caching failed");
//            }
//            else // caching succceded
//            {
//                if (RestogramClient.getInstance().isDebuggable())
//                    Log.d("REST-O-GRAM", "adding photo to fav - caching succeded");
//            }
//        }
//
//        @Override
//        public void onFinished(FetchPhotosFromCacheResult result) { }
//
//        @Override
//        public void onFinished(CacheVenueResult result) {
//            if (!result.hasSucceded())
//            {
//                if (RestogramClient.getInstance().isDebuggable())
//                    Log.d("REST-O-GRAM", "adding venue to fav - caching venue failed");
//            }
//            else // caching succceded
//            {
//                if (RestogramClient.getInstance().isDebuggable())
//                    Log.d("REST-O-GRAM", "adding venue to fav - caching venue succeded");
//            }
//        }
//
//        @Override
//        public void onFinished(FetchVenuesFromCacheResult result) { }
//
//        @Override
//        public void onFinished(GetProfilePhotoUrlResult result) { }
//
//        @Override
//        public void onFinished(AddPhotoToFavoritesResult result) { }
//
//        @Override
//        public void onFinished(RemovePhotoFromFavoritesResult result) { }
//
//        @Override
//        public void onFinished(GetFavoritePhotosResult result) { }
//
//        @Override
//        public void onCanceled() { }
//
//        @Override
//        public void onError() { }
//    }
//
    private IRestogramClient client;
    private Set<String> favoritePhotos;
}