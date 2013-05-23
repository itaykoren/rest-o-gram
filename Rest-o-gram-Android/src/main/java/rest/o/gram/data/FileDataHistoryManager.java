package rest.o.gram.data;

import android.content.Context;
import android.util.Pair;
import rest.o.gram.common.Comparers;
import rest.o.gram.common.Defs;
import rest.o.gram.common.Utils;
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.entities.RestogramVenue;

import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 5/22/13
 */
public class FileDataHistoryManager implements IDataHistoryManager {

    /**
     * Ctor
     */
    public FileDataHistoryManager(Context context) {
        this.context = context;

        // Init maps
        venues = new HashMap<>();
        photos = new HashMap<>();

        // Init comparators
        venueComparator = new Comparers.VenueComparator();
        photoComparator = new Comparers.PhotoComparator();

        // Set is up to date flag to false
        isUpToDate = false;

        // Load from files
        load();
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

        isUpToDate = false;
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

        isUpToDate = false;
        return true;
    }

    @Override
    public RestogramVenue[] loadVenues() {
        RestogramVenue[] venues;
        try {
            if(this.venues.isEmpty())
                return null;

            // Create list of venues and sort it
            List<Pair<RestogramVenue, Date>> list = new LinkedList<>(this.venues.values());
            Collections.sort(list, venueComparator);

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
    public RestogramPhoto[] loadPhotos() {
        RestogramPhoto[] photos;
        try {
            if(this.photos.isEmpty())
                return null;

            // Create list of photos and sort it
            List<Pair<RestogramPhoto, Date>> list = new LinkedList<>(this.photos.values());
            Collections.sort(list, photoComparator);

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

        isUpToDate = false;
    }

    @Override
    public void flush() {
        if(isUpToDate)
            return; // Up to date, no need to flush

        // Save data to files
        save();
    }

    /**
     * Saves data to files
     */
    private void save() {
        // Save data to files
        Utils.serialize(venues, new File(context.getFilesDir(), Defs.Data.DATA_VENUES_FILENAME));
        Utils.serialize(photos, new File(context.getFilesDir(), Defs.Data.DATA_PHOTOS_FILENAME));

        isUpToDate = true;
    }

    /**
     * Loads data from files
     */
    private void load() {
        // Load data from files
        try {
            venues = (Map<String, Pair<RestogramVenue, Date>>)Utils.deserialize(new File(context.getFilesDir(), Defs.Data.DATA_VENUES_FILENAME));
            photos = (Map<String, Pair<RestogramPhoto, Date>>)Utils.deserialize(new File(context.getFilesDir(), Defs.Data.DATA_PHOTOS_FILENAME));
        }
        catch(Exception e) {
            // TODO: report error
        }

        isUpToDate = true;
    }

    private Context context; // Context

    private Map<String, Pair<RestogramVenue, Date>> venues; // Venues map
    private Map<String, Pair<RestogramPhoto, Date>> photos; // Photos map

    private Comparers.VenueComparator venueComparator; // Venue comparator
    private Comparers.PhotoComparator photoComparator; // Photo comparator

    private boolean isUpToDate; // Is up to date flag
}
