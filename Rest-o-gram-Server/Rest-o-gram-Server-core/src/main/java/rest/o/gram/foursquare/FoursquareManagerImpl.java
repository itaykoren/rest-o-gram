package rest.o.gram.foursquare;

import fi.foyt.foursquare.api.FoursquareApi;
import fi.foyt.foursquare.api.FoursquareApiException;
import fi.foyt.foursquare.api.Result;
import fi.foyt.foursquare.api.entities.CompactVenue;
import fi.foyt.foursquare.api.entities.CompleteVenue;
import fi.foyt.foursquare.api.entities.VenuesSearchResult;
import fi.foyt.foursquare.api.io.GAEIOHandler;
import rest.o.gram.ApisConverters;
import rest.o.gram.Defs;
import rest.o.gram.credentials.Credentials;
import rest.o.gram.credentials.ICredentialsFactory;
import rest.o.gram.credentials.RandomCredentialsFactory;
import rest.o.gram.entities.RestogramVenue;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 6/6/14
 */
public class FoursquareManagerImpl implements FoursquareManager {

    public FoursquareManagerImpl() {
        try {
            // TODO: when foursquare login is implemented - prefer user credentials whenever possible
            credentialsFactory = new RandomCredentialsFactory();
        } catch (Exception e) {
            log.severe("an error occurred while initializing the service");
        }
    }

    @Override
    public List<RestogramVenue> getNearby(final double latitude, final double longitude, final double radius) {
        return doGetNearby(latitude, longitude, radius);
    }

    private List<RestogramVenue> doGetNearby(final double latitude, final double longitude,
                                             final double radius) {
        final Map<String, String> params = new HashMap<String, String>();
        params.put("ll", String.format("%f,%f", latitude, longitude));
        params.put("categoryId", Defs.Foursquare.VENUE_CATEGORY);
        if (radius >= 0)
            params.put("radius", Double.toString(radius));
        else
            params.put("intent", "match");

        return doGetNearby(params);
    }

    private List<RestogramVenue> doGetNearby(final Map<String, String> params) {
        Result<VenuesSearchResult> result;
        try {
            final Credentials credentials = credentialsFactory.createFoursquareCredentials();
            log.info("foursquare credentials type = " + credentials.getType());
            final FoursquareApi foursquare =
                    new FoursquareApi(credentials.getClientId(), credentials.getClientSecret(), "", new GAEIOHandler());
            result = foursquare.venuesSearch(params);
        } catch (FoursquareApiException e) {
            try {
                log.warning("first venue search has failed, retry");
                final Credentials credentials = credentialsFactory.createFoursquareCredentials();
                log.info("foursquare credentials type = " + credentials.getType());
                final FoursquareApi foursquare =
                        new FoursquareApi(credentials.getClientId(), credentials.getClientSecret(), "", new GAEIOHandler());
                result = foursquare.venuesSearch(params);
            } catch (FoursquareApiException e2) {
                log.severe("second venue search has failed");
                return null;
            }
        }

        if (result.getMeta().getCode() != HttpServletResponse.SC_OK) {
            log.severe("venue search returned an error code: " + result.getMeta().getCode());
            return null;
        }

        final CompactVenue[] arr = result.getResult().getVenues();
        if (arr == null || arr.length == 0) {
            log.severe("venue search returned no venues");
            return null;
        }

        final String[] venueIds = new String[arr.length];
        final RestogramVenue[] venues = new RestogramVenue[arr.length];

        for (int i = 0; i < arr.length; i++) {
            venues[i] = ApisConverters.convertToRestogramVenue(arr[i]);
            venueIds[i] = arr[i].getId();
        }

        log.info("found " + venues.length + " venues!");
        return Arrays.asList(venues);
    }

    @Override
    public RestogramVenue getInfo(final String venueID) {
        Result<CompleteVenue> result;
        try {
            final Credentials credentials = credentialsFactory.createFoursquareCredentials();
            log.info("foursquare credentials type = " + credentials.getType());
            final FoursquareApi foursquare =
                    new FoursquareApi(credentials.getClientId(), credentials.getClientSecret(), "", new GAEIOHandler());
            result = foursquare.venue(venueID);
        } catch (FoursquareApiException e) {
            log.warning("first venue  " + venueID + " retrieval has failed, retry");
            try {
                final Credentials credentials = credentialsFactory.createFoursquareCredentials();
                log.info("foursquare credentials type = " + credentials.getType());
                final FoursquareApi foursquare =
                        new FoursquareApi(credentials.getClientId(), credentials.getClientSecret(), "", new GAEIOHandler());
                result = foursquare.venue(venueID);
            } catch (FoursquareApiException e2) {
                log.severe("second venue " + venueID + " retrieval has failed");
                return null;
            }
        }

        if (result.getMeta().getCode() != HttpServletResponse.SC_OK) {
            log.severe("venue " + venueID + "retrieval returned an error code: " + result.getMeta().getCode());
            return null;
        }

        final CompleteVenue completeVenue = result.getResult();
        if (completeVenue == null) {
            log.severe("extracting info from venue has failed");
            return null;
        }

        return ApisConverters.convertToRestogramVenue(completeVenue);
    }

    private static final Logger log = Logger.getLogger(FoursquareManagerImpl.class.getName());
    private ICredentialsFactory credentialsFactory;
}