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
public class DataProvider implements IDataProvider {
    @Override
    public long[] addFavoritePhotos(RestogramPhoto... photos) {
        return new long[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeFavoritePhotos(String... ids) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void clearFavoritePhotos() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PhotosResult getFavoritePhotos() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long[] addFavoriteVenues(RestogramVenue... venues) {
        return new long[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeFavoriteVenues(String... ids) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void clearFavoriteVenues() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public VenuesResult getFavoriteVenues() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
