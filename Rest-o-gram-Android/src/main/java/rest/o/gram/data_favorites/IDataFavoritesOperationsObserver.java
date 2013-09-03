package rest.o.gram.data_favorites;

import rest.o.gram.data_favorites.results.*;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/23/13
 */
public interface IDataFavoritesOperationsObserver {
    void onFinished(GetFavoritePhotosResult result);
    void onFinished(AddPhotoToFavoritesResult result);
    void onFinished(RemovePhotoFromFavoritesResult result);
}
