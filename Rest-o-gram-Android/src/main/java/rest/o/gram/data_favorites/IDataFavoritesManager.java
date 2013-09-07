package rest.o.gram.data_favorites;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/22/13
 */
public interface IDataFavoritesManager {

    void addFavoritePhoto(String photoId);
    boolean removeFavoritePhoto(String photoId);
    Set<String> getFavoritePhotos();

    void dispose();
}
