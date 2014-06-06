package rest.o.gram.service;

import com.google.gson.Gson;
import com.leanengine.server.auth.AuthService;
import fi.foyt.foursquare.api.FoursquareApi;
import fi.foyt.foursquare.api.FoursquareApiException;
import fi.foyt.foursquare.api.Result;
import fi.foyt.foursquare.api.entities.CompactVenue;
import fi.foyt.foursquare.api.entities.CompleteVenue;
import fi.foyt.foursquare.api.entities.VenuesSearchResult;
import fi.foyt.foursquare.api.io.GAEIOHandler;
import org.apache.commons.lang3.StringUtils;
import org.jinstagram.Instagram;
import org.jinstagram.entity.common.Pagination;
import org.jinstagram.entity.locations.LocationSearchFeed;
import org.jinstagram.exceptions.InstagramException;
import rest.o.gram.ApisConverters;
import rest.o.gram.Defs;
import rest.o.gram.InstagramAccessManager;
import rest.o.gram.credentials.Credentials;
import rest.o.gram.credentials.ICredentialsFactory;
import rest.o.gram.credentials.RandomCredentialsFactory;
import rest.o.gram.data.DataManager;
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.entities.RestogramVenue;
import rest.o.gram.filters.RestogramFilter;
import rest.o.gram.filters.RestogramFilterFactory;
import rest.o.gram.filters.RestogramFilterType;
import rest.o.gram.iservice.RestogramService;
import rest.o.gram.results.PhotosResult;
import rest.o.gram.results.VenueResult;
import rest.o.gram.results.VenuesResult;
import rest.o.gram.server.RestogramServer;
import rest.o.gram.service.InstagramServices.Entities.RestogramPhotos;
import rest.o.gram.shared.CommonDefs;
import rest.o.gram.tasks.TasksManager;
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
        try {
            credentialsFactory = new RandomCredentialsFactory();
        } catch (Exception e) {
            log.severe("an error occurred while initializing the service");
        }
    }

    /**
     * @return array of venus near given location
     */
    @Override
    public VenuesResult getNearby(final double latitude, final double longitude) {
        return doGetNearby(latitude, longitude, -1);
    }

    /**
     * @return array of venues near given location within given radius
     */
    @Override
    public VenuesResult getNearby(final double latitude, final double longitude, final double radius) {
        return doGetNearby(latitude, longitude, radius);
    }

    private VenuesResult doGetNearby(final double latitude, final double longitude, final double radius) {
        final Map<String, String> params = new HashMap<String, String>();
        params.put("ll", String.format("%f,%f", latitude, longitude));
        params.put("categoryId", Defs.Foursquare.VENUE_CATEGORY);
        if (radius >= 0)
            params.put("radius", Double.toString(radius));
        else
            params.put("intent", "match");

        return doGetNearby(params);
    }

    /**
     * @return venue information according to its ID
     */
    @Override
    public VenueResult getInfo(final String venueID) {
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

        final RestogramVenue venue = ApisConverters.convertToRestogramVenue(completeVenue);
        if (venue != null) {
            if (!m_dataManager.cacheVenue(venue))
                log.warning(String.format("cannot save venue %s to cache", venue.getFoursquare_id()));
        }
        return new VenueResult(venue);
    }

    /**
     * @return array of media related to venue given its ID
     */
    @Override
    public PhotosResult getPhotos(final String venueID) {
        return getPhotos(venueID, RestogramFilterType.None);
    }

    /**
     * @return array of media related to venue given its ID, after applying given filter
     */
    @Override
    public PhotosResult getPhotos(final String venueId, final RestogramFilterType filterType) {
        return doGetPhotos(venueId, filterType, null);
    }

    /**
     * @param token identifying previous session for getting next photos
     * @return array of media related to venue given its ID
     */
    @Override
    public PhotosResult getNextPhotos(final String token, final String originVenueId) {
        return getNextPhotos(token, RestogramFilterType.None, originVenueId);
    }

    /**
     * @param token identifying previous session for getting next photos
     * @return array of media related to venue given its ID, after applying given filter
     */
    @Override
    public PhotosResult getNextPhotos(final String token, final RestogramFilterType filterType, final String originVenueId) {
        return doGetPhotos(originVenueId, filterType, token);
    }

    /**
     * Executes get nearby request
     */
    private VenuesResult doGetNearby(final Map<String, String> params) {
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

        final Map<String, RestogramVenue> idToVenueMapping = m_dataManager.fetchVenuesFromCache(venueIds);

        if (idToVenueMapping != null) {
            for (final RestogramVenue currVenue : venues) {
                if (idToVenueMapping.containsKey(currVenue.getFoursquare_id()))
                    currVenue.setImageUrl(idToVenueMapping.get(currVenue.getFoursquare_id()).getImageUrl());
            }
        }

        log.info("found " + venues.length + " venues!");
        return new VenuesResult(venues);
    }

    private PhotosResult doGetPhotos(final String venueId, final RestogramFilterType filterType, final String token) {

        // client shouldn't send a request when finished fetching from instagram, but just to be on the safe side
        if (noMorePhotos(token))
            return null;

        PhotosResult cachedPhotosResult = null;
        if (StringUtils.isBlank(token) || m_dataManager.isValidCursor(token)) {
            // fetch cached photos of given venue
            cachedPhotosResult = m_dataManager.fetchPhotosFromCache(venueId, token);

            // set as approved
            if (hasPhotos(cachedPhotosResult)) {
                for (final RestogramPhoto currPhoto : cachedPhotosResult.getPhotos())
                    currPhoto.setApproved(true);
            }

            //set as favorite
            if (hasPhotos(cachedPhotosResult))
                markFavoritePhotos(cachedPhotosResult);
        }

        // if got enough results from cache, return results
        if (hasPhotos(cachedPhotosResult) &&
                cachedPhotosResult.getPhotos().length > Defs.Request.MIN_PHOTOS_PER_REQUEST) {
            log.info(String.format("got enough photos from cache - %d", cachedPhotosResult.getPhotos().length));
            log.info(String.format("sending %d photos to client", cachedPhotosResult.getPhotos().length));
            return cachedPhotosResult;
        } else // otherwise, not enough results were found, add more from Instagram
        {
            if (hasPhotos(cachedPhotosResult))
                log.info(String.format("not enough photos from cache - %d, fetch from instagram", cachedPhotosResult.getPhotos().length));
            final String instagramToken = resolveInstagramToken(token);
            PhotosResult photosFromInstagram = doGetInstagramPhotos(venueId, filterType, instagramToken);
            PhotosResult mergedResults = mergeResults(cachedPhotosResult, photosFromInstagram);
            if (shouldFetchMorePhotosFromInstagram(mergedResults)) {
                photosFromInstagram = doGetInstagramPhotos(venueId, filterType, mergedResults.getToken());
                mergedResults = mergeResults(mergedResults, photosFromInstagram);
            }
            log.info(String.format("sending %d photos to client",
                    hasPhotos(mergedResults) ? mergedResults.getPhotos().length : 0));
            return mergedResults;
        }
    }

    private boolean shouldFetchMorePhotosFromInstagram(PhotosResult mergedResults) {
        return hasPhotos(mergedResults) &&
                mergedResults.getPhotos().length <= Defs.Request.MIN_PHOTOS_PER_REQUEST &&
                !noMorePhotos(mergedResults.getToken());
    }

    private boolean noMorePhotos(String token) {
        return token != null && token.equals(CommonDefs.Tokens.FINISHED_FETCHING_FROM_INSTAGRAM);
    }

    private String resolveInstagramToken(String token) {
        return isValidPaginationToken(token) ? token : null;
    }

    private void markFavoritePhotos(PhotosResult cachedPhotosResult) {
        if (AuthService.isUserLoggedIn()) {
            final Set<String> favIds = m_dataManager.fetchFavoritePhotoIds();
            if (favIds == null)
                return;

            for (final RestogramPhoto currPhoto : cachedPhotosResult.getPhotos()) {
                if (favIds.contains(currPhoto.getInstagram_id()))
                    currPhoto.set_favorite(true);
            }
        }
    }

    private boolean hasPhotos(final PhotosResult cachedPhotosResult) {
        return cachedPhotosResult != null &&
                cachedPhotosResult.getPhotos() != null &&
                cachedPhotosResult.getPhotos().length != 0;
    }

    private PhotosResult mergeResults(final PhotosResult first, final PhotosResult second) {

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

    private PhotosResult doGetInstagramPhotos(final String venueId, final RestogramFilterType filterType, String token) {

        final RestogramPhotos recentMediaByLocation = fetchInstagramPhotos(venueId, token);

        if (InstagramUtils.isNullOrEmpty(recentMediaByLocation)) {
            log.warning("media search returned no media");
            return null;
        }

        List<RestogramPhoto> data = recentMediaByLocation.getPhotos();
        log.info(String.format("got %d photos from instagram", data.size()));
        data = getUncachedPhotos(data);
        log.info(String.format("kept %d photos after checking cache", data.size()));
        addPhotosToQueue(data, venueId);

        filterPhotos(data, filterType);

        log.info(String.format("got %d photos", data.size()));
        final Pagination pagination = recentMediaByLocation.getPagination();
        log.info("has more? " + (StringUtils.isNotBlank(pagination.getNextUrl()) ? "yes!" : "no!"));
        token = StringUtils.isNotBlank(pagination.getNextUrl()) ?
                new Gson().toJson(pagination) : CommonDefs.Tokens.FINISHED_FETCHING_FROM_INSTAGRAM;

        return new PhotosResult(data.toArray(new RestogramPhoto[]{}), token);
    }

    private long getInstagramLocationId(final String venueID) {
        log.info(String.format("instagram request for getting location %s started", venueID));
        final InstagramAccessManager.PrepareRequest prepareRequest =
                new InstagramAccessManager.PrepareRequest() {
                    @Override
                    public byte[] getPayload() {
                        return venueID.getBytes();
                    }
                };
        final LocationSearchFeed locationSearchFeed =
                InstagramAccessManager.parallelFrontendInstagramRequest(Defs.Instagram.RequestType.GetLocation,
                        prepareRequest, LocationSearchFeed.class);
        if (InstagramUtils.isNullOrEmpty(locationSearchFeed)) {
            log.severe(String.format("venue: %s not found", venueID));
            return 0;
        }

        log.info("got result from instagram - location-id: " + locationSearchFeed.getLocationList().get(0).getId());
        return locationSearchFeed.getLocationList().get(0).getId(); // TODO: what if we get multiple locations?
    }

    private void filterPhotos(final List<RestogramPhoto> data, final RestogramFilterType filterType) {
        if (filterType != RestogramFilterType.None) {
            final RestogramFilter restogramFilter =
                    RestogramFilterFactory.createFilter(filterType);
            restogramFilter.doFilter(data);
        }
    }

    private void addPhotosToQueue(final List<RestogramPhoto> data, final String originVenueId) {
        final List<RestogramPhoto> photosToEnqueue = new ArrayList<>(data.size());
        final Map<String, RestogramPhoto> idToPhotoMapping = new HashMap<>(data.size());
        for (final RestogramPhoto currPhoto : data) {
            if (!m_dataManager.isPhotoPending(currPhoto.getInstagram_id())) {
                idToPhotoMapping.put(currPhoto.getInstagram_id(), currPhoto);
                photosToEnqueue.add(currPhoto);
            }
        }

        // enque task + set as pending
        if (!photosToEnqueue.isEmpty()) {
            m_dataManager.addPendingPhotos(idToPhotoMapping);
            m_tasksManager.enqueueFilterTask(originVenueId, photosToEnqueue);
        }
    }

    private List<RestogramPhoto> getUncachedPhotos(final List<RestogramPhoto> data) {
        final Map<String, Boolean> photoToRuleMapping =
                m_dataManager.getPhotoToRuleMapping(data.toArray(new RestogramPhoto[0]));

        if (photoToRuleMapping == null)
            return data;

        final List<RestogramPhoto> uncachedPhotos = new ArrayList<>();
        for (final RestogramPhoto currPhoto : data) {
            if (!photoToRuleMapping.containsKey(currPhoto.getInstagram_id()))
                uncachedPhotos.add(currPhoto);
        }
        return uncachedPhotos;
    }

    private RestogramPhotos fetchInstagramPhotos(final String venueId, final String token) {

        if (noMorePhotos(token))
            return null;

        Pagination pagination = null;

        if (isValidPaginationToken(token))
            pagination = new Gson().fromJson(token, Pagination.class);

        RestogramPhotos recentMediaByLocation;
        try {
            final Credentials credentials = credentialsFactory.createInstagramCredentials();
            log.info("instagram credentials type = " + credentials.getType());
            final Instagram instagram = new Instagram(credentials.getClientId());
            recentMediaByLocation = getRecentMedia(venueId, pagination, instagram);
        } catch (InstagramException e) {
            log.warning("first media search has failed, retry");

            try {
                final Credentials credentials = credentialsFactory.createInstagramCredentials();
                log.info("instagram credentials type = " + credentials.getType());
                final Instagram instagram = new Instagram(credentials.getClientId());
                recentMediaByLocation = getRecentMedia(venueId, pagination, instagram);
            } catch (InstagramException e2) {
                log.severe("second media search has failed");
                return null;
            }
        }
        return recentMediaByLocation;
    }

    private RestogramPhotos getRecentMedia(final String venueId, final Pagination pagination, final Instagram instagram) throws InstagramException {
        if (pagination != null)
            return ApisConverters.convertToRestogramPhotos(instagram.getRecentMediaNextPage(pagination), venueId);
        else {
            final long locationID = getInstagramLocationId(venueId);
            if (locationID == 0) {
                log.severe("cannot find location for venue: " + venueId);
                return null;
            }
            log.info(String.format("instagram request for getting media feed by location %d started", locationID));
            final InstagramAccessManager.PrepareRequest prepareRequest =
                    new InstagramAccessManager.PrepareRequest() {
                        @Override
                        public byte[] getPayload() {
                            return String.valueOf(locationID).getBytes();
                        }
                    };
            final RestogramPhotos restogramPhotos =
                    InstagramAccessManager.parallelFrontendInstagramRequest(Defs.Instagram.RequestType.GetMediaByLocation,
                            prepareRequest, RestogramPhotos.class);
            if (InstagramUtils.isNullOrEmpty(restogramPhotos)) {
                log.warning(String.format("media feed for location %d not found", locationID));
                return null;
            }
            //decode string to get the correct encoding
            restogramPhotos.decodeStrings();

            log.info("got result from instagram - mediafeed");

            setVenueId(restogramPhotos, venueId);

            return restogramPhotos;
        }
    }

    private void setVenueId(RestogramPhotos restogramPhotos, String venueId) {
        for (RestogramPhoto photo : restogramPhotos.getPhotos()) {
            photo.setOriginVenueId(venueId);
        }
    }

    private boolean isValidPaginationToken(final String token) {
        return !StringUtils.isBlank(token) &&
                !token.equals(CommonDefs.Tokens.FINISHED_FETCHING_FROM_CACHE) &&
                !m_dataManager.isValidCursor(token);
    }

    private static final Logger log = Logger.getLogger(RestogramServiceImpl.class.getName());
    private final DataManager m_dataManager =
            RestogramServer.getInstance().getDataManager();
    private final TasksManager m_tasksManager =
            RestogramServer.getInstance().getTasksManager();
    private ICredentialsFactory credentialsFactory;
}