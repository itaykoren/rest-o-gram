package rest.o.gram.service;

import com.google.gson.Gson;
import fi.foyt.foursquare.api.FoursquareApi;
import fi.foyt.foursquare.api.FoursquareApiException;
import fi.foyt.foursquare.api.Result;
import fi.foyt.foursquare.api.entities.*;
import org.apache.commons.lang3.StringUtils;
import org.jinstagram.Instagram;
import org.jinstagram.entity.common.Images;
import org.jinstagram.entity.common.Location;
import org.jinstagram.entity.common.Pagination;
import org.jinstagram.entity.locations.LocationSearchFeed;
import org.jinstagram.entity.users.feed.MediaFeed;
import org.jinstagram.entity.users.feed.MediaFeedData;
import org.jinstagram.exceptions.InstagramException;
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.entities.RestogramVenue;
import rest.o.gram.filters.RestogramFilter;
import rest.o.gram.filters.RestogramFilterFactory;
import rest.o.gram.filters.RestogramFilterType;
import rest.o.gram.iservice.RestogramService;
import rest.o.gram.results.*;

import java.util.logging.Logger;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 31/03/13
 */
public class RestogramServiceImpl implements RestogramService {

    private static final Logger log = Logger.getLogger(RestogramServiceImpl.class.getName());

    public RestogramServiceImpl()
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

    /**
     * @return array of venus near given location
     */
    @Override
    public VenuesResult getNearby(double latitude, double longitude)
    {
        String location = latitude + "," + longitude;

        // TODO: manage foursquare categories...
        String categories = "4d4b7105d754a06374d81259";

        Map<String, String> params = new HashMap<String, String>();
        params.put("ll", location);
        params.put("categoryId", categories);
        params.put("intent", "match");

        return doGetNearby(params);
    }

    /**
     * @return array of venus near given location within given radius
     */
    @Override
    public VenuesResult getNearby(double latitude, double longitude, double radius)
    {
        String location = latitude + "," + longitude;

        // TODO: manage foursquare categories...
        String categories = "4d4b7105d754a06374d81259";

        Map<String, String> params = new HashMap<String, String>();
        params.put("ll", location);
        params.put("radius", Double.toString(radius));
        params.put("categoryId", categories);

        return doGetNearby(params);
    }

    /**
     * @return venue information according to its ID
     */
    @Override
    public VenueResult getInfo(String venueID) {
        Result<CompleteVenue> result;
        try {
            result = m_foursquare.venue(venueID);
        } catch (FoursquareApiException e) {
            log.warning("first venue  " + venueID + " retrieval has failed, retry");
            e.printStackTrace();
            try {
                result = m_foursquare.venue(venueID);
            }
            catch (FoursquareApiException e2)
            {
                log.severe("second venue " + venueID + " retrieval has failed");
                e2.printStackTrace();
                return null;
            }
        }

        if (result.getMeta().getCode() != 200) {
            log.severe("venue " + venueID + "retrieval returned an error code: " + result.getMeta().getCode());
            return null;
        }

        CompleteVenue v = result.getResult();
        if(v == null)
        {
            log.severe("extracting info from venue has failed");
            return null;
        }

        return new VenueResult(convert(v));
    }

    /**
     * @return array of media related to venue given its ID
     */
    @Override
    public PhotosResult getPhotos(String venueID)
    {
        return doGetPhotos(venueID, RestogramFilterType.None);
    }

    /**
     * @return array of media related to venue given its ID, after applying given filter
     */
    @Override
    public PhotosResult getPhotos(String venueID, RestogramFilterType filterType)
    {
        return doGetPhotos(venueID, filterType);
    }

    /**
     * @param token identifying previous session for getting next photos
     * @return array of media related to venue given its ID
     */
    @Override
    public PhotosResult getNextPhotos(String token) {
        Pagination pag =
                new Gson().fromJson(token, Pagination.class);
        return doGetPhotos(pag, RestogramFilterType.None);
    }

    /**
     *
     * @param token identifying previous session for getting next photos
     * @return array of media related to venue given its ID, after applying given filter
     */
    @Override
    public PhotosResult getNextPhotos(String token, RestogramFilterType filterType) {

        Pagination pag = new Gson().fromJson(token, Pagination.class);
        return doGetPhotos(pag, filterType);
    }

    /**
     * Executes get nearby request
     */
    private VenuesResult doGetNearby(Map<String, String> params)
    {
        Result<VenuesSearchResult> result;
        try
        {
            result = m_foursquare.venuesSearch(params);
        }
        catch (FoursquareApiException e)
        {
            // TODO: test the "second-chance" policy
            try
            {
                log.warning("first venue search has failed, retry");
                e.printStackTrace();
                result = m_foursquare.venuesSearch(params);
            }
            catch (FoursquareApiException e2)
            {
                log.severe("second venue search has failed");
                e2.printStackTrace();
                return null;
            }
        }

        if (result.getMeta().getCode() != 200)
        {
            log.severe("venue search returned an error code: " + result.getMeta().getCode());
            return null;
        }

        CompactVenue[] arr = result.getResult().getVenues();

        if(arr == null || arr.length == 0)
        {
            log.severe("venue search returned no venues");
            return null;
        }

        RestogramVenue[] venues = new RestogramVenue[arr.length];
        for(int i = 0; i < arr.length; i++) {
            venues[i] = convert(arr[i]);
        }

        log.info("found " + venues.length + " venues!");

        return new VenuesResult(venues);
    }

