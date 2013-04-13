package com.tau;

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
     * @return array of media related to venue given its ID
     */
    RestogramPhoto[] getPhotos(String venueID);

    /**
     * @return array of media related to venue given its ID, after applying given filter
     */
    RestogramPhoto[] getPhotos(String venueID, RestogramPhotoFilter filter);
}
