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
     * Attempts to find photos according to venue id
     * Returns photos if successful, null otherwise
     */
    RestogramPhotos findPhotos(String venueId);

    /**
     * Returns all venues
     */
    Iterable<RestogramVenue> getVenues();

    /**
     * Returns all photos
     */
    Iterable<RestogramPhoto> getPhotos();

    /**
     * Clears all data from cache
     */
    void clear();
}
