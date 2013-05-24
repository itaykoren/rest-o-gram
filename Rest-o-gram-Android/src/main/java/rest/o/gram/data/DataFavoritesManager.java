package rest.o.gram.data;

import com.leanengine.*;
import rest.o.gram.client.IRestogramClient;
import rest.o.gram.data.results.*;
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
        client.cachePhoto(photo.getInstagram_id(), internalObserver);
    }

    @Override
    public void removeFavoritePhoto(final RestogramPhoto photo, final IDataFavoritesOperationsObserver observer) {
        LeanEntity entity = Converters.photoRefToLeanEntity(photo);
        entity.put(Props.PhotoRef.IS_FAVORITE, false);
        entity.saveInBackground(new NetworkCallback<Long>() {
            @Override
            public void onResult(Long... result) {
                photo.set_favorite(false);
                observer.onFinished(new RemoveFavoritePhotosResult(true));
            }

            @Override
            public void onFailure(LeanError error) {
                observer.onFinished(new RemoveFavoritePhotosResult(false));
            }
        });
    }

    @Override
    public void clearFavoritePhotos(final IDataFavoritesOperationsObserver observer) {
        //To change body of implemented methods use File | Settings | File Templates.
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
        }
        final TaskObserverImpl internalObserver = new TaskObserverImpl(observer, query);

        NetworkCallback<LeanEntity> callback =
                new NetworkCallback<LeanEntity>() {
                    @Override
                    public void onResult(LeanEntity... result) {
                        client.fetchPhotosFromCache(internalObserver, Converters.photosRefsToNames(result));
                    }

                    @Override
                    public void onFailure(LeanError error) {
                        observer.onFinished(new GetFavoritePhotosResult(null, null));
                    }
                };

        if (previous != null)
            query.fetchNextInBackground(callback);
        else
            query.fetchInBackground(callback);
    }

    @Override
    public void addFavoriteVenue(final RestogramVenue venue, final IDataFavoritesOperationsObserver observer) {
        final TaskObserverImpl internalObserver = new TaskObserverImpl(observer, venue);
        client.cacheVenue(venue.getFoursquare_id(), internalObserver);
    }

    @Override
    public void removeFavoriteVenue(final RestogramVenue venue, final IDataFavoritesOperationsObserver observer) {
        LeanEntity entity = Converters.venueRefToLeanEntity(venue);
        entity.put(Props.VenueRef.IS_FAVORITE, false);
        entity.saveInBackground(new NetworkCallback<Long>() {
            @Override
            public void onResult(Long... result) {
                venue.setfavorite(false);
                observer.onFinished(new RemoveFavoriteVenuesResult(true));
            }

            @Override
            public void onFailure(LeanError error) {
                observer.onFinished(new RemoveFavoriteVenuesResult(false));
            }
        });
    }

    @Override
    public void clearFavoriteVenues(final IDataFavoritesOperationsObserver observer) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void getFavoriteVenues(final IDataFavoritesOperationsObserver observer) {
        doGetFavoriteVenues(null, observer);
    }

    @Override
    public void getNextFavoriteVenues(final  GetFavoriteVenuesResult previous, final IDataFavoritesOperationsObserver observer) {
        doGetFavoriteVenues(previous, observer);
    }

    private void doGetFavoriteVenues(final GetFavoriteVenuesResult previous, final IDataFavoritesOperationsObserver observer) {
        LeanQuery query;
        if (previous != null) // get next
            query = previous.getQuery();
        else // no previous results
        {
            query = new LeanQuery(Kinds.VENUE_REFERENCE);
            query.addFilter(Props.VenueRef.IS_FAVORITE, LeanQuery.FilterOperator.EQUAL, true);
        }
        final TaskObserverImpl internalObserver = new TaskObserverImpl(observer, query);

        NetworkCallback<LeanEntity> callback =
                new NetworkCallback<LeanEntity>() {
                    @Override
                    public void onResult(LeanEntity... result) {
                        client.fetchVenuesFromCache(internalObserver, Converters.venuesRefsToNames(result));
                    }

                    @Override
                    public void onFailure(LeanError error) {
                        observer.onFinished(new GetFavoriteVenuesResult(null, null));
                    }
                };

        if (previous != null)
            query.fetchNextInBackground(callback);
        else
            query.fetchInBackground(callback);
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

        @Override
        public void onFinished(GetNearbyResult venues) { }

        @Override
        public void onFinished(GetInfoResult venue) { }

        @Override
        public void onFinished(GetPhotosResult result) { }

        @Override
        public void onFinished(CachePhotoResult result) {
            if (!result.hasSucceded())
                observer.onFinished(new AddFavoritePhotosResult(false));
            else // caching succceded
            {
                LeanEntity entity = Converters.photoRefToLeanEntity(photo_to_add);
                entity.put(Props.PhotoRef.IS_FAVORITE, true);
                entity.saveInBackground(new NetworkCallback<Long>() {

                    // operation has  fully succeded
                    @Override
                    public void onResult(Long... result) {
                        photo_to_add.set_favorite(true);
                        photo_to_add.setId(result[0]); // TODO: should set it even if already set?
                        observer.onFinished(new AddFavoritePhotosResult(true));
                    }

                    // photo ref update has failed
                    @Override
                    public void onFailure(LeanError error) {
                        observer.onFinished(new AddFavoritePhotosResult(false));
                    }
                });
            }
        }

        @Override
        public void onFinished(FetchPhotosFromCacheResult result) {
            if (result.getPhotos() == null)
                observer.onFinished(new GetFavoritePhotosResult(null, null));
            else
                observer.onFinished(new GetFavoritePhotosResult(result.getPhotos(), favorites_query));
        }

        @Override
        public void onFinished(CacheVenueResult result) {
            if (!result.hasSucceded())
                observer.onFinished(new AddFavoriteVenuesResult(false));
            else // caching succceded
            {
                LeanEntity entity = Converters.venueRefToLeanEntity(venue_to_add);
                entity.put(Props.VenueRef.IS_FAVORITE, true);
                entity.saveInBackground(new NetworkCallback<Long>() {

                    // operation has  fully succeded
                    @Override
                    public void onResult(Long... result) {
                        photo_to_add.set_favorite(true);
                        photo_to_add.setId(result[0]); // TODO: should set it even if already set?
                        observer.onFinished(new AddFavoriteVenuesResult(true));
                    }

                    // venue ref update has failed
                    @Override
                    public void onFailure(LeanError error) {
                        observer.onFinished(new AddFavoriteVenuesResult(false));
                    }
                });
            }
        }

        @Override
        public void onFinished(FetchVenuesFromCacheResult result) {
            if (result.getVenues() == null)
                observer.onFinished(new GetFavoriteVenuesResult(null, null));
            else
                observer.onFinished(new GetFavoriteVenuesResult(result.getVenues(), favorites_query));
        }

        @Override
        public void onCanceled() {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        private IDataFavoritesOperationsObserver observer;
        private RestogramPhoto photo_to_add;
        private RestogramVenue venue_to_add;
        private LeanQuery favorites_query;
    }

    private IRestogramClient client;
}
