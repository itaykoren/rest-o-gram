package rest.o.gram.data_history;

import rest.o.gram.common.Defs;
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.entities.RestogramVenue;

import java.util.*;

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
        venues = new ArrayDeque<>();
        photos = new ArrayDeque<>();
    }

    @Override
    public boolean save(RestogramVenue venue, Defs.Data.SortOrder order) {
        try {
            if(venues.contains(venue)) {
                venues.remove(venue);
            }

            if(order == Defs.Data.SortOrder.SortOrderFIFO)
                venues.addLast(venue);
            else // if(order == Defs.Data.SortOrder.SortOrderLIFO)
                venues.addFirst(venue);
        }
        catch(Exception e) {
            return false;
        }

        return true;
    }

    @Override
    public boolean save(RestogramPhoto photo, Defs.Data.SortOrder order) {
        try {
            if(photos.contains(photo)) {
                photos.remove(photo);
            }

            if(order == Defs.Data.SortOrder.SortOrderFIFO)
                photos.addLast(photo);
            else // if(order == Defs.Data.SortOrder.SortOrderLIFO)
                photos.addFirst(photo);
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

    protected Deque<RestogramVenue> venues; // Venues list
    protected Deque<RestogramPhoto> photos; // Photos list
}
