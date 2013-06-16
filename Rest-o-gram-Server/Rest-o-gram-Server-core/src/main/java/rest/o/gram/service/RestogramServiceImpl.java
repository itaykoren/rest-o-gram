package rest.o.gram.service;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.repackaged.org.joda.time.DateTime;
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
import org.jinstagram.entity.users.feed.MediaFeed;
import org.jinstagram.entity.users.feed.MediaFeedData;
import org.jinstagram.exceptions.InstagramException;
import rest.o.gram.ApisCoonverters;
import rest.o.gram.DataStoreConverters;
import rest.o.gram.Defs;
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
        } catch (Exception e) {
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
            } catch (FoursquareApiException e2) {
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
        if (v == null) {
            log.severe("extracting info from venue has failed");
            return null;
        }

        final RestogramVenue venue = ApisCoonverters.convertToRestogramVenue(v);

        cacheVenue(venue);

        return new VenueResult(venue);
    }

    /**
     * @return array of media related to venue given its ID
     */
    @Override
    public PhotosResult getPhotos(String venueID) {
        return getPhotos(venueID, RestogramFilterType.None);
    }

    /**
     * @return array of media related to venue given its ID, after applying given filter
     */
    @Override
    public PhotosResult getPhotos(String venueId, RestogramFilterType filterType) {
        return doGetPhotos(venueId, filterType, null);
    }

    /**
     * @param token identifying previous session for getting next photos
     * @return array of media related to venue given its ID
     */
    @Override
    public PhotosResult getNextPhotos(String token, String originVenueId) {
        return getNextPhotos(token, RestogramFilterType.None, originVenueId);
    }

    /**
     * @param token identifying previous session for getting next photos
     * @return array of media related to venue given its ID, after applying given filter
     */
    @Override
    public PhotosResult getNextPhotos(String token, RestogramFilterType filterType, String originVenueId) {
        return doGetPhotos(originVenueId, filterType, token);
    }

//    @Override
//    public boolean cachePhoto(String id, String originVenueId) {
//        // TODO: check if photo is already in cache
//        long lid;
//        try {
//            lid = InstagramUtils.extractMediaId(id);
//        }
//        catch (Exception e) {
//            log.severe("cannot extract media id from media feed string id");
//            e.printStackTrace();
//            return false;
//        }
//
//        MediaInfoFeed mediaInfo;
//        try {
//            Credentials credentials = m_factory.createInstagramCredentials();
//            log.info("instagram credentials type = " + credentials.getType());
//            Instagram instagram = new Instagram(credentials.getClientId());
//            mediaInfo = instagram.getMediaInfo(lid);
//        }
//        catch (InstagramException e) {
//            log.warning("first get photo has failed, retry");
//            e.printStackTrace();
//            try {
//                Credentials credentials = m_factory.createInstagramCredentials();
//                log.info("instagram credentials type = " + credentials.getType());
//                Instagram instagram = new Instagram(credentials.getClientId());
//                mediaInfo = instagram.getMediaInfo(lid);
//            }
//            catch (InstagramException e2) {
//                log.severe("second get photo has failed");
//                e2.printStackTrace();
//                return false;
//            }
//        }
//
//        final RestogramPhoto photo = convertToRestogramPhoto(mediaInfo.getData(), originVenueId);
//        try {
//            DatastoreUtils.putPublicEntity(Kinds.PHOTO,
//                    photo.getInstagram_id(), DataStoreConverters.photoToProps(photo));
//        }
//        catch (LeanException e) {
//            log.severe("caching the photo in DS has failed");
//            e.printStackTrace();
//            return false;
//        }
//        return true;
//    }

