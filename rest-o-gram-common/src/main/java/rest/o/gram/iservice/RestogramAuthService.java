package rest.o.gram.iservice;

import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.entities.RestogramVenue;
import rest.o.gram.results.PhotosResult;
import rest.o.gram.results.VenuesResult;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 19/05/13
 */
public interface RestogramAuthService {
    long[] addRecentPhotos(String token, RestogramPhoto[] photos);

    void removeRecentPhotos(String token, String[] ids);

    void clearRecentPhotos(String token);

    PhotosResult getRecentPhotos(String token);

    long[] addRecentVenues(String token, RestogramVenue[] venues);

    void removeRecentVenues(String token, String[] ids);

    void clearRecentVenues(String token);

    VenuesResult getRecentVenues(String token);

    long[] addFavoritePhotos(String token, RestogramPhoto[] photos);

    void removeFavoritePhotos(String token, String[] ids);

    void clearFavoritePhotos(String token);

    PhotosResult getFavoritePhotos(String token);

    long[] addFavoriteVenues(String token, RestogramVenue[] venues);

    void removeFavoriteVenues(String token, String[] ids);

    void clearFavoriteVenues(String token);

    VenuesResult getFavoriteVenues(String token);
}
