package rest.o.gram.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;
import rest.o.gram.R;
import rest.o.gram.activities.helpers.FavoriteHelper;
import rest.o.gram.activities.helpers.LoginHelper;
import rest.o.gram.activities.visitors.IActivityVisitor;
import rest.o.gram.authentication.IAuthenticationProvider;
import rest.o.gram.cache.IRestogramCache;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.common.Defs;
import rest.o.gram.data_favorites.results.AddPhotoToFavoritesResult;
import rest.o.gram.data_favorites.results.GetFavoritePhotosResult;
import rest.o.gram.data_favorites.results.RemovePhotoFromFavoritesResult;
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

        // Initialize dialog manager
        dialogManager = new DialogManager();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(dialogManager != null)
            dialogManager.clear();
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

            // Save last location to cache data history
            cacheDataHistoryManager.save(result.getLatitude(), result.getLongitude());
        }
    }

    @Override
    public void onFinished(GetInfoResult result) {
       if(result == null || result.getVenue() == null)
           return;

        IRestogramCache cache = RestogramClient.getInstance().getCache();
        if(cache != null) {
            //// Reinsert venue to cache
            //cache.removeVenue(result.getVenue().getFoursquare_id());
            //cache.add(result.getVenue());

            // Update image url if needed
            RestogramVenue newVenue = result.getVenue();
            RestogramVenue oldVenue = cache.findVenue(newVenue.getFoursquare_id());
            if(oldVenue == null)
                cache.add(newVenue);
            else if(newVenue.getImageUrl() != null && !newVenue.getImageUrl().isEmpty())
                oldVenue.setImageUrl(newVenue.getImageUrl());
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
    public void onFinished(AddPhotoToFavoritesResult result) {
        // Empty
    }

    @Override
    public void onFinished(RemovePhotoFromFavoritesResult result) {
        // Empty
    }

    @Override
    public void onFinished(GetFavoritePhotosResult result) {
       // Empty
    }

    @Override
    public void onFinished(GetCurrentAccountDataResult result) {
        if (result != null && result.getAccount() != null)
        {
            final IAuthenticationProvider provider =
                    RestogramClient.getInstance().getAuthenticationProvider();
            if (provider != null)
                provider.setAccountData(result.getAccount());
        }
    }

    @Override
    public void onFinished(LogoutResult result) {
        if (result != null && result.getSucceded())
        {
            Toast.makeText(this, R.string.logout_success, Toast.LENGTH_LONG).show();

            final IAuthenticationProvider provider =
                    RestogramClient.getInstance().getAuthenticationProvider();
            if (provider != null)
                provider.resetAuthData();

            onUserLoggedOut();
            if (RestogramClient.getInstance().isDebuggable())
                Log.d("REST-O-GRAM", "logout successful");
        }
        else
        {
            Log.e("REST-O-GRAM", "logout has failed");
            Toast.makeText(this, R.string.logout_fail, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCanceled() {
        // Empty
    }

    @Override
    public void onError() {
        if(dialogManager != null)
            dialogManager.showConnectionErrorAlert(this);
    }

    /**
     * Accepts given visitor
     */
    public void accept(IActivityVisitor visitor) {
        visitor.visit(this);
    }

    protected LoginHelper loginHelper; // Login helper
    protected FavoriteHelper favoriteHelper; // Favorite helper
    protected DialogManager dialogManager; // Dialog manager
}
