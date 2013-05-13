package rest.o.gram.location;

import android.location.LocationManager;
import rest.o.gram.common.Defs;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/12/13
 */
public final class Utils {
    public static boolean isAccurate(int accuracy, String provider) {
        if (provider  == LocationManager.GPS_PROVIDER)
            return  true;
        else if (provider  == LocationManager.NETWORK_PROVIDER)
            return accuracy <= Defs.Location.DEFAULT_FINDME_RADIUS * Defs.Location.FINDME_UNCERTAINTY_FACTOR;

        return false;
    }

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
