package rest.o.gram.data_favorites;

import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.entities.RestogramVenue;
import rest.o.gram.tasks.ITaskObserver;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/22/13
 */
public interface IDataFavoritesManager {
    // TODO: convert to a command
    void getFavoritePhotos(final IDataFavoritesOperationsObserver observer);

    void addFavoritePhoto(String photoId);
    boolean removeFavoritePhoto(String photoId);
    Set<String> getFavoritePhotos();

    // TODO: convert to a command
    void getNextFavoritePhotos(final GetFavoritePhotosResult previous, final IDataFavoritesOperationsObserver observer);

    void dispose();
}
