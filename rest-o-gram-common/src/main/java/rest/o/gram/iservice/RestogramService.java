package rest.o.gram.iservice;

import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.entities.RestogramVenue;
import rest.o.gram.filters.RestogramFilterType;
import rest.o.gram.results.PhotosResult;
import rest.o.gram.results.VenueResult;
import rest.o.gram.results.VenuesResult;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 31/03/13
 */
public interface RestogramService {

    /**
     * @return array of venus near given location
     */
    VenuesResult getNearby(double latitude, double longitude);

    /**
     * @return array of venus near given location within given radius
     */
    VenuesResult getNearby(double latitude, double longitude, double radius);

    /**
     * @return venue information according to its ID
     */
    VenueResult getInfo(String venueID);

    /**
     * @return array of media related to venue given its ID
     */
    PhotosResult getPhotos(String venueID);

    /**
     * @return array of media related to venue given its ID, after applying given filter
     */
    PhotosResult getPhotos(String venueID, RestogramFilterType filterType);

    /**
     * @param token identifying previous session for getting next photos
     * @param originVenueId the identifier of the venue from which the photo is taken
     * @return array of media related to venue given its ID
     */
    PhotosResult getNextPhotos(String token, String originVenueId);

    /**
     *
     * @param token identifying previous session for getting next photos
     * @param originVenueId the identifier of the venue from which the photo is taken
     * @return array of media related to venue given its ID, after applying given filter
     */
    PhotosResult getNextPhotos(String token, RestogramFilterType filterType, String originVenueId);
}
