package rest.o.gram.application;

import android.app.Application;
import com.littlefluffytoys.littlefluffylocationlibrary.*;
import rest.o.gram.BuildConfig;
import rest.o.gram.common.Defs;

/**
 * Created with IntelliJ IDEA.
 * User: or_2
 * Date: 4/19/13
 */
public class RestogramApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LocationLibrary.showDebugOutput(BuildConfig.DEBUG);

        if (Defs.Location.INTENSE_LOCATION_UPDATES)
            LocationLibrary.initialiseLibrary(getBaseContext(), true, "rest.o.gram");
        else // uses defined intervals
            LocationLibrary.initialiseLibrary(getBaseContext(), Defs.Location.LOCATION_UPDATE_INTERVAL,
                                              Defs.Location.MAX_LOCATION_AGE, "rest.o.gram");
    }
}
