package rest.o.gram.data;

import rest.o.gram.data.results.*;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/23/13
 */
public interface IDataFavoritesOperationsObserver {
    void onFinished(GetFavoritePhotosResult result);
    void onFinished(AddFavoritePhotosResult result);
    void onFinished(RemoveFavoritePhotosResult result);
    void onFinished(ClearFavoritePhotosResult result);

    void onFinished(GetFavoriteVenuesResult result);
    void onFinished(AddFavoriteVenuesResult result);
    void onFinished(RemoveFavoriteVenuesResult result);
    void onFinished(ClearFavoriteVenuesResult result);
}
