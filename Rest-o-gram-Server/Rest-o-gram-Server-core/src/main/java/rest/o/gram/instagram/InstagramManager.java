package rest.o.gram.instagram;

import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.service.InstagramServices.Entities.RestogramPhotos;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 6/6/14
 */
public interface InstagramManager {
    /**
     * Searches Instagram for the given Foursquare venue
     * @param foursquareID Foursqaure venue ID
     * @return The found venue's Instagram ID or -1 if no venue was found
     */
    long searchFoursquareVenue(String foursquareID);

    /**
     * Gets recent media from Instagram according to the given location ID
     * @param locationID Instagram location ID to get recent media for
     * @return Recent media as Restogram photos (including token for later continuations)
     */
    RestogramPhotos getRecentMedia(long locationID);

    /**
     * Gets recent media from Instagram according to the given token
     * @param token Instagram pagiantion token that indicates the request offset
     * @param venueId foursquare id of the origin venue for the requested photos
     * @return Recent media as Restogram photos (including token for later continuations)
     */
    RestogramPhotos getRecentMedia(String token, String venueId);

    /**
     * Searches Instagram for the given photo
     * @param id Id of the photo to search for
     * @return The requested photo
     */
    RestogramPhoto getPhoto(String id);
}