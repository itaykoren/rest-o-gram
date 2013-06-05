package rest.o.gram.activities;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import rest.o.gram.activities.helpers.FavoriteHelper;
import rest.o.gram.activities.helpers.LoginHelper;
import rest.o.gram.tasks.ITaskObserver;
import rest.o.gram.tasks.results.*;

/**
 * Created with IntelliJ IDEA.
 * User: Hen
 * Date: 24/05/13
 */
public class RestogramActivity extends FragmentActivity implements ITaskObserver {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize login helper
        loginHelper = new LoginHelper(this);

        // Initialize favorite helper
        favoriteHelper = new FavoriteHelper();
    }

    /**
     * Called after user has logged in
     */
    public void onUserLoggedIn() {
        favoriteHelper.refresh();
    }

    /**
     * Called after user has logged out
     */
    public void onUserLoggedOut() {
        // TODO
    }

    @Override
    public void onFinished(GetNearbyResult venues) {
        // Empty
    }

    @Override
    public void onFinished(GetInfoResult venue) {
        // Empty
    }

    @Override
    public void onFinished(GetPhotosResult result) {
        // Empty
    }

    @Override
    public void onFinished(CachePhotoResult result) {
        // Empty
    }

    @Override
    public void onFinished(FetchPhotosFromCacheResult result) {
        // Empty
    }

    @Override
    public void onFinished(CacheVenueResult result) {
        // Empty
    }

    @Override
    public void onFinished(FetchVenuesFromCacheResult result) {
        // Empty
    }

    @Override
    public void onCanceled() {
        // Empty
    }

    protected LoginHelper loginHelper; // Login helper
    protected FavoriteHelper favoriteHelper; // Favorite helper
}
