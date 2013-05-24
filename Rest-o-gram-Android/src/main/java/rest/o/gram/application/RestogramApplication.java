package rest.o.gram.application;

import android.app.Application;
import rest.o.gram.client.RestogramClient;

/**
 * Created with IntelliJ IDEA.
 * User: Itay
 * Date: 20/04/13
 */
public class RestogramApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize client
        RestogramClient.getInstance().initialize(getApplicationContext());
    }

    @Override
    public void onTerminate() { // Not guaranteed to be called
        super.onTerminate();

        // Dispose client
        RestogramClient.getInstance().dispose();
    }
}
