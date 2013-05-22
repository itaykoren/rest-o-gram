package rest.o.gram.data;

import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.entities.RestogramVenue;
import rest.o.gram.results.PhotosResult;
import rest.o.gram.results.VenuesResult;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/22/13
 */
public interface IDataProvider {
    long[] addFavoritePhotos(RestogramPhoto... photos);

    void removeFavoritePhotos(String... ids);

    void clearFavoritePhotos();

    PhotosResult getFavoritePhotos();

    long[] addFavoriteVenues(RestogramVenue... venues);

    void removeFavoriteVenues(String... ids);

    void clearFavoriteVenues();

    VenuesResult getFavoriteVenues();
}
