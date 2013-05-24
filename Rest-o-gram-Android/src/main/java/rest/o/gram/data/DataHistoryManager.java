package rest.o.gram.data;

import android.util.Pair;
import rest.o.gram.common.Comparers;
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
        // Init maps
        venues = new HashMap<>();
        photos = new HashMap<>();
    }

    @Override
    public boolean save(RestogramVenue venue) {
        try {
            if(venues.containsKey(venue.getFoursquare_id())) {
                venues.remove(venue.getFoursquare_id());
            }

            Date now = new Date();
            venues.put(venue.getFoursquare_id(), new Pair<>(venue, now));
        }
        catch(Exception e) {
            return false;
        }

        return true;
    }

    @Override
    public boolean save(RestogramPhoto photo) {
        try {
            if(photos.containsKey(photo.getInstagram_id())) {
                photos.remove(photo.getInstagram_id());
            }

            Date now = new Date();
            photos.put(photo.getInstagram_id(), new Pair<>(photo, now));
        }
        catch(Exception e) {
            return false;
        }

        return true;
    }

    @Override
    public RestogramVenue[] loadVenues(Defs.Data.SortOrder order) {
        RestogramVenue[] venues;
        try {
            if(this.venues.isEmpty())
                return null;

            // Create list of venues and sort it
            List<Pair<RestogramVenue, Date>> list = new LinkedList<>(this.venues.values());
            Collections.sort(list, new Comparers.VenueComparator(order));

            int i = 0;
            venues = new RestogramVenue[list.size()];
            for(Pair<RestogramVenue, Date> pair : list) {
                venues[i++] = pair.first;
            }
        }
        catch(Exception e) {
            return null;
        }

        return venues;
    }

    @Override
    public RestogramPhoto[] loadPhotos(Defs.Data.SortOrder order) {
        RestogramPhoto[] photos;
        try {
            if(this.photos.isEmpty())
                return null;

            // Create list of photos and sort it
            List<Pair<RestogramPhoto, Date>> list = new LinkedList<>(this.photos.values());
            Collections.sort(list, new Comparers.PhotoComparator(order));

            photos = new RestogramPhoto[list.size()];

            int i = 0;
            for(Pair<RestogramPhoto, Date> pair : list) {
                photos[i++] = pair.first;
            }
        }
        catch(Exception e) {
            return null;
        }

        return photos;
    }

    @Override
    public void clear() {
        venues.clear();
        photos.clear();
    }

    @Override
    public void flush() {
        // Empty
    }

    protected Map<String, Pair<RestogramVenue, Date>> venues; // Venues map
    protected Map<String, Pair<RestogramPhoto, Date>> photos; // Photos map


}
