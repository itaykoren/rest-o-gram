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
        favoriteVenues = new HashSet<>();
        favoritePhotos = new HashSet<>();
    }

    @Override
    public void getFavoritePhotos(final IDataFavoritesOperationsObserver observer) {
        doGetFavoritePhotos(null, observer);
    }

    @Override
    public void addFavoritePhoto(String photoId) {
        favoritePhotos.add(photoId);
    }

    @Override
    public boolean removeFavoritePhoto(String photoId) {
        return favoritePhotos.remove(photoId);
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

                                IRestogramCache cache = RestogramClient.getInstance().getCache();
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
    public void addFavoriteVenue(final RestogramVenue venue, final IDataFavoritesOperationsObserver observer) {
        final TaskObserverImpl internalObserver = new TaskObserverImpl();
        final LeanEntity entity = Converters.venueRefToLeanEntity(venue);
        entity.put(Props.VenueRef.IS_FAVORITE, true);
        if (RestogramClient.getInstance().isDebuggable())
            Log.d("REST-O-GRAM", "adding venue to fav - saving venue ref to DS");
        entity.saveInBackground(new NetworkCallback<Long>() {
            // operation has fully succeeded
            @Override
            public void onResult(Long... result) {
                if (RestogramClient.getInstance().isDebuggable())
                    Log.d("REST-O-GRAM", "adding venue to fav - saving venue ref succeded");
                venue.setfavorite(true);
                venue.setId(result[0]);
                favoriteVenues.add(venue.getFoursquare_id());
                observer.onFinished(new AddVenueToFavoritesResult(true, venue.getFoursquare_id()));
                // TODO - consider removing as now the server caches every venue in getInfo
                client.cacheVenue(venue.getFoursquare_id(), internalObserver);
            }

            // venue ref update has failed
            @Override
            public void onFailure(LeanError error) {
                if (RestogramClient.getInstance().isDebuggable())
                    Log.d("REST-O-GRAM", "adding venue to fav - saving venue ref failed:" + error.getErrorMessage() +
                            ", error_code:" + error.getErrorCode() + ", error_type:" + error.getErrorType());
                observer.onFinished(new AddVenueToFavoritesResult(false, null));
            }
        });
    }

    @Override
    public void removeFavoriteVenue(final RestogramVenue venue, final IDataFavoritesOperationsObserver observer) {
        final LeanEntity entity = Converters.venueRefToLeanEntity(venue);
        entity.put(Props.VenueRef.IS_FAVORITE, false);
        if (RestogramClient.getInstance().isDebuggable())
            Log.d("REST-O-GRAM", "removing venue from fav - updating DS");
        entity.saveInBackground(new NetworkCallback<Long>() {
            @Override
            public void onResult(Long... result) {
                if (RestogramClient.getInstance().isDebuggable())
                    Log.d("REST-O-GRAM", "removing venue from fav - updating DS succeded");
                venue.setfavorite(false);
                favoriteVenues.remove(venue.getFoursquare_id());
                observer.onFinished(new RemoveVenueFromFavoritesResult(true, venue.getFoursquare_id()));
            }

            @Override
            public void onFailure(LeanError error) {
                if (RestogramClient.getInstance().isDebuggable())
                    Log.d("REST-O-GRAM", "removing venue from fav - updating DS failed");
                observer.onFinished(new RemoveVenueFromFavoritesResult(false, null));
            }
        });
    }

    @Override
    public void getFavoriteVenues(final IDataFavoritesOperationsObserver observer) {
        doGetFavoriteVenues(null, observer);
    }

    @Override
    public Set<String> getFavoriteVenues() {
        return favoriteVenues;
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
            query.setReference(new QueryReference(Props.VenueRef.FOURSQUARE_ID, Kinds.VENUE));
        }

        final LeanQuery actualQuery = query;
        NetworkCallback<LeanEntity> callback =
                new NetworkCallback<LeanEntity>() {
                    @Override
                    public void onResult(LeanEntity... result) {
                        if (RestogramClient.getInstance().isDebuggable())
                            Log.d("REST-O-GRAM", "fetching fav venues - from DS succeded");

                         final List<RestogramVenue> venues =
                                 result == null ? null :  Converters.leanEntitiesToVenues(result);

                        if(venues != null) {
                            favoriteVenues.clear();
                            for(RestogramVenue v : venues) {
                                favoriteVenues.add(v.getFoursquare_id());

                                IRestogramCache cache = RestogramClient.getInstance().getCache();
                                if(cache != null) {
                                    RestogramVenue venue = cache.findVenue(v.getFoursquare_id());
                                    if(venue != null)
                                        venue.setfavorite(true);
                                }
                            }
                        }

                        observer.onFinished(new GetFavoriteVenuesResult(venues, actualQuery));
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
        public void onCanceled() {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    private IRestogramClient client;
    private Set<String> favoriteVenues;
    private Set<String> favoritePhotos;
}