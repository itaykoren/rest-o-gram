package rest.o.gram.iservice;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 19/05/13
 */
public interface RestogramAuthService {
    /**
     * adds requested photo to user favorites, increments global yummies count
     */
    boolean addPhotoToFavorites(String photoId, String originVenueId);

    /**
     * removes requested photo from user favorites, decrements global yummies count
     */
    boolean removePhotoFromFavorites(String photoId);

    /**
     * fetches all favorite photos of the current user
     */
    boolean fetchFavoritePhotos(String token);
}
