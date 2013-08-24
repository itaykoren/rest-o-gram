package rest.o.gram.service;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.urlfetch.*;
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
import rest.o.gram.ApisConverters;
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

import java.net.URL;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
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

        final RestogramVenue venue = ApisConverters.convertToRestogramVenue(v);

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

        return cacheVenue(ApisConverters.convertToRestogramVenue(compVenue));
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
            venues[i] = ApisConverters.convertToRestogramVenue(arr[i]);
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

        PhotosResult cachedPhotosResult = null;
        if (StringUtils.isBlank(token) || DataManager.isValidCursor(token))
        {
            // fetch cached photos of given venue
            cachedPhotosResult = DataManager.fetchPhotosFromCache(venueId, token);
            // set private data for entities
            if (AuthService.isUserLoggedIn() && hasPhotos(cachedPhotosResult))
            {
                final Set<String> favIds = DataManager.fetchFavoritePhotoIds();
                for (final RestogramPhoto currPhoto : cachedPhotosResult.getPhotos())
                {
                    if (favIds.contains(currPhoto.getInstagram_id()))
                        currPhoto.set_favorite(true);
                }
            }
        }

        // if got enough results from cache, return results
        if (cachedPhotosResult != null &&
            cachedPhotosResult.getPhotos().length > Defs.Request.MIN_PHOTOS_PER_REQUEST)
        {
            log.info(String.format("got enough photos from cache - [%d]", cachedPhotosResult.getPhotos().length));
            log.info(String.format("sending [%d] photos to client", cachedPhotosResult.getPhotos().length));
            return cachedPhotosResult;
        }
        else // otherwise, not enough results were found, add more from Instagram
        {
            if (hasPhotos(cachedPhotosResult))
                log.info(String.format("not enough photos from cache - [%d], fetch from instagram", cachedPhotosResult.getPhotos().length));
            final String insragramToken = cachedPhotosResult != null ? cachedPhotosResult.getToken() : token;
            PhotosResult photosFromInstagram = doGetInstagramPhotos(venueId, filterType, insragramToken);
            PhotosResult mergedResults = mergeResults(cachedPhotosResult, photosFromInstagram);
            if (hasPhotos(mergedResults) && mergedResults.getPhotos().length <= Defs.Request.MIN_PHOTOS_PER_REQUEST)
            {
                photosFromInstagram = doGetInstagramPhotos(venueId, filterType, mergedResults.getToken());
                mergedResults = mergeResults(mergedResults, photosFromInstagram);
            }
            log.info(String.format("sending [%d] photos to client",
                     hasPhotos(mergedResults) ? mergedResults.getPhotos().length : 0));
            return mergedResults;
        }
    }

    private static boolean hasPhotos(PhotosResult cachedPhotosResult) {
        return cachedPhotosResult != null &&
               cachedPhotosResult.getPhotos() != null &&
               cachedPhotosResult.getPhotos().length != 0;
    }

    private PhotosResult mergeResults(PhotosResult first, PhotosResult second) {

        if (!hasPhotos(first))
            return second;

        if (!hasPhotos(second))
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

        if (recentMediaByLocation == null || recentMediaByLocation.getData() == null)
        {
            log.severe("media search returned no media");
            return null;
        }

        List<MediaFeedData> data = recentMediaByLocation.getData();
        log.info(String.format("got [%d] photos from instagram", data.size()));
        data = removeCachedPhotos(data);
        log.info(String.format("kept [%d] photos after checking cache", data.size()));
        addPhotosToQueue(data, venueId);
        log.info(String.format("sending [%d] photos for filtering", data.size()));
        final List<MediaFeedData> whiteList = filterPhotosIfNeeded(data, filterType);
        log.info(String.format("received [%d] photos after filtering", data.size()));

        final RestogramPhoto[] photos = convertMediaFeedDataToRestogramPhotos(data, whiteList, venueId);

        log.info(String.format("GOT [%d] PHOTOS", photos.length));
        final Pagination pagination = recentMediaByLocation.getPagination();
        log.info("HAS MORE? " + (StringUtils.isNotBlank(pagination.getNextUrl()) ? "YES!" : "NO!"));
        token = (StringUtils.isNotBlank(pagination.getNextUrl()) ?
                new Gson().toJson(pagination) : null);

        return new PhotosResult(photos, token);
    }

    private long getInstagramLocationId(String venueID) {

        LocationSearchFeed locationSearchFeed = null;
        final URLFetchService fetcher = URLFetchServiceFactory.getURLFetchService();
        try
        {
            final URL url = new URL(Defs.Transport.BASE_HOST_NAME + "/get-location");
            final HTTPRequest req = new HTTPRequest(url, HTTPMethod.POST, FetchOptions.Builder.withDeadline(6));
            req.setPayload(venueID.getBytes());
            log.info("get-location first fetch");
            final Future<HTTPResponse> firstRequest = fetcher.fetchAsync(req);
            log.info("get-location second fetch");
            final Future<HTTPResponse> secondRequest = fetcher.fetchAsync(req);

            while (!firstRequest.isDone() && !secondRequest.isDone()) { }
            log.info("one of the fetch operations is done!");
            Future<HTTPResponse> done = null;
            Future<HTTPResponse> other = null;
            if (firstRequest.isDone())
            {
                log.info("first operation is done");
                done = firstRequest;
                other = secondRequest;
            }
            else // if (secondRequest.isDone())
            {
                log.info("second operation is done");
                done = secondRequest;
                other = firstRequest;
            }

            HTTPResponse resp = null;
            if (done.isCancelled())
            {
                log.info("fetch operation has been cacncelled");
                resp = other.get(30, TimeUnit.SECONDS);
            }
            else
            {
                log.info("fetch operation has been successful");
                //other.cancel(true);
                resp = done.get(30, TimeUnit.SECONDS);
                log.info("got response");
            }
            if (resp.getResponseCode() != 200)
            {
                log.warning("error while getting location from instagram  : " + resp.getResponseCode());
                return 0;
            }

            log.info("getting venue data");
            locationSearchFeed =  new Gson().fromJson(new String(resp.getContent()), LocationSearchFeed.class);
            log.info("got response - location-id: " + locationSearchFeed.getLocationList().get(0).getId());
        }
        catch (Exception e)
        {
            log.severe(String.format("get instagram location for venue: [%s] has failed - [%s]", venueID, e.toString()));
            return 0;
        }

        List<Location> locationList = locationSearchFeed.getLocationList();
        if (locationList.isEmpty()) { // TODO: handle in a different way?
            log.severe("venue:  " + venueID + " not found");
            return 0;
        }

        return locationList.get(0).getId(); // TODO: what if we get multiple locations?
    }

    private RestogramPhoto[] convertMediaFeedDataToRestogramPhotos(List<MediaFeedData> data, List<MediaFeedData> whiteList, String venueId) {

        final Hashtable<String,MediaFeedData> idToWhiteListMapping = new Hashtable<>();
        for (final MediaFeedData currMedia : whiteList)
            idToWhiteListMapping.put(currMedia.getId(), currMedia);

        RestogramPhoto[] photos = new RestogramPhoto[data.size()];

        int i = 0;
        for (final MediaFeedData media : data) {
            photos[i] = ApisConverters.convertToRestogramPhoto(media, venueId);
            if (idToWhiteListMapping.containsKey(media.getId()))
                photos[i].setApproved(true);
            ++i;
        }

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

        // converts to ids
        final String[] instaIds = new String[data.size()];
        int i = 0;
        for (final MediaFeedData currMediaFeedData : data)
            instaIds[i++] = currMediaFeedData.getId();

        // gets filter rules for photos
        Map<String, Boolean> photoToRule = null;
        try
        {
            photoToRule = DataManager.getPhotoToRuleMapping(instaIds);
        } catch (LeanException e)
        {
            e.printStackTrace();
            log.severe("cannot get photos filter rules");
        }

        // enqueue filtering task for unknown photos
        List<MediaFeedData> mediaFeedDatas = new ArrayList<>();
        for (final MediaFeedData currMediaFeedData : data)
        {
            final String currId = currMediaFeedData.getId();
            if (!photoToRule.containsKey(currId) && !DataManager.isPhotoPending(currId))
            {
                final Images currImages = currMediaFeedData.getImages();
                if (currImages != null && currImages.getStandardResolution() != null)
                    mediaFeedDatas.add(currMediaFeedData);
            }
        }

        // enquing as a task to queue
        if (!mediaFeedDatas.isEmpty())
            TasksManager.enqueueFilterTask(originVenueId, mediaFeedDatas);

        // setting as pending photos
        for (final MediaFeedData currMediaFeedData : data)
            DataManager.addPendingPhoto(ApisConverters.convertToRestogramPhoto(currMediaFeedData,
                                        originVenueId));
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

        if (isValidPaginationToken(token))
            pagination = new Gson().fromJson(token, Pagination.class);

        MediaFeed recentMediaByLocation;
        try
        {
            Credentials credentials = m_factory.createInstagramCredentials();
            log.info("instagram credentials type = " + credentials.getType());
            Instagram instagram = new Instagram(credentials.getClientId());
            recentMediaByLocation = getRecentMedia(venueId, pagination, instagram);
        } catch (InstagramException e)
        {
            log.warning("first media search has failed, retry");
            e.printStackTrace();

            try
            {
                Credentials credentials = m_factory.createInstagramCredentials();
                log.info("instagram credentials type = " + credentials.getType());
                Instagram instagram = new Instagram(credentials.getClientId());
                recentMediaByLocation = getRecentMedia(venueId, pagination, instagram);
            } catch (InstagramException e2)
            {
                log.severe("second media search has failed");
                e2.printStackTrace();
                return null;
            }
        }
        return recentMediaByLocation;
    }

    private MediaFeed getRecentMedia(String venueId, Pagination pagination, Instagram instagram) throws InstagramException {
        if (pagination != null)
            return instagram.getRecentMediaNextPage(pagination);
        else
        {
            long locationID = getInstagramLocationId(venueId);
            if (locationID == 0)
            {
                log.severe("cannot find location for venue: " + venueId);
                return null;
            }
            return instagram.getRecentMediaByLocation(locationID);
        }
    }

    private static boolean isValidPaginationToken(final String token) {
        return !StringUtils.isBlank(token) && !token.equals(Defs.Tokens.FINISHED_FETCHING_FROM_CACHE);
    }

    private static final Logger log = Logger.getLogger(RestogramServiceImpl.class.getName());
    private ICredentialsFactory m_factory;
}