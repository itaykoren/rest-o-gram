package rest.o.gram.service;

import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.entities.RestogramVenue;
import rest.o.gram.filters.RestogramFilterType;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 31/03/13
 */
public interface RestogramService {

    /**
     * @return array of venus near given location
     */
    RestogramVenue[] getNearby(double latitude, double longitude);

    /**
     * @return array of venus near given location within given radius
     */
    RestogramVenue[] getNearby(double latitude, double longitude, double radius);

    /**
     * @return venue information according to its ID
     */
    RestogramVenue getInfo(String venueID);

    /**
     * @return array of media related to venue given its ID
     */
    RestogramPhoto[] getPhotos(String venueID);

    /**
     * @return array of media related to venue given its ID, after applying given filter
     */
    RestogramPhoto[] getPhotos(String venueID, RestogramFilterType filterType);
}
