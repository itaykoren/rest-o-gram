package rest.o.gram.application;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import rest.o.gram.activities.PersonalActivity;
import rest.o.gram.authentication.IAuthenticationProvider;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.data_history.IDataHistoryManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Itay
 * Date: 20/04/13
 */
public class RestogramApplication extends Application implements Application.ActivityLifecycleCallbacks {

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize activities map
        activities = new HashMap<>();

        // Initialize client
        RestogramClient.getInstance().initialize(getApplicationContext());

        // Register callbacks
        registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onTerminate() { // Not guaranteed to be called from android
        super.onTerminate();

        // Unregister callbacks
        unregisterActivityLifecycleCallbacks(this);

        // Dispose client
        RestogramClient.getInstance().dispose();

        // Clear activities map
        activities.clear();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        try {
            activities.put(activity.getLocalClassName(), activity);
        }
        catch(Exception e) {
            // TODO
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        // Empty
    }

    @Override
    public void onActivityResumed(Activity activity) {
        if(activity.getClass() == PersonalActivity.class) {
            IAuthenticationProvider provider = RestogramClient.getInstance().getAuthenticationProvider();
            if(!provider.isUserLoggedIn())
                activity.finish();
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        // Empty
    }

    @Override
    public void onActivityStopped(Activity activity) {
        // Empty
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        // Empty
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        try {
            activities.remove(activity.getLocalClassName());

            if(activities.size() == 0) {
                onTerminate();
            }
            else {
                // Flush data
                IDataHistoryManager dataHistoryManager = RestogramClient.getInstance().getDataHistoryManager();
                if(dataHistoryManager != null) {
                    dataHistoryManager.flush();
                }
            }
        }
        catch(Exception e) {
            // TODO
        }
    }

    private Map<String, Activity> activities;
}
