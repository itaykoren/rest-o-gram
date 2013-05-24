package rest.o.gram.common;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 17/04/13
 */
public final class Defs {

    /**
     * Request codes enumeration
     */
    public static class RequestCodes {
        public static final int RC_HOME = 100;
        public static final int RC_FINDME = 101;
        public static final int RC_NEARBY = 102;
        public static final int RC_VENUE = 103;
        public static final int RC_PHOTO = 104;
        public static final int RC_PERSONAL = 105;
    }

    /**
     * Location constants
     */
    public static class Location {
        /**
         * Tracker type enumeration
         */
        public enum TrackerType {
            TrackerTypeDummy,
            TrackerTypeDefault
        }

        public static final TrackerType TRACKER_TYPE = TrackerType.TrackerTypeDefault;

        public static final double DEFAULT_FINDME_RADIUS = 50;
        public static final double DEFAULT_NEARBY_RADIUS = 500;

        public static final boolean INTENSE_LOCATION_UPDATES = true;
        public static final int MAX_LOCATION_AGE = 20 * 60 * 1000; // 20 minutes
        public static final long LOCATION_UPDATE_INTERVAL = 5 * 60 * 1000; // 5 minutes
        public static final int TRACKING_TIMEOUT = 20 * 1000; // 20 seconds

        public static final double FINDME_UNCERTAINTY_FACTOR = 1.5;
    }

    /**
     * Commands constants
     */
    public static class Commands {
        public static final int MAX_EXECUTING_COMMANDS = 10;
    }

    /**
     * Photos constants
     */
    public static class Photos {
        public static final int THUMBNAIL_WIDTH = 120;
        public static final int THUMBNAIL_HEIGHT = 120;
    }

    /**
     * Filtering constants
     */
    public static class Filtering {
        public static final boolean FACE_FILTERING_ENABLED = true;
        public static final int MAX_FACES_TO_DETECT = 5;
    }

    /**
     * Data constants
     */
    public static class Data {
        /**
         * Sort order enumeration
         */
        public enum SortOrder {
            SortOrderFIFO,
            SortOrderLIFO
        }

        public static final boolean DATA_HISTORY_ENABLED = false;
        public static final String DATA_VENUES_FILENAME = "venues.rog";
        public static final String DATA_PHOTOS_FILENAME = "photos.rog";
        public static final boolean CACHE_DATA_HISTORY_ENABLED = true;
    }

    public static final int VENUES_AMBIGOUITY_LEVEL = 2;
}
