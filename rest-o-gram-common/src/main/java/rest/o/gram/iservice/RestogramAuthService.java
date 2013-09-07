package rest.o.gram.iservice;

import rest.o.gram.results.PhotosResult;

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
    PhotosResult getFavoritePhotos(String token);
}
