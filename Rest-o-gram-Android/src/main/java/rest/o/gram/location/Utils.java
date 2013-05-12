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
}
