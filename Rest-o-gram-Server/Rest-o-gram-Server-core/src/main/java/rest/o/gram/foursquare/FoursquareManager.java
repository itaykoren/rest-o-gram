package rest.o.gram.foursquare;

import rest.o.gram.entities.RestogramVenue;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 6/6/14
 */
public interface FoursquareManager {

    /**
     *  Gets a list of Foursqaure venus nearby the given location within the given radius,
     *  sorted by Foursquare's rank
     * @param latitude lat coordinate of location
     * @param longitude long coordinate of location
     * @param radius radius to use with respect to location
     * @return list of nearby venues sorted by Foursquare's rank
     */
    List<RestogramVenue> getNearby(double latitude, double longitude, double radius);

    /**
     * Gets extended info for a given Foursqaure venue
     * @param venueID venue id
     * @return venue extended info
     */
    RestogramVenue getInfo(String venueID);
}
