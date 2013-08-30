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

        // Init location
        location = new double[2];
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
    public void save(double latitude, double longitude) {
        location[0] = latitude;
        location[1] = longitude;
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
    public double[] loadLocation() {
        return location;
    }

    @Override
    public void clear() {
        if(venues != null)
            venues.clear();
    }

    @Override
    public void flush() {
        // Empty
    }

    protected IDictionary<String, RestogramVenue> venues; // Venues dictionary
    protected double[] location; // Location
}
