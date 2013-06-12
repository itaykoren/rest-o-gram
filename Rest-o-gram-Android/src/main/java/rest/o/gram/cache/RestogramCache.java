package rest.o.gram.cache;

import rest.o.gram.data_structs.Dictionary;
import rest.o.gram.data_structs.IDictionary;
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.entities.RestogramVenue;

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
        venues = new Dictionary<>();
        photos = new Dictionary<>();
        venuePhotos = new Dictionary<>();
    }

    @Override
    public boolean add(RestogramVenue venue) {
        if(findVenue(venue.getFoursquare_id()) != null)
            return false;

        venues.putLast(venue.getFoursquare_id(), venue);
        return true;
    }

    @Override
    public boolean add(RestogramPhoto photo) {
        if(findPhoto(photo.getInstagram_id()) != null)
            return false;

        photos.putLast(photo.getInstagram_id(), photo);

        String venueId = photo.getOriginVenueId();
        RestogramPhotos photos = venuePhotos.find(venueId);
        if(photos == null) {
            photos = new RestogramPhotos();
            venuePhotos.putLast(venueId, photos);
        }
        photos.getPhotos().add(photo);

        return true;
    }

    @Override
    public boolean removeVenue(String id) {
        return venues.remove(id);
    }

    @Override
    public RestogramVenue findVenue(String id) {
        return venues.find(id);
    }

    @Override
    public RestogramPhoto findPhoto(String id) {
        return photos.find(id);
    }

    @Override
    public RestogramPhotos findPhotos(String venueId) {
        return venuePhotos.find(venueId);
    }

    @Override
    public void clear() {
        venues.clear();
        photos.clear();
        venuePhotos.clear();
    }

    private IDictionary<String, RestogramVenue> venues; // Venues dictionary
    private IDictionary<String, RestogramPhoto> photos; // Photos dictionary
    private IDictionary<String, RestogramPhotos> venuePhotos; // Venue photos dictionary
}
