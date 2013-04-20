package rest.o.gram.common;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 17/04/13
 */
public class Defs {

    /**
     * Request codes enumeration
     */
    public static class RequestCodes {
        public static final int RC_HOME = 100;
        public static final int RC_FINDME = 101;
        public static final int RC_NEARBY = 102;
        public static final int RC_VENUE = 103;
        public static final int RC_PHOTO = 104;
    }

    /**
     * Location constants
     */
    public static class Location {
        public static final double DEFAULT_FINDME_RADIUS = 200;
        public static final double DEFAULT_NEARBY_RADIUS = 500;

        public static final boolean INTENSE_LOCATION_UPDATES = true;
        public static final int MAX_LOCATION_AGE = 20 * 60 * 1000; // 20 minutes
        public static final long LOCATION_UPDATE_INTERVAL = 5 * 60 * 1000; // 5 minutes
    }
}
