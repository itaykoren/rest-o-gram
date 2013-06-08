package rest.o.gram.cache;

import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.entities.RestogramVenue;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 08/06/13
 */
public interface IRestogramCache {
    /**
     * Attempts to add given venue to cache
     * Returns true if successful, false otherwise
     */
    boolean add(RestogramVenue venue);

    /**
     * Attempts to add given photo to cache
     * Returns true if successful, false otherwise
     */
    boolean add(RestogramPhoto photo);

    /**
     * Attempts to remove given venue from cache
     * Returns true if successful, false otherwise
     */
    boolean removeVenue(String id);

    /**
     * Attempts to remove given photo from cache
     * Returns true if successful, false otherwise
     */
    boolean removePhoto(String id);

    /**
     * Attempts to find venue according to its id
     * Returns venue if successful, null otherwise
     */
    RestogramVenue findVenue(String id);

    /**
     * Attempts to find photo according to its id
     * Returns photo if successful, null otherwise
     */
    RestogramPhoto findPhoto(String id);

    /**
     * Clears all data from cache
     */
    void clear();
}
