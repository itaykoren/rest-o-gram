package rest.o.gram.data_favorites;

import android.util.Log;
import com.leanengine.*;
import rest.o.gram.client.IRestogramClient;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.data_favorites.results.*;
import rest.o.gram.entities.Kinds;
import rest.o.gram.entities.Props;
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.entities.RestogramVenue;
import rest.o.gram.tasks.ITaskObserver;
import rest.o.gram.tasks.results.*;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/22/13
 */
public class DataFavoritesManager implements IDataFavoritesManager {


    public DataFavoritesManager(final IRestogramClient client) {
        this.client = client;
    }

    @Override
    public void addFavoritePhoto(final RestogramPhoto photo, final IDataFavoritesOperationsObserver observer) {
        final TaskObserverImpl internalObserver = new TaskObserverImpl(observer, photo);
        if (RestogramClient.getInstance().isDebuggable())
            Log.d("REST-O-GRAM", "adding photo to fav - caching photo");
        client.cachePhoto(photo.getInstagram_id(), internalObserver);
    }

    @Override
    public void removeFavoritePhoto(final RestogramPhoto photo, final IDataFavoritesOperationsObserver observer) {
        LeanEntity entity = Converters.photoRefToLeanEntity(photo);
        entity.put(Props.PhotoRef.IS_FAVORITE, false);
        if (RestogramClient.getInstance().isDebuggable())
            Log.d("REST-O-GRAM", "removing photo from fav - updating DS");
        entity.saveInBackground(new NetworkCallback<Long>() {
            @Override
            public void onResult(Long... result) {
                if (RestogramClient.getInstance().isDebuggable())
                    Log.d("REST-O-GRAM", "removing photo from fav - updating DS succeded");
                photo.set_favorite(false);
                observer.onFinished(new RemoveFavoritePhotosResult(true));
            }

            @Override
            public void onFailure(LeanError error) {
                if (RestogramClient.getInstance().isDebuggable())
                    Log.d("REST-O-GRAM", "removing photo from fav - updating DS failed");
                observer.onFinished(new RemoveFavoritePhotosResult(false));
            }
        });
    }

    @Override
    public void getFavoritePhotos(final IDataFavoritesOperationsObserver observer) {
        doGetFavoritePhotos(null, observer);
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
            query.addSort(Props.PhotoRef.INSTAGRAM_ID, LeanQuery.SortDirection.ASCENDING);
        }
        final TaskObserverImpl internalObserver = new TaskObserverImpl(observer, query);

