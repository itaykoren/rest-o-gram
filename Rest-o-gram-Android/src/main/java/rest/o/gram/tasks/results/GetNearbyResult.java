package rest.o.gram.tasks.results;

import rest.o.gram.entities.RestogramVenue;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 4/23/13
 */
public interface GetNearbyResult {

    /**
     * Returns venues
     */
    RestogramVenue[] getVenues();

    /**
     * Returns location latitude
     */
    double getLatitude();

    /**
     * Returns location longitude
     */
    double getLongitude();
}