    /**
     * Executes get photos request
     */
    private PhotosResult doGetPhotos(String venueID, RestogramFilterType filterType)
    {
        LocationSearchFeed locationSearchFeed = null;

        try
        {
            locationSearchFeed = m_instagram.searchFoursquareVenue(venueID);
        }
        catch (InstagramException e)
        {
            log.warning("first search for venue: " + venueID + " has failed, retry");
            e.printStackTrace();
            try
            {
                locationSearchFeed = m_instagram.searchFoursquareVenue(venueID);
            }
            catch (InstagramException e2)
            {
                log.severe("second search for venue: " + venueID + "has failed");
                e.printStackTrace();
            }
        }

        List<Location> locationList = locationSearchFeed.getLocationList();
        if (locationList.isEmpty()) // TODO: handle in a different way?
        {
            log.severe("venue:  " + venueID + " not found");
            return null;
        }
        long locationId = locationList.get(0).getId(); // TODO: what if we get multiple locations?

        MediaFeed recentMediaByLocation;
        try
        {
            recentMediaByLocation = m_instagram.getRecentMediaByLocation(locationId);
        }
        catch(InstagramException e)
        {
            log.warning("first recent media search for venue: " + venueID + "has failed, retry");
            e.printStackTrace();

            try
            {
                recentMediaByLocation = m_instagram.getRecentMediaByLocation(locationId);
            }
            catch (InstagramException e2)
            {
                log.severe("second search for recent media for venue: " + venueID + "has failed");
                e2.printStackTrace();
                return null;
            }
        }

        return createPhotosResult(recentMediaByLocation, filterType);
    }

    /**
     * Executes get photos request
     */
    private PhotosResult doGetPhotos(Pagination pagination, RestogramFilterType filterType)
    {
        MediaFeed recentMediaByLocation;
        try
        {
            recentMediaByLocation = m_instagram.getRecentMediaNextPage(pagination);
        }
        catch(InstagramException e)
        {
            log.warning("first next media search has failed, retry");
            e.printStackTrace();

            try
            {
                recentMediaByLocation = m_instagram.getRecentMediaNextPage(pagination);
            }
            catch (InstagramException e2)
            {
                log.severe("second next for recent media has failed");
                e2.printStackTrace();
                return null;
            }
        }

        return createPhotosResult(recentMediaByLocation, filterType);
    }

    private PhotosResult createPhotosResult(MediaFeed recentMediaByLocation, RestogramFilterType filterType) {
        if(recentMediaByLocation == null)
        {
            log.severe("next media search returned no media");
            return null;
        }

        List<MediaFeedData> data = recentMediaByLocation.getData();
        if(data == null)
        {
            log.severe("next media search returned no media");
            return null;
        }

        log.info("fetched " + data.size() + " photos");
        // TODO: apply simple/complex filter
        if (filterType != RestogramFilterType.None)
        {
            RestogramFilter restogramFilter = RestogramFilterFactory.createFilter(filterType);
            data = restogramFilter.doFilter(data);
        }

        RestogramPhoto[] photos = new RestogramPhoto[data.size()];

        int i = 0;
        for (MediaFeedData media : data) {

            String caption = "";
            if(media.getCaption() != null)
                caption = media.getCaption().getText();

            String user = "";
            if(media.getUser() != null)
                user = media.getUser().getUserName();

            Images images = media.getImages();
            String thumbnail = images.getThumbnail().getImageUrl();
            String standardResolution = images.getStandardResolution().getImageUrl();

            photos[i++] = new RestogramPhoto(caption, media.getCreatedTime(), media.getId(),
                    media.getImageFilter(), thumbnail, standardResolution,
                    media.getLikes().getCount(), media.getLink(),
                    media.getType(), user);
        }

        log.info("GOT " + photos.length + " PHOTOS");
        final Pagination pagination = recentMediaByLocation.getPagination();
        log.info("HAS MORE? " + (StringUtils.isNotBlank(pagination.getNextUrl()) ? "YES!" : "NO!"));
        final String token = (StringUtils.isNotBlank(pagination.getNextUrl()) ?
                                new Gson().toJson(pagination) : null)  ;
        return new PhotosResult(photos, token);
    }

    /**
     * Converts compact venue foursquare object to restogram venue
     */
    private RestogramVenue convert(CompactVenue venue) {
        fi.foyt.foursquare.api.entities.Location location = venue.getLocation();

        Contact contact = venue.getContact();
        String phone = "";
        if(contact != null)
            phone = contact.getPhone();

        return new RestogramVenue(venue.getId(),
                                    venue.getName(),
                                    location.getAddress(),
                                    location.getCity(),
                                    location.getState(),
                                    location.getPostalCode(),
                                    location.getCountry(),
                                    location.getLat(),
                                    location.getLng(),
                                    location.getDistance(),
                                    venue.getUrl(),
                                    phone);
    }

    /**
     * Converts complete venue foursquare object to restogram venue
     */
    private RestogramVenue convert(CompleteVenue venue) {
        RestogramVenue result;
        String photoUrl;

        try {
            Photos photos = venue.getPhotos();
            if(photos == null)
                return null;

            PhotoGroup[] groups = photos.getGroups();
            if(groups == null || groups.length < 2)
                return null;

            PhotoGroup group = groups[1];
            Photo[] items = group.getItems();
            if(items == null || items.length == 0)
                return null;

            photoUrl = items[0].getUrl();
            result = new RestogramVenue(venue.getId(), venue.getName(), venue.getDescription(), photoUrl);
        }
        catch(Exception e) {
            log.severe("venue object conversion failed");
            return null;
        }

        return result;
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
