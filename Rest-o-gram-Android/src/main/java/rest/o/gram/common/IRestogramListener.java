package rest.o.gram.common;

import rest.o.gram.entities.RestogramVenue;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 5/25/13
 */
public interface IRestogramListener {
    /**
     * Called after a venue was selected
     */
    void onVenueSelected(RestogramVenue venue);
}
