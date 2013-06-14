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
        public static final int RC_EXPLORE = 106;
        public static final int RC_MAP = 107;
    }

    /**
     * Transport constants
     */
    public static class Transport {
        public static final String BASE_HOST_NAME = "http://restogramapp.appspot.com";
    }

    /**
     * Facebook API constants
     */
    public static class FacebookAPI {
        public static final String GRAPH_BASE_URL = "https://graph.facebook.com/";
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
            TrackerTypeFluffy,
            TrackerTypeSimple,
            TrackerTypeGoogle
        }

        public static final TrackerType PRIMARY_TRACKER_TYPE = TrackerType.TrackerTypeGoogle;
        public static final TrackerType SECONDARY_TRACKER_TYPE = TrackerType.TrackerTypeFluffy;

        public static final double DEFAULT_NEARBY_RADIUS = 500;

        public static final boolean INTENSE_LOCATION_UPDATES = true;
        public static final int MAX_LOCATION_AGE = 20 * 60 * 1000; // 20 minutes
        public static final long LOCATION_UPDATE_INTERVAL = 5 * 60 * 1000; // 5 minutes
        public static final int TRACKING_TIMEOUT = 20 * 1000; // 20 seconds
    }

    /**
     * Commands constants
     */
    public static class Commands {
        public static final int MAX_EXECUTING_COMMANDS = 10;
        public static final long DEFAULT_SHORT_TIMEOUT = 7000; // 7 seconds
        public static final long DEFAULT_LONG_TIMEOUT = 15000; // 15 seconds
    }

    /**
     * Filtering constants
     */
    public static class Filtering {
        public static final boolean FACE_FILTERING_ENABLED = false;
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

        public static final boolean DATA_HISTORY_ENABLED = true;
        public static final boolean FORCE_FLUSH = true;
        public static final String DATA_VENUES_FILENAME = "venues.rog";
        public static final String DATA_PHOTOS_FILENAME = "photos.rog";

        public static final boolean CACHE_DATA_HISTORY_ENABLED = true;

        public static final boolean BITMAP_CACHE_ENABLED = true;
        public static final String BITMAP_CACHE_PREFIX = "/photos/";
    }

    /**
     * Flow constants
     */
    public static class Flow {
        public static final boolean WELCOME_SCREENS_ENABLED = true;
    }
}
