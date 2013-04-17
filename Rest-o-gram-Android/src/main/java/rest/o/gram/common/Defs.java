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
        public static int RC_HOME = 100;
        public static int RC_FINDME = 101;
        public static int RC_NEARBY = 102;
        public static int RC_VENUE = 103;
        public static int RC_PHOTO = 104;
    }

    /**
     * Location constants
     */
    public static class Location {
        public static double DEFAULT_FINDME_RADIUS = 20;
        public static double DEFAULT_NEARBY_RADIUS = 100;
    }
}
