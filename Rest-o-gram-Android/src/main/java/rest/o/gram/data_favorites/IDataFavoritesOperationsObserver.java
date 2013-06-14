package rest.o.gram.data_favorites;

import rest.o.gram.data_favorites.results.*;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/23/13
 */
public interface IDataFavoritesOperationsObserver {
    void onFinished(GetFavoritePhotosResult result);
    void onFinished(AddFavoritePhotoResult result);
    void onFinished(RemoveFavoritePhotoResult result);

    void onFinished(GetFavoriteVenuesResult result);
    void onFinished(AddFavoriteVenueResult result);
    void onFinished(RemoveFavoriteVenueResult result);
}
