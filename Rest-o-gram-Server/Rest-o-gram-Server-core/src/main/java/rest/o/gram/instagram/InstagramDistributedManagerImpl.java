package rest.o.gram.instagram;

import org.jinstagram.entity.locations.LocationSearchFeed;
import rest.o.gram.Defs;
import rest.o.gram.InstagramAccessManager;
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.service.InstagramServices.Entities.RestogramPhotos;
import rest.o.gram.utils.InstagramUtils;

import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 6/7/14
 */
public class InstagramDistributedManagerImpl extends InstagramManagerBaseImpl {

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
            log.severe(String.format("foursquare venue: %s not found", foursquareID));
            return -1;
        }

        final int locationsNum = locationSearchFeed.getLocationList().size();
        if (locationsNum > 1)
            log.warning(String.format("got %d instagram locations", locationsNum));

        log.info("got result from instagram - location-id: " + locationSearchFeed.getLocationList().get(0).getId());
        return locationSearchFeed.getLocationList().get(0).getId(); // TODO: what if we get multiple locations?
    }

    @Override
    public RestogramPhotos getRecentMedia(final long locationID) {
        log.info(String.format("instagram request for getting media feed by instagram location %d started", locationID));
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
            log.warning(String.format("media feed for instagram location %d not found", locationID));
            return null;
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

    private static final Logger log =
            Logger.getLogger(InstagramDistributedManagerImpl.class.getName());
}