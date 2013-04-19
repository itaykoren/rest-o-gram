package rest.o.gram.location;

import android.app.AlarmManager;
import android.os.Build;

/**
 * Created with IntelliJ IDEA.
 * User: Itay
 * Date: 19/04/13
 */
public class LocationLibraryConstants {

    protected static final String TAG = "LittleFluffyLocationLibrary";

    public static final long DEFAULT_ALARM_FREQUENCY = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
    public static final int DEFAULT_MAXIMUM_LOCATION_AGE = (int) AlarmManager.INTERVAL_HOUR;

    protected static final String LOCATION_CHANGED_PERIODIC_BROADCAST_ACTION = ".littlefluffylocationlibrary.LOCATION_CHANGED";

    public static String getLocationChangedPeriodicBroadcastAction() {
        return LocationLibrary.broadcastPrefix + LOCATION_CHANGED_PERIODIC_BROADCAST_ACTION;
    }

    protected static final String LOCATION_CHANGED_TICKER_BROADCAST_ACTION = ".littlefluffylocationlibrary.LOCATION_CHANGED_TICK";

    public static String getLocationChangedTickerBroadcastAction() {
        return LocationLibrary.broadcastPrefix + LOCATION_CHANGED_TICKER_BROADCAST_ACTION;
    }

    public static final String LOCATION_BROADCAST_EXTRA_LOCATIONINFO = "com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo";

    protected static final int LOCATION_BROADCAST_REQUEST_CODE_SINGLE_SHOT = 1;
    protected static final int LOCATION_BROADCAST_REQUEST_CODE_REPEATING_ALARM = 2;
    protected static final String INTENT_CATEGORY_ONE_SHOT_UPDATE = "INTENT_CATEGORY_ONE_SHOT_UPDATE";

    protected static final String SP_KEY_LAST_LOCATION_UPDATE_TIME = "LFT_SP_KEY_LAST_LOCATION_UPDATE_TIME";
    protected static final String SP_KEY_LAST_LOCATION_UPDATE_LAT = "LFT_SP_KEY_LAST_LOCATION_UPDATE_LAT";
    protected static final String SP_KEY_LAST_LOCATION_UPDATE_LNG = "LFT_SP_KEY_LAST_LOCATION_UPDATE_LNG";
    protected static final String SP_KEY_LAST_LOCATION_UPDATE_ACCURACY = "LFT_SP_KEY_LAST_LOCATION_UPDATE_ACCURACY";
    protected static final String SP_KEY_LAST_LOCATION_BROADCAST_TIME = "LFT_SP_KEY_LAST_LOCATION_SUBMIT_TIME";
    protected static final String SP_KEY_RUN_ONCE = "LFT_SP_KEY_RUN_ONCE";

    protected static boolean SUPPORTS_FROYO = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.FROYO;
    protected static boolean SUPPORTS_GINGERBREAD = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD;
    protected static boolean SUPPORTS_JELLYBEAN = android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
}


