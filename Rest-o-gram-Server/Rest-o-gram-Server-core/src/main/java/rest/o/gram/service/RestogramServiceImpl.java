package rest.o.gram.service;

import com.google.appengine.api.datastore.Entity;
import com.google.gson.Gson;
import com.leanengine.server.LeanException;
import com.leanengine.server.appengine.AccountUtils;
import com.leanengine.server.appengine.DatastoreUtils;
import com.leanengine.server.auth.AuthService;
import com.leanengine.server.entity.LeanQuery;
import com.leanengine.server.entity.QueryFilter;
import com.leanengine.server.entity.QueryResult;
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
import org.jinstagram.entity.media.MediaInfoFeed;
import org.jinstagram.entity.users.feed.MediaFeed;
import org.jinstagram.entity.users.feed.MediaFeedData;
import org.jinstagram.exceptions.InstagramException;
import rest.o.gram.Converters;
import rest.o.gram.credentials.*;
import rest.o.gram.entities.Kinds;
import rest.o.gram.entities.Props;
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.entities.RestogramVenue;
import rest.o.gram.filters.RestogramFilter;
import rest.o.gram.filters.RestogramFilterFactory;
import rest.o.gram.filters.RestogramFilterType;
import rest.o.gram.iservice.RestogramService;
import rest.o.gram.results.*;
import rest.o.gram.utils.InstagramUtils;

import java.util.*;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 31/03/13
 */
public class RestogramServiceImpl implements RestogramService {

