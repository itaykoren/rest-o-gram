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
        // Init lists
        favoriteVenues = new HashMap<>();
        favoritePhotos = new HashMap<>();

        dataFavoritesManager = RestogramClient.getInstance().getDataFavoritesManager();

        // TODO: handle pagination
    }

    /**
     * Refreshes this helper: updates favorite venues & photos
     */
    public void refresh() {
        // Clear current favorites
        favoriteVenues.clear();
        favoritePhotos.clear();

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

        if(!favoriteVenues.containsKey(venue.getFoursquare_id())) {
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

        if(!favoritePhotos.containsKey(photo.getInstagram_id())) {
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

        try {
            for(RestogramPhoto photo : result.getElements()) {
                favoritePhotos.put(photo.getInstagram_id(), photo);
            }
        }
        catch(Exception e) {

        }

        if(favoritePhotoButton != null && !photoId.isEmpty()) {
            if(favoritePhotos.containsKey(photoId))
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

        final RestogramPhoto addedPhoto = result.getPhoto();
        favoritePhotos.put(addedPhoto.getInstagram_id(), addedPhoto);

        if(favoritePhotoButton != null)
            favoritePhotoButton.setBackgroundResource(R.drawable.ic_favorite_on);
    }

    @Override
    public void onFinished(RemoveFavoritePhotosResult result) {
        if(!result.hasSucceded())
            return;

        favoritePhotos.remove(result.getPhoto().getInstagram_id());

        if(favoritePhotoButton != null)
            favoritePhotoButton.setBackgroundResource(R.drawable.ic_favorite_off);
    }

    @Override
    public void onFinished(GetFavoriteVenuesResult result) {
        if(result == null)
            return;

        if(result.getElements() == null || result.getElements().isEmpty())
            return;

        try {
            for(RestogramVenue venue : result.getElements()) {
                favoriteVenues.put(venue.getFoursquare_id(), venue);
            }
        }
        catch(Exception e) {

        }

        if(favoriteVenueButton != null && !venueId.isEmpty()) {
            if(favoriteVenues.containsKey(venueId))
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

        final RestogramVenue addedVenue = result.getVenue();
        favoriteVenues.put(addedVenue.getFoursquare_id(), addedVenue);

        if(favoriteVenueButton != null)
            favoriteVenueButton.setBackgroundResource(R.drawable.ic_favorite_on);
    }

    @Override
    public void onFinished(RemoveFavoriteVenuesResult result) {
        if(!result.hasSucceded())
            return;

        favoriteVenues.remove(result.getVenue().getFoursquare_id());

        if(favoriteVenueButton != null)
            favoriteVenueButton.setBackgroundResource(R.drawable.ic_favorite_off);
    }

    private IDataFavoritesManager dataFavoritesManager;

    private Map<String, RestogramVenue> favoriteVenues;
    private Map<String, RestogramPhoto> favoritePhotos;

    private String venueId;
    private String photoId;

    private ImageButton favoriteVenueButton;
    private ImageButton favoritePhotoButton;
}
