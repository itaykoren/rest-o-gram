package rest.o.gram.activities.helpers;

import android.widget.ImageButton;
import rest.o.gram.R;
import rest.o.gram.cache.IRestogramCache;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.data_favorites.GetFavoritePhotosResult;
import rest.o.gram.data_favorites.GetFavoriteVenuesResult;
import rest.o.gram.data_favorites.IDataFavoritesManager;
import rest.o.gram.data_favorites.IDataFavoritesOperationsObserver;
import rest.o.gram.data_favorites.results.*;
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.entities.RestogramVenue;
import rest.o.gram.tasks.ITaskObserver;
import rest.o.gram.tasks.results.*;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 5/25/13
 */
public class FavoriteHelper implements IDataFavoritesOperationsObserver, ITaskObserver {
    /**
     * Ctor
     */
    public FavoriteHelper() {
        dataFavoritesManager = RestogramClient.getInstance().getDataFavoritesManager();

        // TODO: handle pagination
    }

    /**
     * Refreshes this helper: updates favorite venues & photos
     */
    public void refresh() {
        if(dataFavoritesManager != null) {
            if(RestogramClient.getInstance().getAuthenticationProvider().isUserLoggedIn()) {
                // Get updated favorites
                //dataFavoritesManager.getFavoriteVenues(this);
                dataFavoritesManager.getFavoritePhotos(this);
            }
        }
    }

    /**
     * Sets venue id
     */
    public void setVenueId(String id) {
        venueId = id;
    }

    /**
     * Sets photo id
     */
    public void setPhotoId(String id) {
        photoId = id;
    }

    /**
     * Sets favorite venue button
     */
    public void setFavoriteVenueButton(ImageButton button) {
        favoriteVenueButton = button;
    }

    /**
     * Sets favorite photo button
     */
    public void setFavoritePhotoButton(ImageButton button) {
        favoritePhotoButton = button;
    }

    /**
     * Toggles favorite state of given venue according to current user
     */
    public boolean toggleFavoriteVenue(String venueId) {
        if(!RestogramClient.getInstance().getAuthenticationProvider().isUserLoggedIn())
            return false;

        if(dataFavoritesManager == null)
            return false;

        // Get venue from cache
        IRestogramCache cache = RestogramClient.getInstance().getCache();
        RestogramVenue venue = cache.findVenue(venueId);

        if(!dataFavoritesManager.getFavoriteVenues().contains(venue.getFoursquare_id())) {
            dataFavoritesManager.addFavoriteVenue(venue, this);
        }
        else {
            dataFavoritesManager.removeFavoriteVenue(venue, this);
        }
        return true;
    }

    /**
     * Toggles favorite state of given photo according to current user
     */
    public boolean toggleFavoritePhoto(String photoId) {
        if(!RestogramClient.getInstance().getAuthenticationProvider().isUserLoggedIn())
            return false;

        if(dataFavoritesManager == null)
            return false;

        // Get photo from cache
        IRestogramCache cache = RestogramClient.getInstance().getCache();
        RestogramPhoto photo = cache.findPhoto(photoId);

        if (!photo.is_favorite()) {
            RestogramClient.getInstance().addPhotoToFavorites(photo.getInstagram_id(),
                                                              photo.getOriginVenueId(), this);
        }
        else {
            RestogramClient.getInstance().removePhotoFromFavorites(photo.getInstagram_id(), this);
        }
        return true;
    }

    @Override
    public void onFinished(GetFavoritePhotosResult result) {
        if(result == null)
            return;

        if(result.getElements() == null || result.getElements().isEmpty())
            return;

        if(favoritePhotoButton != null && !photoId.isEmpty()) {
            if(dataFavoritesManager.getFavoritePhotos().contains(photoId))
                favoritePhotoButton.setImageResource(R.drawable.ic_favorite_on);
            else
                favoritePhotoButton.setImageResource(R.drawable.ic_favorite_off);
        }

        // TODO: handle pagination - if(result.hasMore())...
    }

    @Override
    public void onFinished(GetNearbyResult result) { }

    @Override
    public void onFinished(GetInfoResult result) { }

    @Override
    public void onFinished(GetPhotosResult result) { }

    @Override
    public void onFinished(CachePhotoResult result) { }

    @Override
    public void onFinished(FetchPhotosFromCacheResult result) { }

    @Override
    public void onFinished(CacheVenueResult result) { }

    @Override
    public void onFinished(FetchVenuesFromCacheResult result) { }

    @Override
    public void onFinished(GetProfilePhotoUrlResult result) { }

    @Override
    public void onFinished(AddPhotoToFavoritesResult result) {
        if(!result.hasSucceeded())
            return;

        dataFavoritesManager.addFavoritePhoto(result.getPhotoId());

        if(favoritePhotoButton != null)
            favoritePhotoButton.setImageResource(R.drawable.ic_favorite_on);
    }

    @Override
    public void onFinished(RemovePhotoFromFavoritesResult result) {
        if(!result.hasSucceeded())
            return;

        dataFavoritesManager.removeFavoritePhoto(result.getPhotoId());

        if(favoritePhotoButton != null)
            favoritePhotoButton.setImageResource(R.drawable.ic_favorite_off);
    }

    @Override
    public void onCanceled() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onFinished(GetFavoriteVenuesResult result) {
        if(result == null)
            return;

        if(result.getElements() == null || result.getElements().isEmpty())
            return;

        if(favoriteVenueButton != null && !venueId.isEmpty()) {
            if(dataFavoritesManager.getFavoriteVenues().contains(venueId))
                favoriteVenueButton.setImageResource(R.drawable.ic_favorite_on);
            else
                favoriteVenueButton.setImageResource(R.drawable.ic_favorite_off);
        }

        // TODO: handle pagination - if(result.hasMore())...
    }

    @Override
    public void onFinished(AddVenueToFavoritesResult result) {
        if(!result.hasSucceeded())
            return;

        if(favoriteVenueButton != null)
            favoriteVenueButton.setImageResource(R.drawable.ic_favorite_on);
    }

    @Override
    public void onFinished(RemoveVenueFromFavoritesResult result) {
        if(!result.hasSucceeded())
            return;

        if(favoriteVenueButton != null)
            favoriteVenueButton.setImageResource(R.drawable.ic_favorite_off);
    }

    private IDataFavoritesManager dataFavoritesManager;

    private String venueId;
    private String photoId;

    private ImageButton favoriteVenueButton;
    private ImageButton favoritePhotoButton;
}