        NetworkCallback<LeanEntity> callback =
                new NetworkCallback<LeanEntity>() {
                    @Override
                    public void onResult(LeanEntity... result) {
                        if (RestogramClient.getInstance().isDebuggable())
                            Log.d("REST-O-GRAM", "fetching fav photos - from DS succeded");
                        internalObserver.setPhotoRefs(result);
                        client.fetchPhotosFromCache(internalObserver, Converters.photosRefsToNames(result));
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
    public void addFavoriteVenue(final RestogramVenue venue, final IDataFavoritesOperationsObserver observer) {
        if (RestogramClient.getInstance().isDebuggable())
            Log.d("REST-O-GRAM", "adding venue to fav - caching venue");
        final TaskObserverImpl internalObserver = new TaskObserverImpl(observer, venue);
        client.cacheVenue(venue.getFoursquare_id(), internalObserver);
    }

    @Override
    public void removeFavoriteVenue(final RestogramVenue venue, final IDataFavoritesOperationsObserver observer) {
        LeanEntity entity = Converters.venueRefToLeanEntity(venue);
        entity.put(Props.VenueRef.IS_FAVORITE, false);
        if (RestogramClient.getInstance().isDebuggable())
            Log.d("REST-O-GRAM", "removing venue from fav - updating DS");
        entity.saveInBackground(new NetworkCallback<Long>() {
            @Override
            public void onResult(Long... result) {
                if (RestogramClient.getInstance().isDebuggable())
                    Log.d("REST-O-GRAM", "removing venue from fav - updating DS succeded");
                venue.setfavorite(false);
                observer.onFinished(new RemoveFavoriteVenuesResult(true));
            }

            @Override
            public void onFailure(LeanError error) {
                if (RestogramClient.getInstance().isDebuggable())
                    Log.d("REST-O-GRAM", "removing venue from fav - updating DS failed");
                observer.onFinished(new RemoveFavoriteVenuesResult(false));
            }
        });
    }

    @Override
    public void getFavoriteVenues(final IDataFavoritesOperationsObserver observer) {
        doGetFavoriteVenues(null, observer);
    }

    @Override
    public void getNextFavoriteVenues(final  GetFavoriteVenuesResult previous, final IDataFavoritesOperationsObserver observer) {
        doGetFavoriteVenues(previous, observer);
    }

    @Override
    public void dispose() {
        LeanEngine.dispose();
    }

    private void doGetFavoriteVenues(final GetFavoriteVenuesResult previous, final IDataFavoritesOperationsObserver observer) {
        LeanQuery query;
        if (previous != null) // get next
            query = previous.getQuery();
        else // no previous results
        {
            query = new LeanQuery(Kinds.VENUE_REFERENCE);
            query.addFilter(Props.VenueRef.IS_FAVORITE, LeanQuery.FilterOperator.EQUAL, true);
            query.addSort(Props.VenueRef.FOURSQUARE_ID, LeanQuery.SortDirection.ASCENDING);
        }
        final TaskObserverImpl internalObserver = new TaskObserverImpl(observer, query);

        NetworkCallback<LeanEntity> callback =
                new NetworkCallback<LeanEntity>() {
                    @Override
                    public void onResult(LeanEntity... result) {
                        if (RestogramClient.getInstance().isDebuggable())
                            Log.d("REST-O-GRAM", "fetching fav venues - from DS succeded");
                        internalObserver.setVenueRefs(result);
                        client.fetchVenuesFromCache(internalObserver, Converters.venuesRefsToNames(result));
                    }

                    @Override
                    public void onFailure(LeanError error) {
                        if (RestogramClient.getInstance().isDebuggable())
                            Log.d("REST-O-GRAM", "fetching fav venues - from DS failed:"+ error.getErrorMessage() +
                                    ", error_code:" + error.getErrorCode() + ", error_type:" + error.getErrorType());
                        observer.onFinished(new GetFavoriteVenuesResult(null, null));
                    }
                };

        if (previous != null)
        {
            if (RestogramClient.getInstance().isDebuggable())
                Log.d("REST-O-GRAM", "fetching next fav venues - from DS");
            query.fetchNextInBackground(callback);
        }
        else
        {
            if (RestogramClient.getInstance().isDebuggable())
                Log.d("REST-O-GRAM", "fetching fav venues - from DS");
            query.fetchInBackground(callback);
        }
    }

    private class TaskObserverImpl implements ITaskObserver {

        private TaskObserverImpl(final IDataFavoritesOperationsObserver observer, final RestogramPhoto photo_to_add) {
            this.observer = observer;
            this.photo_to_add = photo_to_add;
        }

        private TaskObserverImpl(final IDataFavoritesOperationsObserver observer, final RestogramVenue venue_to_add) {
            this.observer = observer;
            this.venue_to_add = venue_to_add;
        }

        private TaskObserverImpl(final IDataFavoritesOperationsObserver observer, final LeanQuery favorites_query) {
            this.observer = observer;
            this.favorites_query = favorites_query;
        }

        void  setPhotoRefs(LeanEntity... photoRefs) {
            this.photo_refs = photoRefs;
        }

        void  setVenueRefs(LeanEntity... venueRefs) {
            this.venue_refs = venueRefs;
        }

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
                observer.onFinished(new AddFavoritePhotosResult(false));
            }
            else // caching succceded
            {
                if (RestogramClient.getInstance().isDebuggable())
                    Log.d("REST-O-GRAM", "adding photo to fav - caching succeded");
                LeanEntity entity = Converters.photoRefToLeanEntity(photo_to_add);
                entity.put(Props.PhotoRef.IS_FAVORITE, true);
                if (RestogramClient.getInstance().isDebuggable())
                    Log.d("REST-O-GRAM", "adding photo to fav - saving photo ref to DS");
                entity.saveInBackground(new NetworkCallback<Long>() {

                    // operation has  fully succeded
                    @Override
                    public void onResult(Long... result) {
                        if (RestogramClient.getInstance().isDebuggable())
                            Log.d("REST-O-GRAM", "adding photo to fav - saving photo ref succeded");
                        photo_to_add.set_favorite(true);
                        photo_to_add.setId(result[0]); // TODO: should set it even if already set?
                        observer.onFinished(new AddFavoritePhotosResult(true));
                    }

                    // photo ref update has failed
                    @Override
                    public void onFailure(LeanError error) {
                        if (RestogramClient.getInstance().isDebuggable())
                            Log.d("REST-O-GRAM", "adding photo to fav - saving photo ref failed: " + error.getErrorMessage()+
                                        ", error_code:" + error.getErrorCode() + ", error_type:" + error.getErrorType());
                        observer.onFinished(new AddFavoritePhotosResult(false));
                    }
                });
            }
        }

        @Override
        public void onFinished(FetchPhotosFromCacheResult result) {
            if (result.getPhotos() == null)
            {
                if (RestogramClient.getInstance().isDebuggable())
                    Log.d("REST-O-GRAM", "fetching fav photos - from cache failed");
                observer.onFinished(new GetFavoritePhotosResult(null, null));
            }
            else
            {
                if (RestogramClient.getInstance().isDebuggable())
                    Log.d("REST-O-GRAM", "fetching fav photos - from cache succeded");
                int i = 0;
                for (final RestogramPhoto currPhoto : result.getPhotos())
                {
                    currPhoto.setId(photo_refs[i++].getId());
                    currPhoto.set_favorite(true);
                }
                observer.onFinished(new GetFavoritePhotosResult(result.getPhotos(), favorites_query));
            }
        }

        @Override
        public void onFinished(CacheVenueResult result) {
            if (!result.hasSucceded())
            {
                if (RestogramClient.getInstance().isDebuggable())
                    Log.d("REST-O-GRAM", "adding venue to fav - caching venue failed");
                observer.onFinished(new AddFavoriteVenuesResult(false));
            }
            else // caching succceded
            {
                if (RestogramClient.getInstance().isDebuggable())
                    Log.d("REST-O-GRAM", "adding venue to fav - caching venue suceded");
                LeanEntity entity = Converters.venueRefToLeanEntity(venue_to_add);
                entity.put(Props.VenueRef.IS_FAVORITE, true);
                if (RestogramClient.getInstance().isDebuggable())
                    Log.d("REST-O-GRAM", "adding venue to fav - saving venue ref to DS");
                entity.saveInBackground(new NetworkCallback<Long>() {

                    // operation has  fully succeded
                    @Override
                    public void onResult(Long... result) {
                        if (RestogramClient.getInstance().isDebuggable())
                            Log.d("REST-O-GRAM", "adding venue to fav - saving venue ref succeded");
                        venue_to_add.setfavorite(true);
                        venue_to_add.setId(result[0]); // TODO: should set it even if already set?
                        observer.onFinished(new AddFavoriteVenuesResult(true));
                    }

                    // venue ref update has failed
                    @Override
                    public void onFailure(LeanError error) {
                        if (RestogramClient.getInstance().isDebuggable())
                            Log.d("REST-O-GRAM", "adding venue to fav - saving venue ref failed:" + error.getErrorMessage() +
                                                    ", error_code:" + error.getErrorCode() + ", error_type:" + error.getErrorType());
                        observer.onFinished(new AddFavoriteVenuesResult(false));
                    }
                });
            }
        }

        @Override
        public void onFinished(FetchVenuesFromCacheResult result) {
            if (result.getVenues() == null)
            {
                if (RestogramClient.getInstance().isDebuggable())
                    Log.d("REST-O-GRAM", "fetching fav venues - from cache failed");
                observer.onFinished(new GetFavoriteVenuesResult(null, null));
            }
            else
            {
                if (RestogramClient.getInstance().isDebuggable())
                    Log.d("REST-O-GRAM", "fetching fav venues - from cache succeded");
                int i = 0;
                for (final RestogramVenue currVenue : result.getVenues())
                {
                    currVenue.setId(venue_refs[i++].getId());
                    currVenue.setfavorite(true);
                }
                observer.onFinished(new GetFavoriteVenuesResult(result.getVenues(), favorites_query));
            }
        }

        @Override
        public void onCanceled() {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        private IDataFavoritesOperationsObserver observer;
        private RestogramPhoto photo_to_add;
        private RestogramVenue venue_to_add;
        private LeanQuery favorites_query;
        private LeanEntity[] photo_refs;
        private LeanEntity[] venue_refs;
    }

    private IRestogramClient client;
}
