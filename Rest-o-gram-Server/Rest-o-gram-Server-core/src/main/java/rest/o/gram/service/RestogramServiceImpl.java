package rest.o.gram.service;

// TODO: remove when Pagination dependencyis no longer neede gere
import com.google.gson.Gson;
import org.jinstagram.entity.common.Pagination;

import com.leanengine.server.auth.AuthService;
import org.apache.commons.lang3.StringUtils;
import rest.o.gram.Defs;
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
        return getNearby(latitude, longitude, -1);
    }

    /**
     * @return array of venues near given location within given radius
     */
    @Override
    public VenuesResult getNearby(final double latitude, final double longitude, final double radius) {
        final List<RestogramVenue> venues =
                RestogramServer.getInstance().getFoursquareManager().getNearby(latitude, longitude, radius);

        if (venues == null)
            return null;

        final String[] venueIds = new String[venues.size()];
        for (int i = 0; i < venueIds.length; i++)
            venueIds[i] = Long.toString(venues.get(i).getId());

        final Map<String, RestogramVenue> idToVenueMapping = m_dataManager.fetchVenuesFromCache(venueIds);

        if (idToVenueMapping != null) {
            for (final RestogramVenue currVenue : venues) {
                if (idToVenueMapping.containsKey(currVenue.getFoursquare_id()))
                    currVenue.setImageUrl(idToVenueMapping.get(currVenue.getFoursquare_id()).getImageUrl());
            }
        }

        return new VenuesResult(venues.toArray(new RestogramVenue[]{}));
    }

    /**
     * @return venue information according to its ID
     */
    @Override
    public VenueResult getInfo(final String venueID) {
        final RestogramVenue venue =
                RestogramServer.getInstance().getFoursquareManager().getInfo(venueID);

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

        final RestogramPhotos recentMediaByLocation = fetchInstagramPhotos(token, venueId);

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

        // TODO: remove the Pagination dependency
        log.info(String.format("got %d photos", data.size()));
        final Pagination pagination = recentMediaByLocation.getPagination();
        log.info("has more? " + (StringUtils.isNotBlank(pagination.getNextUrl()) ? "yes!" : "no!"));
        token = StringUtils.isNotBlank(pagination.getNextUrl()) ?
                new Gson().toJson(pagination) : CommonDefs.Tokens.FINISHED_FETCHING_FROM_INSTAGRAM;

        return new PhotosResult(data.toArray(new RestogramPhoto[]{}), token);
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

    private RestogramPhotos fetchInstagramPhotos(final String token, final String venueId) {

        if (noMorePhotos(token))
            return null;

        String pagination = null;

        if (isValidPaginationToken(token))
            pagination = token;

        return getRecentMedia(pagination, venueId);
    }

    private RestogramPhotos getRecentMedia(final String pagination, final String venueId) {
        if (pagination != null)
            return RestogramServer.getInstance().getInstagramManager().getRecentMedia(pagination, venueId);
        else {
            final long locationID =
                    RestogramServer.getInstance().getInstagramManager().searchFoursquareVenue(venueId);
            if (locationID == -1) {
                log.severe("cannot find location for venue: " + venueId);
                return null;
            }

            final RestogramPhotos photos =
                    RestogramServer.getInstance().getInstagramManager().getRecentMedia(locationID);

            if (photos == null)
                return null;

            // decode string to get the correct encoding
            photos.decodeStrings();

            log.info("got result from instagram - mediafeed");

            setVenueId(photos, venueId);

            return photos;
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