    public RestogramServiceImpl()
    {
        try
        {
            //m_factory = new SimpleCredentialsFactory();
            m_factory = new RandomCredentialsFactory();

            Credentials credentials = m_factory.createFoursquareCredentials();
            log.warning("service created: foursquare credentials type = " + credentials.getType());
            m_foursquare = new FoursquareApi(credentials.getClientId(), credentials.getClientSecret(), "");

            credentials = m_factory.createInstagramCredentials();
            log.warning("service created: instagram credentials type = " + credentials.getType());
            m_instagram = new Instagram(credentials.getClientId());
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
     * @return array of venues near given location within given radius
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

        final RestogramVenue venue = convert(v);
        return new VenueResult(venue);
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

    @Override
    public boolean cachePhoto(String id) {
        // TODO: check if photo is already in cache
        long lid;
        try
        {
            lid = InstagramUtils.extractMediaId(id);
        } catch (Exception e)
        {
            log.severe("cannot extract media id from media feed string id");
            e.printStackTrace();
            return false;
        }
        MediaInfoFeed mediaInfo = null;
        try
        {
            mediaInfo = m_instagram.getMediaInfo(lid);
        } catch (InstagramException e)
        {
            log.warning("first get photo has failed, retry");
            e.printStackTrace();
            try
            {
                mediaInfo = m_instagram.getMediaInfo(lid);
            } catch (InstagramException e2)
            {
                log.severe("second get photo has failed");
                e2.printStackTrace();
                return false;
            }
        }

        final RestogramPhoto photo = convert(mediaInfo.getData());
        try
        {
            DatastoreUtils.putPublicEntity(Kinds.PHOTO,
                    photo.getInstagram_id(), Converters.photoToProps(photo));
        } catch (LeanException e)
        {
            log.severe("caching the photo in DS has failed");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public RestogramPhoto[] fetchPhotosFromCache(String[] ids) {
        Collection<Entity> entities = null;
        try
        {
            entities = DatastoreUtils.getPublicEntities(Kinds.PHOTO, ids);
        } catch (LeanException e)
        {
            log.severe("fetching photos fromm cache has failed");
            e.printStackTrace();
        }

//        if  (entities == null)
//        {
//            log.severe();
//        }

        RestogramPhoto[] photos = new RestogramPhoto[entities.size()];
        int i = 0;
        for (final Entity currEntity : entities)
            photos[i++] = Converters.entityToPhoto(currEntity).encodeStrings();

        Arrays.sort(photos, new Comparator<RestogramPhoto>() {
            @Override
            public int compare(RestogramPhoto o1, RestogramPhoto o2) {
                return o1.getInstagram_id().compareTo(o2.getInstagram_id());
            }
        });
        return photos;
    }

    @Override
    public boolean cacheVenue(String id) {
        //TODO: check if venue is already in cache
        CompleteVenue compVenue = null;
        try
        {
            compVenue = m_foursquare.venue(id).getResult();
        } catch (FoursquareApiException e)
        {
            log.warning("first get venue has failed, retry");
            e.printStackTrace();
            try
            {
                compVenue = m_foursquare.venue(id).getResult();
            } catch (FoursquareApiException e2)
            {
                log.severe("second get venue has failed");
                e2.printStackTrace();
                return false;
            }
        }

        final RestogramVenue venue = convert(compVenue);
        try
        {
            DatastoreUtils.putPublicEntity(Kinds.VENUE, venue.getFoursquare_id(), Converters.venueToProps(venue));
        } catch (LeanException e)
        {
            log.severe("caching the venue in DS has failed");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public RestogramVenue[] fetchVenuesFromCache(String[] ids) {
        Collection<Entity> entities = null;
        try
        {
            entities = DatastoreUtils.getPublicEntities(Kinds.VENUE, ids);
        } catch (LeanException e)
        {
            log.severe("fetching venues fromm cache has failed");
            e.printStackTrace();
        }

        RestogramVenue[] venues = new RestogramVenue[entities.size()];
        int i = 0;
        for (final Entity currEntity : entities)
            venues[i++] = Converters.entityToVenue(currEntity).encodeStrings();

        Arrays.sort(venues, new Comparator<RestogramVenue>() {
            @Override
            public int compare(RestogramVenue o1, RestogramVenue o2) {
                return o1.getFoursquare_id().compareTo(o2.getFoursquare_id());
            }
        });
        return venues;
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
        for(int i = 0; i < arr.length; i++)
        {
            venues[i] = convert(arr[i]);
            if (AuthService.isUserLoggedIn())
            {
                final LeanQuery lquery = new LeanQuery(Kinds.VENUE_REFERENCE);
                lquery.addFilter(Props.VenueRef.FOURSQUARE_ID, QueryFilter.FilterOperator.EQUAL,
                        venues[i].getFoursquare_id());
                lquery.addFilter(Props.VenueRef.IS_FAVORITE, QueryFilter.FilterOperator.EQUAL, true);
                QueryResult qresult = null;
                try {
                    qresult = DatastoreUtils.queryEntityPrivate(lquery);
                } catch (LeanException e) {
                    log.severe("error while getting private venue info from DS");
                    e.printStackTrace();
                }
                if (qresult != null && !qresult.getResult().isEmpty())
                    venues[i].setfavorite(true);
            }
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
        for (MediaFeedData media : data)
        {
            photos[i] = convert(media);
            if (AuthService.isUserLoggedIn())
            {
                final LeanQuery lquery = new LeanQuery(Kinds.PHOTO_REFERENCE);
                lquery.addFilter(Props.PhotoRef.INSTAGRAM_ID, QueryFilter.FilterOperator.EQUAL,
                        photos[i].getInstagram_id());
                lquery.addFilter(Props.PhotoRef.IS_FAVORITE,  QueryFilter.FilterOperator.EQUAL, true);
                QueryResult result = null;
                try {
                    result = DatastoreUtils.queryEntityPrivate(lquery);
                } catch (LeanException e) {
                    log.severe("error while getting private photo info from DS");
                    e.printStackTrace();
                }
                if (result != null && !result.getResult().isEmpty())
                    photos[i].set_favorite(true);
            }
            ++i;
        }

        log.info("GOT " + photos.length + " PHOTOS");
        final Pagination pagination = recentMediaByLocation.getPagination();
        log.info("HAS MORE? " + (StringUtils.isNotBlank(pagination.getNextUrl()) ? "YES!" : "NO!"));
        final String token = (StringUtils.isNotBlank(pagination.getNextUrl()) ?
                                new Gson().toJson(pagination) : null)  ;
        return new PhotosResult(photos, token);
    }

    private RestogramPhoto convert(final MediaFeedData media) {
        String caption = "";
        if(media.getCaption() != null)
            caption = media.getCaption().getText();

        String user = "";
        if(media.getUser() != null)
            user = media.getUser().getUserName();
        final Images images = media.getImages();
        final String thumbnail = images.getThumbnail().getImageUrl();
        final String standardResolution = images.getStandardResolution().getImageUrl();
        return new RestogramPhoto(caption, media.getCreatedTime(), media.getId(),
                media.getImageFilter(), thumbnail, standardResolution,
                media.getLikes().getCount(), media.getLink(),
                media.getType(), user).encodeStrings();
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

        // TODO: calculate distance using LocationUtils

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
                                    phone).encodeStrings();
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

        return result.encodeStrings();
    }

    private static final Logger log = Logger.getLogger(RestogramServiceImpl.class.getName());

    private FoursquareApi m_foursquare;
    private Instagram m_instagram;

    private ICredentialsFactory m_factory;
}
