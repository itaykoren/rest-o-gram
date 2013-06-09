package rest.o.gram.data_favorites;

import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.entities.RestogramVenue;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/22/13
 */
public interface IDataFavoritesManager {
    void addFavoritePhoto(final RestogramPhoto photo, final IDataFavoritesOperationsObserver observer);

    void removeFavoritePhoto(final RestogramPhoto photo, final IDataFavoritesOperationsObserver observer);

    void getFavoritePhotos(final IDataFavoritesOperationsObserver observer);

    Set<String> getFavoritePhotos();

    void getNextFavoritePhotos(final GetFavoritePhotosResult previous, final IDataFavoritesOperationsObserver observer);

    void addFavoriteVenue(final RestogramVenue venue, final IDataFavoritesOperationsObserver observer);

    void removeFavoriteVenue(final RestogramVenue venue, final IDataFavoritesOperationsObserver observer);

    void getFavoriteVenues(final IDataFavoritesOperationsObserver observer);

    Set<String> getFavoriteVenues();

    void getNextFavoriteVenues(final GetFavoriteVenuesResult previous, final IDataFavoritesOperationsObserver observer);

    void dispose();
}
