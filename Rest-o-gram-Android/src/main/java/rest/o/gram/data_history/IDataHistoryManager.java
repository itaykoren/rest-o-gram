package rest.o.gram.data_history;

import rest.o.gram.common.Defs;
import rest.o.gram.entities.RestogramVenue;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 5/22/13
 */
public interface IDataHistoryManager {
    /**
     * Attempts to save given venue
     */
    boolean save(RestogramVenue venue, Defs.Data.SortOrder order);

    /**
     * Saves last location
     */
    void save(double latitude, double longitude);

    /**
     * Attempts to load venues
     */
    RestogramVenue[] loadVenues();

    /**
     * Loads last location
     */
    double[] loadLocation();

    /**
     * Clears all data
     */
    void clear();

    /**
     * Flushes all data
     */
    void flush();
}
