package rest.o.gram.data_history;

import rest.o.gram.common.Defs;
import rest.o.gram.data_structs.*;
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.entities.RestogramVenue;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 24/05/13
 */
public class DataHistoryManager implements IDataHistoryManager {
    /**
     * Ctor
     */
    public DataHistoryManager() {
        // Init containers
        venues = new Dictionary<>();
        photos = new Dictionary<>();
    }

    @Override
    public boolean save(RestogramVenue venue, Defs.Data.SortOrder order) {
        try {
            if(venues.contains(venue.getFoursquare_id())) {
                venues.remove(venue.getFoursquare_id());
            }

            if(order == Defs.Data.SortOrder.SortOrderFIFO)
                venues.putLast(venue.getFoursquare_id(), venue);
            else // if(order == Defs.Data.SortOrder.SortOrderLIFO)
                venues.putFirst(venue.getFoursquare_id(), venue);
        }
        catch(Exception e) {
            return false;
        }

        return true;
    }

    @Override
    public boolean save(RestogramPhoto photo, Defs.Data.SortOrder order) {
        try {
            if(photos.contains(photo.getInstagram_id())) {
                photos.remove(photo.getInstagram_id());
            }

            if(order == Defs.Data.SortOrder.SortOrderFIFO)
                photos.putLast(photo.getInstagram_id(), photo);
            else // if(order == Defs.Data.SortOrder.SortOrderLIFO)
                photos.putFirst(photo.getInstagram_id(), photo);
        }
        catch(Exception e) {
            return false;
        }

        return true;
    }

    @Override
    public RestogramVenue[] loadVenues() {
        RestogramVenue[] venues;
        try {
            if(this.venues.isEmpty())
                return null;

            int i = 0;
            venues = new RestogramVenue[this.venues.size()];
            for(RestogramVenue venue : this.venues) {
                venues[i++] = venue;
            }
        }
        catch(Exception e) {
            return null;
        }

        return venues;
    }

    @Override
    public RestogramPhoto[] loadPhotos() {
        RestogramPhoto[] photos;
        try {
            if(this.photos.isEmpty())
                return null;

            int i = 0;
            photos = new RestogramPhoto[this.photos.size()];
            for(RestogramPhoto photo : this.photos) {
                photos[i++] = photo;
            }
        }
        catch(Exception e) {
            return null;
        }

        return photos;
    }

    @Override
    public void clear() {
        if(venues != null)
            venues.clear();

        if(photos != null)
            photos.clear();
    }

    @Override
    public void flush() {
        // Empty
    }

    protected IDictionary<String, RestogramVenue> venues; // Venues dictionary
    protected IDictionary<String, RestogramPhoto> photos; // Photos dictionary
}
