package rest.o.gram.activities;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import rest.o.gram.activities.helpers.FavoriteHelper;
import rest.o.gram.activities.helpers.LoginHelper;
import rest.o.gram.cache.IRestogramCache;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.common.Defs;
import rest.o.gram.data_history.IDataHistoryManager;
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.entities.RestogramVenue;
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
        // Clear user data from objects in cache
        IRestogramCache cache = RestogramClient.getInstance().getCache();
        if(cache != null) {
            // Reset venue favorite data
            for(final RestogramVenue venue : cache.getVenues()) {
                venue.setfavorite(false);
            }

            // Reset photo favorite data
            for(final RestogramPhoto venue : cache.getPhotos()) {
                venue.set_favorite(false);
            }
        }
    }

    @Override
    public void onFinished(GetNearbyResult result) {
        if(result == null || result.getVenues() == null)
            return;

        IRestogramCache cache = RestogramClient.getInstance().getCache();
        if(cache != null) {
            // Add all venues to cache
            for(final RestogramVenue venue : result.getVenues()) {
                cache.add(venue);
            }
        }

        IDataHistoryManager cacheDataHistoryManager = RestogramClient.getInstance().getCacheDataHistoryManager();
        if(cacheDataHistoryManager != null) {
            // Reset cache data history
            cacheDataHistoryManager.clear();

            // Save to cache data history
            for(final RestogramVenue venue : result.getVenues()) {
                cacheDataHistoryManager.save(venue, Defs.Data.SortOrder.SortOrderFIFO);
            }
        }
    }

    @Override
    public void onFinished(GetInfoResult result) {
       if(result == null || result.getVenue() == null)
           return;

        IRestogramCache cache = RestogramClient.getInstance().getCache();
        if(cache != null) {
            // Reinsert venue to cache
            cache.removeVenue(result.getVenue().getFoursquare_id());
            cache.add(result.getVenue());
        }
    }

    @Override
    public void onFinished(GetPhotosResult result) {
        if(result == null || result.getPhotos() == null)
            return;

        IRestogramCache cache = RestogramClient.getInstance().getCache();
        if(cache != null) {
            // Add all photos to cache
            for(final RestogramPhoto photo : result.getPhotos()) {
                cache.add(photo);
            }
        }
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
    public void onFinished(GetProfilePhotoUrlResult result) {
        // Empty
    }

    @Override
    public void onCanceled() {
        // Empty
    }

    protected LoginHelper loginHelper; // Login helper
    protected FavoriteHelper favoriteHelper; // Favorite helper
}
