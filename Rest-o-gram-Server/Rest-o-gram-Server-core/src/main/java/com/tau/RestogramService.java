package com.tau;

import fi.foyt.foursquare.api.entities.CompactVenue;
import org.jinstagram.entity.users.feed.MediaFeedData;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 31/03/13
 */
public interface RestogramService {

    /**
     * @return array of venus near given location within given radius
     */
    CompactVenue[] getNearby(double latitude, double longitude, double radius);

    /**
     * @return array of media related to venue given its ID
     */
    MediaFeedData[] getPhotos(String venueID);
}
