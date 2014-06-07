package rest.o.gram.instagram;

import com.google.gson.Gson;
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
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.service.InstagramServices.Entities.RestogramPhotos;
import rest.o.gram.utils.InstagramUtils;

import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 6/7/14
 */
public class InstagramManagerImpl implements InstagramManager {

    public InstagramManagerImpl() {
        try {
            // TODO: when instagram login is implemented - prefer user credentials whenever possible
            credentialsFactory = new RandomCredentialsFactory();
        } catch (Exception e) {
            log.severe(String.format("an error occurred while initializing instagram manager %s : %s",
                                     e.toString(), e.getMessage()));
        }
    }

    @Override
    public long searchFoursquareVenue(final String foursquareID) {
        log.info(String.format("instagram request for getting location %s started", foursquareID));
        final InstagramAccessManager.PrepareRequest prepareRequest =
                new InstagramAccessManager.PrepareRequest() {
                    @Override
                    public byte[] getPayload() {
                        return foursquareID.getBytes();
                    }
                };
        final LocationSearchFeed locationSearchFeed =
                InstagramAccessManager.parallelFrontendInstagramRequest(Defs.Instagram.RequestType.GetLocation,
                        prepareRequest, LocationSearchFeed.class);
        if (InstagramUtils.isNullOrEmpty(locationSearchFeed)) {
            log.severe(String.format("venue: %s not found", foursquareID));
            return -1;
        }

        final int venuesNum = locationSearchFeed.getLocationList().size();
        if (venuesNum > 1)
            log.warning(String.format("got %d venues", venuesNum));

        log.info("got result from instagram - location-id: " + locationSearchFeed.getLocationList().get(0).getId());
        return locationSearchFeed.getLocationList().get(0).getId(); // TODO: what if we get multiple locations?
    }

    @Override
    public RestogramPhotos getRecentMedia(final long locationID) {
        log.info(String.format("instagram request for getting media feed by location %d started", locationID));
        final InstagramAccessManager.PrepareRequest prepareRequest =
                new InstagramAccessManager.PrepareRequest() {
                    @Override
                    public byte[] getPayload() {
                        return String.valueOf(locationID).getBytes();
                    }
                };
        final RestogramPhotos photos =
                InstagramAccessManager.parallelFrontendInstagramRequest(Defs.Instagram.RequestType.GetMediaByLocation,
                        prepareRequest, RestogramPhotos.class);
        if (InstagramUtils.isNullOrEmpty(photos)) {
            log.warning(String.format("media feed for location %d not found", locationID));
            return null;
        }
        return  photos;
    }

    @Override
    public RestogramPhotos getRecentMedia(final String token, final String venueId) {
        final Pagination pagination = new Gson().fromJson(token, Pagination.class);
        RestogramPhotos photos = null;

        try {
            final Credentials credentials = credentialsFactory.createInstagramCredentials();
            log.info("instagram credentials type = " + credentials.getType());
            final Instagram instagram = new Instagram(credentials.getClientId());
            photos = ApisConverters.convertToRestogramPhotos(instagram.getRecentMediaNextPage(pagination), venueId);
        } catch (InstagramException e) {
            log.warning("first media search has failed, retry");

            try {
                final Credentials credentials = credentialsFactory.createInstagramCredentials();
                log.info("instagram credentials type = " + credentials.getType());
                final Instagram instagram = new Instagram(credentials.getClientId());
                photos = ApisConverters.convertToRestogramPhotos(instagram.getRecentMediaNextPage(pagination), venueId);
            } catch (InstagramException e2) {
                log.severe("second media search has failed");
                return null;
            }
        }
        return photos;
    }

    @Override
    public RestogramPhoto getPhoto(final String id) {
        final InstagramAccessManager.PrepareRequest prepareRequest =
                new InstagramAccessManager.PrepareRequest() {
                    @Override
                    public byte[] getPayload() {
                        return id.getBytes();
                    }
                };
        final RestogramPhoto photo =
                InstagramAccessManager.parallelBackendInstagramRequest(Defs.Instagram.RequestType.GetPhoto,
                                                                       prepareRequest,
                                                                       RestogramPhoto.class);
        if (InstagramUtils.isNullOrEmpty(photo))
            return null;
        return photo;
    }

    private static final Logger log = Logger.getLogger(InstagramManagerImpl.class.getName());
    private ICredentialsFactory credentialsFactory;
}