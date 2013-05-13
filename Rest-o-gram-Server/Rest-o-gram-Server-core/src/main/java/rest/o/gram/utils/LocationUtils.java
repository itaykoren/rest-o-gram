package rest.o.gram.utils;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 5/13/13
 */
public class LocationUtils {
    /**
     * Returns distance (in meters) between two given points
     */
    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        try {
            return (Math.acos(Math.sin(Math.toRadians(lat1)) *
                    Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) *
                    Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(lon1) - Math.toRadians(lon2)))
                    * EARTH_RADIUS_IN_METERS);

        }
        catch(Exception e) {
            return 0.0;
        }
    }

    private static double EARTH_RADIUS_IN_METERS = 6371 * 1000;
}
