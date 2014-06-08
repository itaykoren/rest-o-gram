package rest.o.gram.instagram;

import org.jinstagram.Instagram;
import org.jinstagram.entity.locations.LocationSearchFeed;
import org.jinstagram.exceptions.InstagramException;
import rest.o.gram.ApisConverters;
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.service.InstagramServices.Entities.RestogramPhotos;

import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 6/8/14
 */
public class InstagramManagerImpl extends InstagramManagerBaseImpl {

    @Override
    public long searchFoursquareVenue(final String foursquareID) {
        log.info(String.format("searchFoursquareVenue : %s", foursquareID));
        LocationSearchFeed locationSearchFeed = null;
        try {
            final Instagram instagram = getInstagramCredentials();
            locationSearchFeed = instagram.searchFoursquareVenue(foursquareID);
        } catch (InstagramException e) {
            log.warning(String.format("first foursquare location search has failed, retry, error: %s", e.getMessage()));

            final Instagram instagram = getInstagramCredentials();
            try {
                locationSearchFeed = instagram.searchFoursquareVenue(foursquareID);
            } catch (InstagramException e2) {
                log.severe(String.format("second foursquare location search has failed, error: %s", e2.getMessage()));
                return -1;
            }
        }

        final int locationsNum = locationSearchFeed.getLocationList().size();
        if (locationsNum > 1)
            log.warning(String.format("got multiple instagram locations (%d)", locationsNum));

        final long instagramLocationID = locationSearchFeed.getLocationList().get(0).getId();
        log.info(String.format("got result from instagram - location-id: %d", instagramLocationID));
        return instagramLocationID; // TODO: what if we get multiple locations?
    }

    @Override
    public RestogramPhotos getRecentMedia(final long locationID) {
        log.info(String.format("getRecentMediaByLocation : %d", locationID));

        RestogramPhotos photos = null;
        try {
            final Instagram instagram = getInstagramCredentials();
            photos = ApisConverters.convertToRestogramPhotos(instagram.getRecentMediaByLocation(locationID));
        } catch (InstagramException e) {
            log.warning(String.format("first media search has failed, retry, error: %s", e.getMessage()));

            final Instagram instagram = getInstagramCredentials();
            try {
                photos = ApisConverters.convertToRestogramPhotos(instagram.getRecentMediaByLocation(locationID));
            } catch (InstagramException e2) {
                log.severe(String.format("second media search has failed, retry, error: %s", e.getMessage()));
                return null;
            }
        }
        return photos;
    }

    @Override
    public RestogramPhoto getPhoto(final String id) {
        log.info(String.format("getMediaInfo : %s", id));

        RestogramPhoto photo = null;
        try {
            final Instagram instagram = getInstagramCredentials();
            photo = ApisConverters.convertToRestogramPhoto(instagram.getMediaInfo(id));
        } catch (InstagramException e) {
            log.warning(String.format("first photo retrieval has failed, retry, error: %s", e.getMessage()));

            final Instagram instagram = getInstagramCredentials();
            try {
                photo = ApisConverters.convertToRestogramPhoto(instagram.getMediaInfo(id));
            } catch (InstagramException e1) {
                log.severe(String.format("second photo retrieval has failed, retry, error: %s", e.getMessage()));
                return null;
            }
        }

        return photo;
    }

    private static final Logger log = Logger.getLogger(InstagramManagerImpl.class.getName());
}