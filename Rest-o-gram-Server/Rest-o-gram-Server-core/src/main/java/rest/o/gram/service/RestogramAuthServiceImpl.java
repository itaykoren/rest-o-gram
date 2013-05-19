package rest.o.gram.service;

import fi.foyt.foursquare.api.FoursquareApi;
import org.jinstagram.Instagram;
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.entities.RestogramVenue;
import rest.o.gram.iservice.RestogramAuthService;
import rest.o.gram.results.PhotosResult;
import rest.o.gram.results.VenuesResult;

import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/19/13
 */
public class RestogramAuthServiceImpl implements RestogramAuthService {

    private static final Logger log = Logger.getLogger(RestogramServiceImpl.class.getName());

    public RestogramAuthServiceImpl()
    {
        try
        {
            m_foursquare = new FoursquareApi(FOURSQUARE_CLIENT_ID, FOURSQUARE_CLIENT_SECRET, "");
            m_instagram = new Instagram(INSTAGRAM_CLIENT_ID);
        }
        catch(Exception e)
        {
            log.severe("an error occurred while initializing the service");
            e.printStackTrace();
        }
    }

    @Override
    public void addRecentPhotos(RestogramPhoto[] photos) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeRecentPhotos(String[] ids) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void clearRecentPhotos() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PhotosResult getRecentPhotos() {
    return null;
    }

    @Override
    public void addRecentVenues(RestogramVenue[] venues) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeRecentVenues(String[] ids) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void clearRecentVenues() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public VenuesResult getRecentVenues() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void addFavoritePhotos(RestogramPhoto[] photos) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeFavoritePhotos(String[] ids) {
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
    public void addFavoriteVenues(RestogramVenue[] venues) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeFavoriteVenues(String[] ids) {
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

    private FoursquareApi m_foursquare;
    private Instagram m_instagram;

    // Foursquare Client Id
    private static final String FOURSQUARE_CLIENT_ID = "OERIKGO1WPRTY2RWWF3IMX5FUGBLSCES1OJ1F3BBLFOIBF3T";

    // Foursquare Client Secret
    private static final String FOURSQUARE_CLIENT_SECRET = "3MQLEAAV5YH2O0ZIWDVJ515KYRDROA3DPQJG4ZDPZHXXCMTF";

    // Instagram Client Id
    private static final String INSTAGRAM_CLIENT_ID = "4d32ff70646e46a992a4ad5a0945ef3f";
}
