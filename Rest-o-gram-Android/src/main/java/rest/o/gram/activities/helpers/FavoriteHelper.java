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

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 5/25/13
 */
public class FavoriteHelper implements IDataFavoritesOperationsObserver {
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
                dataFavoritesManager.getFavoriteVenues(this);
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

        // Get venue from cache
        IRestogramCache cache = RestogramClient.getInstance().getCache();
        RestogramPhoto photo = cache.findPhoto(photoId);

        if(!dataFavoritesManager.getFavoritePhotos().contains(photo.getInstagram_id())) {
            dataFavoritesManager.addFavoritePhoto(photo, this);
        }
        else {
            dataFavoritesManager.removeFavoritePhoto(photo, this);
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
                favoritePhotoButton.setBackgroundResource(R.drawable.ic_favorite_on);
            else
                favoritePhotoButton.setBackgroundResource(R.drawable.ic_favorite_off);
        }

        // TODO: handle pagination - if(result.hasMore())...
    }

    @Override
    public void onFinished(AddFavoritePhotosResult result) {
        if(!result.hasSucceded())
            return;

        if(favoritePhotoButton != null)
            favoritePhotoButton.setBackgroundResource(R.drawable.ic_favorite_on);
    }

    @Override
    public void onFinished(RemoveFavoritePhotosResult result) {
        if(!result.hasSucceded())
            return;

        if(favoritePhotoButton != null)
            favoritePhotoButton.setBackgroundResource(R.drawable.ic_favorite_off);
    }

    @Override
    public void onFinished(GetFavoriteVenuesResult result) {
        if(result == null)
            return;

        if(result.getElements() == null || result.getElements().isEmpty())
            return;

        if(favoriteVenueButton != null && !venueId.isEmpty()) {
            if(dataFavoritesManager.getFavoriteVenues().contains(venueId))
                favoriteVenueButton.setBackgroundResource(R.drawable.ic_favorite_on);
            else
                favoriteVenueButton.setBackgroundResource(R.drawable.ic_favorite_off);
        }

        // TODO: handle pagination - if(result.hasMore())...
    }

    @Override
    public void onFinished(AddFavoriteVenuesResult result) {
        if(!result.hasSucceded())
            return;

        if(favoriteVenueButton != null)
            favoriteVenueButton.setBackgroundResource(R.drawable.ic_favorite_on);
    }

    @Override
    public void onFinished(RemoveFavoriteVenuesResult result) {
        if(!result.hasSucceded())
            return;

        if(favoriteVenueButton != null)
            favoriteVenueButton.setBackgroundResource(R.drawable.ic_favorite_off);
    }

    private IDataFavoritesManager dataFavoritesManager;

    private String venueId;
    private String photoId;

    private ImageButton favoriteVenueButton;
    private ImageButton favoritePhotoButton;
}
