package rest.o.gram.service;

import com.google.appengine.api.datastore.Entity;
import com.google.gson.Gson;
import com.leanengine.server.LeanException;
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
import rest.o.gram.TasksManager.TasksManager;
import rest.o.gram.credentials.*;
import rest.o.gram.data.DataManager;
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

    public RestogramServiceImpl() {
        try {
            //m_factory = new SimpleCredentialsFactory();
            m_factory = new RandomCredentialsFactory();
        }
        catch(Exception e) {
            log.severe("an error occurred while initializing the service");
            e.printStackTrace();
        }
    }

    /**
     * @return array of venus near given location
     */
    @Override
    public VenuesResult getNearby(double latitude, double longitude) {
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
    public VenuesResult getNearby(double latitude, double longitude, double radius) {
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
            Credentials credentials = m_factory.createFoursquareCredentials();
            log.info("foursquare credentials type = " + credentials.getType());
            FoursquareApi foursquare = new FoursquareApi(credentials.getClientId(), credentials.getClientSecret(), "");
            result = foursquare.venue(venueID);
        } catch (FoursquareApiException e) {
            log.warning("first venue  " + venueID + " retrieval has failed, retry");
            e.printStackTrace();
            try {
                Credentials credentials = m_factory.createFoursquareCredentials();
                log.info("foursquare credentials type = " + credentials.getType());
                FoursquareApi foursquare = new FoursquareApi(credentials.getClientId(), credentials.getClientSecret(), "");
                result = foursquare.venue(venueID);
            }
            catch (FoursquareApiException e2) {
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
        if(v == null) {
            log.severe("extracting info from venue has failed");
            return null;
        }

        final RestogramVenue venue = convert(v);

        cacheVenue(venue);

        return new VenueResult(venue);
    }

    /**
     * @return array of media related to venue given its ID
     */
    @Override
    public PhotosResult getPhotos(String venueID) {
        return doGetPhotos(venueID, RestogramFilterType.None);
    }

    /**
     * @return array of media related to venue given its ID, after applying given filter
     */
    @Override
    public PhotosResult getPhotos(String venueID, RestogramFilterType filterType) {
        return doGetPhotos(venueID, filterType);
    }

    /**
     * @param token identifying previous session for getting next photos
     * @return array of media related to venue given its ID
     */
    @Override
    public PhotosResult getNextPhotos(String token, String originVenueId) {
        Pagination pag =
                new Gson().fromJson(token, Pagination.class);
        return doGetPhotos(pag, RestogramFilterType.None, originVenueId);
    }

    /**
     *
     * @param token identifying previous session for getting next photos
     * @return array of media related to venue given its ID, after applying given filter
     */
    @Override
    public PhotosResult getNextPhotos(String token, RestogramFilterType filterType, String originVenueId) {
        Pagination pag = new Gson().fromJson(token, Pagination.class);
        return doGetPhotos(pag, filterType, originVenueId);
    }

    @Override
    public boolean cachePhoto(String id, String originVenueId) {
        // TODO: check if photo is already in cache
        long lid;
        try {
            lid = InstagramUtils.extractMediaId(id);
        }
        catch (Exception e) {
            log.severe("cannot extract media id from media feed string id");
            e.printStackTrace();
            return false;
        }

        MediaInfoFeed mediaInfo;
        try {
            Credentials credentials = m_factory.createInstagramCredentials();
            log.info("instagram credentials type = " + credentials.getType());
            Instagram instagram = new Instagram(credentials.getClientId());
            mediaInfo = instagram.getMediaInfo(lid);
        }
        catch (InstagramException e) {
            log.warning("first get photo has failed, retry");
            e.printStackTrace();
            try {
                Credentials credentials = m_factory.createInstagramCredentials();
                log.info("instagram credentials type = " + credentials.getType());
                Instagram instagram = new Instagram(credentials.getClientId());
                mediaInfo = instagram.getMediaInfo(lid);
            }
            catch (InstagramException e2) {
                log.severe("second get photo has failed");
                e2.printStackTrace();
                return false;
            }
        }

        final RestogramPhoto photo = convert(mediaInfo.getData(), originVenueId);
        try {
            DatastoreUtils.putPublicEntity(Kinds.PHOTO,
                    photo.getInstagram_id(), Converters.photoToProps(photo));
        }
        catch (LeanException e) {
            log.severe("caching the photo in DS has failed");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public RestogramPhoto[] fetchPhotosFromCache(String[] ids) {
        Collection<Entity> entities = null;
        try {
            entities = DatastoreUtils.getPublicEntities(Kinds.PHOTO, ids);
        }
        catch (LeanException e) {
            log.severe("fetching photos from cache has failed");
            e.printStackTrace();
        }

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
        CompleteVenue compVenue;
        try {
            Credentials credentials = m_factory.createFoursquareCredentials();
            log.info("foursquare credentials type = " + credentials.getType());
            FoursquareApi foursquare = new FoursquareApi(credentials.getClientId(), credentials.getClientSecret(), "");
            compVenue = foursquare.venue(id).getResult();
        } catch (FoursquareApiException e) {
            log.warning("first get venue has failed, retry");
            e.printStackTrace();
            try {
                Credentials credentials = m_factory.createFoursquareCredentials();
                log.info("foursquare credentials type = " + credentials.getType());
                FoursquareApi foursquare = new FoursquareApi(credentials.getClientId(), credentials.getClientSecret(), "");
                compVenue = foursquare.venue(id).getResult();
            } catch (FoursquareApiException e2) {
                log.severe("second get venue has failed");
                e2.printStackTrace();
                return false;
            }
        }

        return cacheVenue(convert(compVenue));
    }

    private boolean cacheVenue(final RestogramVenue venue) {

        try {
            DatastoreUtils.putPublicEntity(Kinds.VENUE, venue.getFoursquare_id(), Converters.venueToProps(venue));
        } catch (LeanException e) {
            log.severe("caching the venue in DS has failed");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public RestogramVenue[] fetchVenuesFromCache(String[] ids) {
        Collection<Entity> entities = null;
        try {
            entities = DatastoreUtils.getPublicEntities(Kinds.VENUE, ids);
        }
        catch (LeanException e) {
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
    private VenuesResult doGetNearby(Map<String, String> params) {
        Result<VenuesSearchResult> result;
        try {
            Credentials credentials = m_factory.createFoursquareCredentials();
            log.info("foursquare credentials type = " + credentials.getType());
            FoursquareApi foursquare = new FoursquareApi(credentials.getClientId(), credentials.getClientSecret(), "");
            result = foursquare.venuesSearch(params);
        }
        catch (FoursquareApiException e) {
            // TODO: test the "second-chance" policy
            try {
                log.warning("first venue search has failed, retry");
                e.printStackTrace();

                Credentials credentials = m_factory.createFoursquareCredentials();
                log.info("foursquare credentials type = " + credentials.getType());
                FoursquareApi foursquare = new FoursquareApi(credentials.getClientId(), credentials.getClientSecret(), "");
                result = foursquare.venuesSearch(params);
            }
            catch (FoursquareApiException e2) {
                log.severe("second venue search has failed");
                e2.printStackTrace();
                return null;
            }
        }

        if (result.getMeta().getCode() != 200) {
            log.severe("venue search returned an error code: " + result.getMeta().getCode());
            return null;
        }

        CompactVenue[] arr = result.getResult().getVenues();

        int length = arr.length;
        if(arr == null || length == 0) {
            log.severe("venue search returned no venues");
            return null;
        }

        String[] venueIds = new String[length];
        RestogramVenue[] venues = new RestogramVenue[length];

        for(int i = 0; i < length; i++) {
            venues[i] = convert(arr[i]);
            venueIds[i] = arr[i].getId();
            if (AuthService.isUserLoggedIn()) {
                final LeanQuery lquery = new LeanQuery(Kinds.VENUE_REFERENCE);
                lquery.addFilter(Props.VenueRef.FOURSQUARE_ID, QueryFilter.FilterOperator.EQUAL,
                        venues[i].getFoursquare_id());
                lquery.addFilter(Props.VenueRef.IS_FAVORITE, QueryFilter.FilterOperator.EQUAL, true);
                QueryResult qresult = null;
                try {
                    qresult = DatastoreUtils.queryEntityPrivate(lquery);
                }
                catch (LeanException e) {
                    log.severe("error while getting private venue info from DS");
                    e.printStackTrace();
                }
                if (qresult != null && !qresult.getResult().isEmpty())
                    venues[i].setfavorite(true);
            }
        }

        RestogramVenue[] venuesFromCache = fetchVenuesFromCache(venueIds);

        for (int i = 0; i < venuesFromCache.length; i++) {

            RestogramVenue currVenue = venuesFromCache[i];

            if (currVenue != null) {

                String imageUrl = currVenue.getImageUrl();
                if (imageUrl != null && !imageUrl.isEmpty()) {

                    addImageUrlToVenue(venues, currVenue.getId(), imageUrl);
                }
            }
        }

        log.info("found " + venues.length + " venues!");
        return new VenuesResult(venues);
    }

    private void addImageUrlToVenue(RestogramVenue[] venues, long id, String imageUrl) {

        for (RestogramVenue venue : venues) {
            if (venue != null && venue.getId() == id) {
                venue.setImageUrl(imageUrl);
            }
        }
    }

    /**
     * Executes get photos request
     */
    private PhotosResult doGetPhotos(String venueID, RestogramFilterType filterType) {
        LocationSearchFeed locationSearchFeed = null;
        try {
            Credentials credentials = m_factory.createInstagramCredentials();
            log.info("instagram credentials type = " + credentials.getType());
            Instagram instagram = new Instagram(credentials.getClientId());
            locationSearchFeed = instagram.searchFoursquareVenue(venueID);
        }
        catch (InstagramException e) {
            log.warning("first search for venue: " + venueID + " has failed, retry");
            e.printStackTrace();
            try {
                Credentials credentials = m_factory.createInstagramCredentials();
                log.info("instagram credentials type = " + credentials.getType());
                Instagram instagram = new Instagram(credentials.getClientId());
                locationSearchFeed = instagram.searchFoursquareVenue(venueID);
            }
            catch (InstagramException e2) {
                log.severe("second search for venue: " + venueID + "has failed");
                e.printStackTrace();
            }
        }

        List<Location> locationList = locationSearchFeed.getLocationList();
        if (locationList.isEmpty()) { // TODO: handle in a different way?
            log.severe("venue:  " + venueID + " not found");
            return null;
        }
        long locationId = locationList.get(0).getId(); // TODO: what if we get multiple locations?

        MediaFeed recentMediaByLocation;
        try {
            Credentials credentials = m_factory.createInstagramCredentials();
            log.info("instagram credentials type = " + credentials.getType());
            Instagram instagram = new Instagram(credentials.getClientId());
            recentMediaByLocation = instagram.getRecentMediaByLocation(locationId);
        }
        catch(InstagramException e) {
            log.warning("first recent media search for venue: " + venueID + "has failed, retry");
            e.printStackTrace();

            try {
                Credentials credentials = m_factory.createInstagramCredentials();
                log.info("instagram credentials type = " + credentials.getType());
                Instagram instagram = new Instagram(credentials.getClientId());
                recentMediaByLocation = instagram.getRecentMediaByLocation(locationId);
            }
            catch (InstagramException e2) {
                log.severe("second search for recent media for venue: " + venueID + "has failed");
                e2.printStackTrace();
                return null;
            }
        }

        return createPhotosResult(recentMediaByLocation, filterType, venueID);
    }

    /**
     * Executes get photos request
     */
    private PhotosResult doGetPhotos(Pagination pagination, RestogramFilterType filterType, String venueId) {
        MediaFeed recentMediaByLocation;
        try {
            Credentials credentials = m_factory.createInstagramCredentials();
            log.info("instagram credentials type = " + credentials.getType());
            Instagram instagram = new Instagram(credentials.getClientId());
            recentMediaByLocation = instagram.getRecentMediaNextPage(pagination);
        }
        catch(InstagramException e) {
            log.warning("first next media search has failed, retry");
            e.printStackTrace();

            try {
                Credentials credentials = m_factory.createInstagramCredentials();
                log.info("instagram credentials type = " + credentials.getType());
                Instagram instagram = new Instagram(credentials.getClientId());
                recentMediaByLocation = instagram.getRecentMediaNextPage(pagination);
            }
            catch (InstagramException e2) {
                log.severe("second next for recent media has failed");
                e2.printStackTrace();
                return null;
            }
        }

        return createPhotosResult(recentMediaByLocation, filterType, venueId);
    }

    private PhotosResult createPhotosResult(MediaFeed recentMediaByLocation, RestogramFilterType filterType, String originVenueId) {
        if(recentMediaByLocation == null) {
            log.severe("next media search returned no media");
            return null;
        }

        List<MediaFeedData> data = recentMediaByLocation.getData();
        if(data == null) {
            log.severe("next media search returned no media");
            return null;
        }

        // converts to ids
        final String[] instaIds = new String[data.size()];
        int i = 0;
        for (final MediaFeedData currMediaFeedData : data)
            instaIds[i++] = currMediaFeedData.getId();

        // gets filter rules for photos
        Map<String,Boolean> photoToRule = null;
        try
        {
            photoToRule = DataManager.getPhotoToRuleMapping(instaIds);
        } catch (LeanException e)
        {
            e.printStackTrace();
            log.severe("cannot get photos filter rules");
        }

        // enque filtering task for unknown photos
        final Map<String,String> photoIdToUrl = new HashMap<>();
        for (final MediaFeedData currMediaFeedData : data)
        {
            final String currId = currMediaFeedData.getId();
            if (!photoToRule.containsKey(currId))
                // TODO: try low resolution
                photoIdToUrl.put(currId, currMediaFeedData.getImages().getStandardResolution().getImageUrl());
        }

        if (!photoIdToUrl.isEmpty())
            TasksManager.enqueueFilterTask(originVenueId, photoIdToUrl);

        log.info("fetched " + data.size() + " photos");
        if (filterType != RestogramFilterType.None)
        {
            final RestogramFilter restogramFilter =
                    RestogramFilterFactory.createFilter(filterType, photoToRule);
            data = restogramFilter.doFilter(data);
        }

        RestogramPhoto[] photos = new RestogramPhoto[data.size()];

        i = 0;
        for (MediaFeedData media : data) {
            photos[i] = convert(media, originVenueId);
            if (AuthService.isUserLoggedIn()) {
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

    private RestogramPhoto convert(final MediaFeedData media, String originVenueId) {
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
                media.getType(), user, originVenueId).encodeStrings();
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
        RestogramVenue result = null;
        String photoUrl;

        try {
            fi.foyt.foursquare.api.entities.Location location = venue.getLocation();

            Contact contact = venue.getContact();
            String phone = "";
            if(contact != null)
                phone = contact.getPhone();

            result = new RestogramVenue(venue.getId(),
                    venue.getName(),
                    location.getAddress(),
                    location.getCity(),
                    location.getState(),
                    location.getPostalCode(),
                    location.getCountry(),
                    location.getLat(),
                    location.getLng(),
                    0.0, // Cannot use location.getDistance()
                    venue.getUrl(),
                    phone);

            Photos photos = venue.getPhotos();
            if(photos == null)
                return result;

            PhotoGroup[] groups = photos.getGroups();
            if(groups == null || groups.length < 2)
                return result;

            PhotoGroup group = groups[1];
            Photo[] items = group.getItems();
            if(items == null || items.length == 0)
                return result;

            photoUrl = items[0].getUrl();

            result.setDescription(venue.getDescription());
            result.setImageUrl(photoUrl);
        }
        catch(Exception e) {
            log.severe("venue object conversion failed");
            return result;
        }

        return result.encodeStrings();
    }

    private static final Logger log = Logger.getLogger(RestogramServiceImpl.class.getName());
    private ICredentialsFactory m_factory;
}
