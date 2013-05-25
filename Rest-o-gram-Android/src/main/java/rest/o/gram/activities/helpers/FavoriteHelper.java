package rest.o.gram.activities.helpers;

import android.widget.ImageButton;
import com.leanengine.LeanAccount;
import rest.o.gram.R;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.data.GetFavoritePhotosResult;
import rest.o.gram.data.GetFavoriteVenuesResult;
import rest.o.gram.data.IDataFavoritesManager;
import rest.o.gram.data.IDataFavoritesOperationsObserver;
import rest.o.gram.data.results.*;
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
        if(dataFavoritesManager != null) {
            dataFavoritesManager.getFavoriteVenues(this);
            dataFavoritesManager.getFavoritePhotos(this);
        }

        // TODO: handle pagination
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
    public boolean toggleFavoriteVenue(RestogramVenue venue) {
        if(!LeanAccount.isUserLoggedIn())
            return false;

        if(dataFavoritesManager == null)
            return false;

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
    public boolean toggleFavoritePhoto(RestogramPhoto photo) {
        if(!LeanAccount.isUserLoggedIn())
            return false;

        if(dataFavoritesManager == null)
            return false;

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
    public void onFinished(ClearFavoritePhotosResult result) {
        // Empty
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

    @Override
    public void onFinished(ClearFavoriteVenuesResult result) {
        // Empty
    }

    private IDataFavoritesManager dataFavoritesManager;

    private Map<String, RestogramVenue> favoriteVenues;
    private Map<String, RestogramPhoto> favoritePhotos;

    private String venueId;
    private String photoId;

    private ImageButton favoriteVenueButton;
    private ImageButton favoritePhotoButton;
}
