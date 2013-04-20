package rest.o.gram.application;

import android.app.Application;
import rest.o.gram.client.RestogramClient;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 4/19/13
 */
public class RestogramApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize client
        RestogramClient.getInstance().initialize(getBaseContext());
    }
}