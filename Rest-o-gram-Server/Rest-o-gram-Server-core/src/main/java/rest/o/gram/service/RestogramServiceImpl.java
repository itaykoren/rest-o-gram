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
import org.jinstagram.entity.common.Pagination;
import org.jinstagram.entity.locations.LocationSearchFeed;
import org.jinstagram.entity.users.feed.MediaFeed;
import org.jinstagram.entity.users.feed.MediaFeedData;
import org.jinstagram.exceptions.InstagramException;
import rest.o.gram.ApisAccessManager;
import rest.o.gram.ApisConverters;
import rest.o.gram.DataStoreConverters;
import rest.o.gram.Defs;
import rest.o.gram.tasks.TasksManager;
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

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 31/03/13
 */
public class RestogramServiceImpl implements RestogramService {

    public RestogramServiceImpl() {
        try
        {
            m_CredentialsFactory = new RandomCredentialsFactory();

        } catch (Exception e)
        {
            log.severe("an error occurred while initializing the service");
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
            Credentials credentials = m_CredentialsFactory.createFoursquareCredentials();
            log.info("foursquare credentials type = " + credentials.getType());
            FoursquareApi foursquare = new FoursquareApi(credentials.getClientId(), credentials.getClientSecret(), "");
            result = foursquare.venue(venueID);
        } catch (FoursquareApiException e) {
            log.warning("first venue  " + venueID + " retrieval has failed, retry");
            try {
                Credentials credentials = m_CredentialsFactory.createFoursquareCredentials();
                log.info("foursquare credentials type = " + credentials.getType());
                FoursquareApi foursquare = new FoursquareApi(credentials.getClientId(), credentials.getClientSecret(), "");
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
            Credentials credentials = m_CredentialsFactory.createFoursquareCredentials();
            log.info("foursquare credentials type = " + credentials.getType());
            FoursquareApi foursquare = new FoursquareApi(credentials.getClientId(), credentials.getClientSecret(), "");
            compVenue = foursquare.venue(id).getResult();
        } catch (FoursquareApiException e) {
            log.warning("first get venue has failed, retry");
            try {
                Credentials credentials = m_CredentialsFactory.createFoursquareCredentials();
                log.info("foursquare credentials type = " + credentials.getType());
                FoursquareApi foursquare = new FoursquareApi(credentials.getClientId(), credentials.getClientSecret(), "");
                compVenue = foursquare.venue(id).getResult();
            } catch (FoursquareApiException e2) {
                log.severe("second get venue has failed");
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
            Credentials credentials = m_CredentialsFactory.createFoursquareCredentials();
            log.info("foursquare credentials type = " + credentials.getType());
            FoursquareApi foursquare = new FoursquareApi(credentials.getClientId(), credentials.getClientSecret(), "");
            result = foursquare.venuesSearch(params);
        } catch (FoursquareApiException e) {
            // TODO: test the "second-chance" policy
            try {
                log.warning("first venue search has failed, retry");

                Credentials credentials = m_CredentialsFactory.createFoursquareCredentials();
                log.info("foursquare credentials type = " + credentials.getType());
                FoursquareApi foursquare = new FoursquareApi(credentials.getClientId(), credentials.getClientSecret(), "");
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
            log.info(String.format("got enough photos from cache - %d", cachedPhotosResult.getPhotos().length));
            log.info(String.format("sending %d photos to client", cachedPhotosResult.getPhotos().length));
            return cachedPhotosResult;
        }
        else // otherwise, not enough results were found, add more from Instagram
        {
            if (hasPhotos(cachedPhotosResult))
                log.info(String.format("not enough photos from cache - %d, fetch from instagram", cachedPhotosResult.getPhotos().length));
            final String insragramToken = cachedPhotosResult != null ? cachedPhotosResult.getToken() : token;
            PhotosResult photosFromInstagram = doGetInstagramPhotos(venueId, filterType, insragramToken);
            PhotosResult mergedResults = mergeResults(cachedPhotosResult, photosFromInstagram);
            if (hasPhotos(mergedResults) && mergedResults.getPhotos().length <= Defs.Request.MIN_PHOTOS_PER_REQUEST)
            {
                photosFromInstagram = doGetInstagramPhotos(venueId, filterType, mergedResults.getToken());
                mergedResults = mergeResults(mergedResults, photosFromInstagram);
            }
            log.info(String.format("sending %d photos to client",
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

        final MediaFeed recentMediaByLocation = fetchInstagramPhotos(venueId, token);

        if (InstagramUtils.isNullOrEmpty(recentMediaByLocation))
        {
            log.warning("media search returned no media");
            return null;
        }

        List<MediaFeedData> data = recentMediaByLocation.getData();
        log.info(String.format("got %d photos from instagram", data.size()));
        data = removeCachedPhotos(data);
        log.info(String.format("kept %d photos after checking cache", data.size()));
        addPhotosToQueue(data, venueId);
        log.info(String.format("sending %d photos for filtering", data.size()));
        final List<MediaFeedData> whiteList = filterPhotosIfNeeded(data, filterType);
        log.info(String.format("received %d photos after filtering", data.size()));

        final RestogramPhoto[] photos = convertMediaFeedDataToRestogramPhotos(data, whiteList, venueId);

        log.info(String.format("got %d photos", photos.length));
        final Pagination pagination = recentMediaByLocation.getPagination();
        log.info("has more? " + (StringUtils.isNotBlank(pagination.getNextUrl()) ? "yes!" : "no!"));
        token = (StringUtils.isNotBlank(pagination.getNextUrl()) ?
                new Gson().toJson(pagination) : null);

        return new PhotosResult(photos, token);
    }

    private long getInstagramLocationId(final String venueID) {
        log.info(String.format("instagram request for getting location %s started", venueID));
        final ApisAccessManager.PrepareRequest prepareRequest =
                new ApisAccessManager.PrepareRequest() {
            @Override
            public byte[] getPayload() {
                return venueID.getBytes();
            }
        };
        final LocationSearchFeed locationSearchFeed =
                ApisAccessManager.parallelInstagramRequest(Defs.Instagram.RequestType.GetLocation,
                                                           prepareRequest, LocationSearchFeed.class);
        if (InstagramUtils.isNullOrEmpty(locationSearchFeed))
        {
            log.severe(String.format("venue: %s not found", venueID ));
            return 0;
        }

        log.info("got result from instagram - location-id: " + locationSearchFeed.getLocationList().get(0).getId());
        return locationSearchFeed.getLocationList().get(0).getId(); // TODO: what if we get multiple locations?
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
            Credentials credentials = m_CredentialsFactory.createInstagramCredentials();
            log.info("instagram credentials type = " + credentials.getType());
            Instagram instagram = new Instagram(credentials.getClientId());
            recentMediaByLocation = getRecentMedia(venueId, pagination, instagram);
        } catch (InstagramException e)
        {
            log.warning("first media search has failed, retry");

            try
            {
                Credentials credentials = m_CredentialsFactory.createInstagramCredentials();
                log.info("instagram credentials type = " + credentials.getType());
                Instagram instagram = new Instagram(credentials.getClientId());
                recentMediaByLocation = getRecentMedia(venueId, pagination, instagram);
            } catch (InstagramException e2)
            {
                log.severe("second media search has failed");
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
            final long locationID = getInstagramLocationId(venueId);
            if (locationID == 0)
            {
                log.severe("cannot find location for venue: " + venueId);
                return null;
            }
            log.info(String.format("instagram request for getting media feed by location %d started", locationID));
            final ApisAccessManager.PrepareRequest prepareRequest =
                    new ApisAccessManager.PrepareRequest() {
                        @Override
                        public byte[] getPayload() {
                            return String.valueOf(locationID).getBytes();
                        }
                    };
            final MediaFeed mediaFeed =
                    ApisAccessManager.parallelInstagramRequest(Defs.Instagram.RequestType.GetMediaByLocation,
                            prepareRequest, MediaFeed.class);
            if (InstagramUtils.isNullOrEmpty(mediaFeed))
            {
                log.warning(String.format("media feed for location %d not found", locationID));
                return null;
            }

            log.info("got result from instagram - mediafeed");
            return mediaFeed;
        }
    }

    private static boolean isValidPaginationToken(final String token) {
        return !StringUtils.isBlank(token) && !token.equals(Defs.Tokens.FINISHED_FETCHING_FROM_CACHE);
    }

    private static final Logger log = Logger.getLogger(RestogramServiceImpl.class.getName());
    private ICredentialsFactory m_CredentialsFactory;
}