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
    public boolean save(RestogramVenue venue) {
        if(!super.save(venue))
            return false;

        isUpToDate = false;
        return true;
    }

    @Override
    public boolean save(RestogramPhoto photo) {
        if(!super.save(photo))
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
    private boolean isUpToDate; // Is up to date flag
}
