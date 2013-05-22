package rest.o.gram.service;

import com.google.appengine.api.datastore.Entity;
import com.leanengine.server.LeanException;
import com.leanengine.server.appengine.AccountUtils;
import com.leanengine.server.appengine.DatastoreUtils;
import com.leanengine.server.auth.AuthService;
import com.leanengine.server.auth.LeanAccount;
import com.leanengine.server.entity.LeanQuery;
import com.leanengine.server.entity.QueryFilter;
import fi.foyt.foursquare.api.FoursquareApi;
import org.jinstagram.Instagram;
import rest.o.gram.data.Kinds;
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.entities.RestogramVenue;
import rest.o.gram.iservice.RestogramAuthService;
import rest.o.gram.results.PhotosResult;
import rest.o.gram.results.VenuesResult;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/19/13
 */
public class RestogramAuthServiceImpl implements RestogramAuthService {

    private static final Logger log = Logger.getLogger(RestogramAuthServiceImpl.class.getName());

    public RestogramAuthServiceImpl()
    {
        try
        {
            m_foursquare = new FoursquareApi(FOURSQUARE_CLIENT_ID, FOURSQUARE_CLIENT_SECRET, "");
            m_instagram = new Instagram(INSTAGRAM_CLIENT_ID);
            log.info("AUTH-RPC SERVICE UP");
        }
        catch(Exception e)
        {
            log.severe("an error occurred while initializing the service");
            e.printStackTrace();
        }
    }


//    private void handleSession(final String token, final Handler handler) {
//        AuthService.startAuthSession(token);
//        try {
//            handler.handle();
//        } catch (LeanException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
//        AuthService.finishAuthSession();
//    }

    @Override
    public long[] addFavoritePhotos(RestogramPhoto[] photos) {
        return new long[0];  //To change body of implemented methods use File | Settings | File Templates.
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
    public long[] addFavoriteVenues(RestogramVenue[] venues) {
        return new long[0];  //To change body of implemented methods use File | Settings | File Templates.
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

//    private static abstract class Handler {
//        abstract void handle() throws LeanException;
//    }

    private FoursquareApi m_foursquare;
    private Instagram m_instagram;

    // Foursquare Client Id
    private static final String FOURSQUARE_CLIENT_ID = "OERIKGO1WPRTY2RWWF3IMX5FUGBLSCES1OJ1F3BBLFOIBF3T";

    // Foursquare Client Secret
    private static final String FOURSQUARE_CLIENT_SECRET = "3MQLEAAV5YH2O0ZIWDVJ515KYRDROA3DPQJG4ZDPZHXXCMTF";

    // Instagram Client Id
    private static final String INSTAGRAM_CLIENT_ID = "4d32ff70646e46a992a4ad5a0945ef3f";
}
