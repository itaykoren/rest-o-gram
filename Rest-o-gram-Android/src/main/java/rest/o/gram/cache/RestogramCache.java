package rest.o.gram.cache;

import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.entities.RestogramVenue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 08/06/13
 */
public class RestogramCache implements IRestogramCache {
    /**
     * Ctor
     */
    public RestogramCache() {
        venues = new HashMap<>();
        photos = new HashMap<>();
    }

    @Override
    public boolean add(RestogramVenue venue) {
        if(findVenue(venue.getFoursquare_id()) != null)
            return false;

        venues.put(venue.getFoursquare_id(), venue);
        return true;
    }

    @Override
    public boolean add(RestogramPhoto photo) {
        if(findPhoto(photo.getInstagram_id()) != null)
            return false;

        photos.put(photo.getInstagram_id(), photo);
        return true;
    }

    @Override
    public boolean removeVenue(String id) {
        return venues.remove(id) != null;
    }

    @Override
    public boolean removePhoto(String id) {
        return photos.remove(id) != null;
    }

    @Override
    public RestogramVenue findVenue(String id) {
        return venues.get(id);
    }

    @Override
    public RestogramPhoto findPhoto(String id) {
        return photos.get(id);
    }

    @Override
    public void clear() {
        venues.clear();
        photos.clear();
    }

    private Map<String, RestogramVenue> venues; // Venues map
    private Map<String, RestogramPhoto> photos; // Photos map
}
