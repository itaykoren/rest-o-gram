package rest.o.gram.common;

import android.util.Pair;
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.entities.RestogramVenue;

import java.util.Comparator;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 5/23/13
 */
public class Comparers {
    /**
     * Venue comparator
     */
    public static class VenueComparator implements Comparator<Pair<RestogramVenue, Date>> {
        /**
         * Ctor
         */
        public VenueComparator(Defs.Data.SortOrder sortOrder) {
            this.sortOrder = sortOrder;
        }

        @Override
        public int compare(Pair<RestogramVenue, Date> lhs, Pair<RestogramVenue, Date> rhs) {
            if(lhs == null || rhs == null)
                return 0;

            if(sortOrder == Defs.Data.SortOrder.SortOrderFIFO)
                return Utils.compare(lhs.second, rhs.second);
            else
                return -1 * Utils.compare(lhs.second, rhs.second);
        }

        private Defs.Data.SortOrder sortOrder;
    }

    /**
     * Photo comparator
     */
    public static class PhotoComparator implements Comparator<Pair<RestogramPhoto, Date>> {
        /**
         * Ctor
         */
        public PhotoComparator(Defs.Data.SortOrder sortOrder) {
            this.sortOrder = sortOrder;
        }

        @Override
        public int compare(Pair<RestogramPhoto, Date> lhs, Pair<RestogramPhoto, Date> rhs) {
            if(lhs == null || rhs == null)
                return 0;

            if(sortOrder == Defs.Data.SortOrder.SortOrderFIFO)
                return Utils.compare(lhs.second, rhs.second);
            else
                return -1 * Utils.compare(lhs.second, rhs.second);
        }

        private Defs.Data.SortOrder sortOrder;
    }
}
