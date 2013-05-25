package rest.o.gram.data_history;

import android.content.Context;
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
public class FileDataHistoryManager extends DataHistoryManager {

    /**
     * Ctor
     */
    public FileDataHistoryManager(Context context) {
        this.context = context;

        // Set is up to date flag to false
        isUpToDate = false;

        // Load from files
        load();
    }

    @Override
    public boolean save(RestogramVenue venue, Defs.Data.SortOrder order) {
        if(!super.save(venue, order))
            return false;

        isUpToDate = false;
        return true;
    }

    @Override
    public boolean save(RestogramPhoto photo, Defs.Data.SortOrder order) {
        if(!super.save(photo, order))
            return false;

        isUpToDate = false;
        return true;
    }

    @Override
    public void clear() {
        super.clear();

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
        try {
            Utils.serialize(venues, new File(context.getFilesDir(), Defs.Data.DATA_VENUES_FILENAME));
            Utils.serialize(photos, new File(context.getFilesDir(), Defs.Data.DATA_PHOTOS_FILENAME));
        }
        catch(Exception e) {
            // TODO: report error
        }

        isUpToDate = true;
    }

    /**
     * Loads data from files
     */
    private void load() {
        // Load data from files
        try {
            venues = (Deque<RestogramVenue>)Utils.deserialize(new File(context.getFilesDir(), Defs.Data.DATA_VENUES_FILENAME));
            photos = (Deque<RestogramPhoto>)Utils.deserialize(new File(context.getFilesDir(), Defs.Data.DATA_PHOTOS_FILENAME));
        }
        catch(Exception e) {
            // TODO: report error
        }

        if(venues == null)
            venues = new ArrayDeque<>();

        if(photos == null)
            photos = new ArrayDeque<>();

        isUpToDate = true;
    }

    private Context context; // Context
    private boolean isUpToDate; // Is up to date flag
}
