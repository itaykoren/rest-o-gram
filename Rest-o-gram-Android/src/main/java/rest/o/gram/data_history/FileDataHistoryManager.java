package rest.o.gram.data_history;

import android.content.Context;
import rest.o.gram.common.Defs;
import rest.o.gram.common.Utils;
import rest.o.gram.data_structs.*;
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.entities.RestogramVenue;

import java.io.*;

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

        // Set is flushing flag to false
        isFlushing = false;

        // Load from files
        load();
    }

    @Override
    public boolean save(RestogramVenue venue, Defs.Data.SortOrder order) {
        if(!super.save(venue, order))
            return false;

        isUpToDate = false;

        if(Defs.Data.FORCE_FLUSH)
            flushOnThread();

        return true;
    }

    @Override
    public boolean save(RestogramPhoto photo, Defs.Data.SortOrder order) {
        if(!super.save(photo, order))
            return false;

        isUpToDate = false;

        if(Defs.Data.FORCE_FLUSH)
            flushOnThread();

        return true;
    }

    @Override
    public void clear() {
        super.clear();

        isUpToDate = false;

        if(Defs.Data.FORCE_FLUSH)
            flushOnThread();
    }

    @Override
    public void flush() {
        if(isUpToDate)
            return; // Up to date, no need to flush

        if(isFlushing)
            return; // Currently flushing

        // Set flag to true
        isFlushing = true;

        // Save data to files
        save();

        // Set flag to false
        isFlushing = false;
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
            venues = (IDictionary<String, RestogramVenue>)Utils.deserialize(new File(context.getFilesDir(), Defs.Data.DATA_VENUES_FILENAME));
            photos = (IDictionary<String, RestogramPhoto>)Utils.deserialize(new File(context.getFilesDir(), Defs.Data.DATA_PHOTOS_FILENAME));
        }
        catch(Exception e) {
            // TODO: report error
        }

        if(venues == null)
            venues = new Dictionary<>();

        if(photos == null)
            photos = new Dictionary<>();

        isUpToDate = true;
    }

    /**
     * Flushes all data in a new thread
     */
    private void flushOnThread() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                flush();
            }
        };
        thread.start();
    }

    private Context context; // Context
    private boolean isUpToDate; // Is up to date flag
    private boolean isFlushing; // Is flushing flag
}
