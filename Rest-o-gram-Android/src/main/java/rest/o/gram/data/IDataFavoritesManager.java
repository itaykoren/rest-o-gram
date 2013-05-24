package rest.o.gram.data;

import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.entities.RestogramVenue;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/22/13
 */
public interface IDataFavoritesManager {
    void addFavoritePhoto(final RestogramPhoto photo, final IDataFavoritesOperationsObserver observer);

    void removeFavoritePhoto(final RestogramPhoto photo, final IDataFavoritesOperationsObserver observer);

    void clearFavoritePhotos(final IDataFavoritesOperationsObserver observer);

    void getFavoritePhotos(final IDataFavoritesOperationsObserver observer);

    void getNextFavoritePhotos(final GetFavoritePhotosResult previous, final IDataFavoritesOperationsObserver observer);

    void addFavoriteVenue(final RestogramVenue venue, final IDataFavoritesOperationsObserver observer);

    void removeFavoriteVenue(final RestogramVenue venue, final IDataFavoritesOperationsObserver observer);

    void clearFavoriteVenues(final IDataFavoritesOperationsObserver observer);

    void getFavoriteVenues(final IDataFavoritesOperationsObserver observer);

    void getNextFavoriteVenues(final GetFavoriteVenuesResult previous, final IDataFavoritesOperationsObserver observer);
}