//    @Override
//    public RestogramPhoto[] fetchPhotosFromCache(String[] ids) {
//        Collection<Entity> entities = null;
//        try {
//            entities = DatastoreUtils.getPublicEntities(Kinds.PHOTO, ids);
//        }
//        catch (LeanException e) {
//            log.severe("fetching photos from cache has failed");
//            e.printStackTrace();
//        }
//
//        RestogramPhoto[] photos = new RestogramPhoto[entities.size()];
//        int i = 0;
//        for (final Entity currEntity : entities)
//            photos[i++] = DataStoreConverters.entityToPhoto(currEntity).encodeStrings();
//
//        Arrays.sort(photos, new Comparator<RestogramPhoto>() {
//            @Override
//            public int compare(RestogramPhoto o1, RestogramPhoto o2) {
//                return o1.getInstagram_id().compareTo(o2.getInstagram_id());
//            }
//        });
//        return photos;
//    }

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

        return cacheVenue(ApisCoonverters.convertToRestogramVenue(compVenue));
    }

    private boolean cacheVenue(final RestogramVenue venue) {

        try {
            DatastoreUtils.putPublicEntity(Kinds.VENUE, venue.getFoursquare_id(), DataStoreConverters.venueToProps(venue));
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
        } catch (LeanException e) {
            log.severe("fetching venues fromm cache has failed");
            e.printStackTrace();
        }

        RestogramVenue[] venues = new RestogramVenue[entities.size()];
        int i = 0;
        for (final Entity currEntity : entities)
            venues[i++] = DataStoreConverters.entityToVenue(currEntity).encodeStrings();

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
        } catch (FoursquareApiException e) {
            // TODO: test the "second-chance" policy
            try {
                log.warning("first venue search has failed, retry");
                e.printStackTrace();

                Credentials credentials = m_factory.createFoursquareCredentials();
                log.info("foursquare credentials type = " + credentials.getType());
                FoursquareApi foursquare = new FoursquareApi(credentials.getClientId(), credentials.getClientSecret(), "");
                result = foursquare.venuesSearch(params);
            } catch (FoursquareApiException e2) {
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
        if (arr == null || length == 0) {
            log.severe("venue search returned no venues");
            return null;
        }

        String[] venueIds = new String[length];
        RestogramVenue[] venues = new RestogramVenue[length];

        for (int i = 0; i < length; i++) {
            venues[i] = ApisCoonverters.convertToRestogramVenue(arr[i]);
            venueIds[i] = arr[i].getId();
            if (AuthService.isUserLoggedIn()) {
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

    private PhotosResult doGetPhotos(String venueId, RestogramFilterType filterType, String token) {

        long start = new DateTime().getMillis();
        PhotosResult cachedPhotosResult = null;
        if (token == null || DataManager.isValidCursor(token)) {
            // fetch cached photos of given venue
            cachedPhotosResult = DataManager.fetchPhotosFromCache(venueId, token);
            log.severe(String.format("checking cache took [%d] millis", (new DateTime().getMillis() - start)));

            // sets private data for entities
            if (AuthService.isUserLoggedIn() && cachedPhotosResult != null &&
                cachedPhotosResult.getPhotos() != null)
            {
                final Set<String> favIds = DataManager.fetchFavoritePhotoIds();
                for (final RestogramPhoto currPhoto : cachedPhotosResult.getPhotos())
                {
                    if (favIds.contains(currPhoto.getInstagram_id()))
                        currPhoto.set_favorite(true);
                }
            }

            log.severe(String.format("setting private data for entities took [%d] millis", (new DateTime().getMillis() - start)));

        }

        // reset token if needed
        token = resetTokenIfNeeded(token, cachedPhotosResult);

        // if no results found, go to Instagram
        if (cachedPhotosResult == null ||
                cachedPhotosResult.getPhotos() == null ||
                cachedPhotosResult.getPhotos().length == 0) {

            PhotosResult instagramPhotos = doGetInstagramPhotos(venueId, filterType, token);
            log.severe(String.format("no photos in cache. first fetch from instagram took [%d] millis", (new DateTime().getMillis() - start)));
            instagramPhotos = getMoreResultsIfNeeded(instagramPhotos, venueId, filterType);
            log.severe(String.format("no photos in cache. second fetch from instagram took [%d] millis", (new DateTime().getMillis() - start)));
            return instagramPhotos;
        }

        // if enough results were found, return results
        if (cachedPhotosResult.getPhotos().length > Defs.Request.MIN_PHOTOS_PER_REQUEST) {
            return cachedPhotosResult;
        }
        // otherwise, not enough results were found, add more from Instagram
        else {
            PhotosResult photosFromInstagram = doGetInstagramPhotos(venueId, filterType, token);
            log.severe(String.format("some photos were found in cache. first fetch from instagram took [%d] millis", (new DateTime().getMillis() - start)));
            PhotosResult mergedResults = mergeResults(cachedPhotosResult, photosFromInstagram);
            log.severe(String.format("adding up cache and instagram photos took [%d] millis", (new DateTime().getMillis() - start)));
            mergedResults = getMoreResultsIfNeeded(mergedResults, venueId, filterType);
            log.severe(String.format("still not enough photos were found. second fetch from instagram took [%d] millis", (new DateTime().getMillis() - start)));
            return mergedResults;
        }
    }

    private PhotosResult getMoreResultsIfNeeded(PhotosResult currentPhotos, String venueId, RestogramFilterType filterType) {

        if (currentPhotos != null &&
                currentPhotos.getPhotos() != null &&
                currentPhotos.getToken() != null &&
                currentPhotos.getPhotos().length < Defs.Request.MIN_PHOTOS_PER_REQUEST) {

            PhotosResult nextPhotos = doGetInstagramPhotos(venueId, filterType, currentPhotos.getToken());
            return mergeResults(currentPhotos, nextPhotos);
        }
        return currentPhotos;
    }

    private String resetTokenIfNeeded(String token, PhotosResult result) {

        if ((result != null &&
                result.getToken() != null &&
                result.getToken().equals(Defs.Tokens.FINISHED_FETCHING_FROM_CACHE)) ||
                (token != null && token.equals(Defs.Tokens.FINISHED_FETCHING_FROM_CACHE))) {
            // finished fetching from cache, reset token
            return null;
        }
        return token;
    }

    private PhotosResult mergeResults(PhotosResult first, PhotosResult second) {

        if (first == null || first.getPhotos() == null)
            return second;

        if (second == null || second.getPhotos() == null)
            return first;

        RestogramPhoto[] firstPhotos = first.getPhotos();
        RestogramPhoto[] secondPhotos = second.getPhotos();
        int firstPhotosLength = firstPhotos.length;
        int secondPhotosLength = secondPhotos.length;

        RestogramPhoto[] mergedPhotos = new RestogramPhoto[firstPhotosLength + secondPhotosLength];

        // add photos from cache to results
        System.arraycopy(firstPhotos, 0, mergedPhotos, 0, firstPhotosLength);

        // add photos from Instagram to results
        System.arraycopy(secondPhotos, 0, mergedPhotos, firstPhotosLength, secondPhotosLength);

        return new PhotosResult(mergedPhotos, second.getToken());
    }

    private PhotosResult doGetInstagramPhotos(String venueId, RestogramFilterType filterType, String token) {

        MediaFeed recentMediaByLocation = fetchInstagramPhotos(venueId, token);

        if (recentMediaByLocation == null || recentMediaByLocation.getData() == null) {
            log.severe("next media search returned no media");
            return null;
        }

        List<MediaFeedData> data = recentMediaByLocation.getData();

        data = removeCachedPhotos(data);

        addPhotosToQueue(data, venueId);

        data = filterPhotosIfNeeded(data, filterType);

        RestogramPhoto[] photos = convertMediaFeedDataToRestogramPhoto(data, venueId);

        log.info("GOT " + photos.length + " PHOTOS");
        final Pagination pagination = recentMediaByLocation.getPagination();
        log.info("HAS MORE? " + (StringUtils.isNotBlank(pagination.getNextUrl()) ? "YES!" : "NO!"));
        token = (StringUtils.isNotBlank(pagination.getNextUrl()) ?
                new Gson().toJson(pagination) : null);

        return new PhotosResult(photos, token);
    }

    private long getInstagramLocationId(String venueID) {

        LocationSearchFeed locationSearchFeed = null;
        try {
            Credentials credentials = m_factory.createInstagramCredentials();
            log.info("instagram credentials type = " + credentials.getType());
            Instagram instagram = new Instagram(credentials.getClientId());
            locationSearchFeed = instagram.searchFoursquareVenue(venueID);
        } catch (InstagramException e) {
            log.warning("first search for venue: " + venueID + " has failed, retry");
            e.printStackTrace();
            try {
                Credentials credentials = m_factory.createInstagramCredentials();
                log.info("instagram credentials type = " + credentials.getType());
                Instagram instagram = new Instagram(credentials.getClientId());
                locationSearchFeed = instagram.searchFoursquareVenue(venueID);
            } catch (InstagramException e2) {
                log.severe("second search for venue: " + venueID + "has failed");
                e.printStackTrace();
            }
        }

        List<Location> locationList = locationSearchFeed.getLocationList();
        if (locationList.isEmpty()) { // TODO: handle in a different way?
            log.severe("venue:  " + venueID + " not found");
            return 0;
        }

        return locationList.get(0).getId(); // TODO: what if we get multiple locations?
    }

    private RestogramPhoto[] convertMediaFeedDataToRestogramPhoto(List<MediaFeedData> data, String venueId) {

        RestogramPhoto[] photos = new RestogramPhoto[data.size()];

        int i = 0;
        for (final MediaFeedData media : data)
            photos[i] = ApisCoonverters.convertToRestogramPhoto(media, venueId);
        return photos;
    }

    private List<MediaFeedData> filterPhotosIfNeeded(List<MediaFeedData> data, RestogramFilterType filterType) {

        if (filterType != RestogramFilterType.None) {
            final RestogramFilter restogramFilter =
                    RestogramFilterFactory.createFilter(filterType);
            data = restogramFilter.doFilter(data);
        }

        return data;
    }

    private void addPhotosToQueue(List<MediaFeedData> data, String originVenueId) {

//        converts to ids
        final String[] instaIds = new String[data.size()];
        int i = 0;
        for (final MediaFeedData currMediaFeedData : data)
            instaIds[i++] = currMediaFeedData.getId();

//       gets filter rules for photos
        Map<String, Boolean> photoToRule = null;
        try {
            photoToRule = DataManager.getPhotoToRuleMapping(instaIds);
        } catch (LeanException e) {
            e.printStackTrace();
            log.severe("cannot get photos filter rules");
        }

//     enqueue filtering task for unknown photos
        final Map<String, String> photoIdToUrl = new HashMap<>();
        for (final MediaFeedData currMediaFeedData : data) {
            final String currId = currMediaFeedData.getId();
            if (!photoToRule.containsKey(currId) && !DataManager.isPhotoPending(currId)) {
                final Images currImages = currMediaFeedData.getImages();
                if (currImages != null && currImages.getStandardResolution() != null)
                    photoIdToUrl.put(currId, currImages.getStandardResolution().getImageUrl());
            }
        }

        if (!photoIdToUrl.isEmpty())
            TasksManager.enqueueFilterTask(originVenueId, photoIdToUrl);
    }

    private List<MediaFeedData> removeCachedPhotos(List<MediaFeedData> data) {

        List<MediaFeedData> dataNotInCache = new ArrayList<>();

        for (MediaFeedData currItem : data) {
            if (!DataManager.isPhotoApproved(currItem.getId()))
                dataNotInCache.add(currItem);
        }
        return dataNotInCache;
    }

    private MediaFeed fetchInstagramPhotos(String venueId, String token) {

        Pagination pagination = null;

        if (token != null) {
            pagination = new Gson().fromJson(token, Pagination.class);
        }

        MediaFeed recentMediaByLocation;
        try {
            Credentials credentials = m_factory.createInstagramCredentials();
            log.info("instagram credentials type = " + credentials.getType());
            Instagram instagram = new Instagram(credentials.getClientId());
            recentMediaByLocation = (token == null) ?
                    instagram.getRecentMediaByLocation(getInstagramLocationId(venueId)) :
                    instagram.getRecentMediaNextPage(pagination);
        } catch (InstagramException e) {
            log.warning("first next media search has failed, retry");
            e.printStackTrace();

            try {
                Credentials credentials = m_factory.createInstagramCredentials();
                log.info("instagram credentials type = " + credentials.getType());
                Instagram instagram = new Instagram(credentials.getClientId());
                recentMediaByLocation = (token == null) ?
                        instagram.getRecentMediaByLocation(getInstagramLocationId(venueId)) :
                        instagram.getRecentMediaNextPage(pagination);
            } catch (InstagramException e2) {
                log.severe("second next for recent media has failed");
                e2.printStackTrace();
                return null;
            }
        }
        return recentMediaByLocation;
    }

    private static final Logger log = Logger.getLogger(RestogramServiceImpl.class.getName());
    private ICredentialsFactory m_factory;
}
