package rest.o.gram.data;

import rest.o.gram.common.Defs;
import rest.o.gram.entities.RestogramPhoto;
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
     * Attempts to save given photo
     */
    boolean save(RestogramPhoto photo, Defs.Data.SortOrder order);

    /**
     * Attempts to load venues
     */
    RestogramVenue[] loadVenues();

    /**
     * Attempts to load photos
     */
    RestogramPhoto[] loadPhotos();

    /**
     * Clears all data
     */
    void clear();

    /**
     * Flushes all data
     */
    void flush();
}
