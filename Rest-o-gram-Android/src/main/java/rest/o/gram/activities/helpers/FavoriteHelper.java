package rest.o.gram.activities.helpers;

import android.widget.ImageButton;
import android.widget.TextView;
import rest.o.gram.R;
import rest.o.gram.cache.IRestogramCache;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.common.Utils;
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
     * Sets photo id
     */
    public void setPhotoId(String id) {
        photoId = id;
    }

    /**
     * Sets favorite photo button
     */
    public void setFavoritePhotoButton(ImageButton button, TextView textView) {
        favoritePhotoButton = button;
        photoYummiesTextView = textView;
    }

    /**
     * Toggles favorite state of given photo according to current user
     */
    public boolean toggleFavoritePhoto(String photoId) {
        disableFavoritePhotoButton();
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

        // Handle pagination
        if(result.hasMore()) {
            dataFavoritesManager.getNextFavoritePhotos(result, this);
        }
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
        if (result == null ||!result.hasSucceeded()) {
            enableFavoritePhotoButton();
            return;
        }

        dataFavoritesManager.addFavoritePhoto(result.getPhotoId());

        if (favoritePhotoButton != null)
            favoritePhotoButton.setImageResource(R.drawable.ic_favorite_on);

        enableFavoritePhotoButton();

        updateYummiesCount(photoYummiesTextView, photoId);
    }

    @Override
    public void onFinished(RemovePhotoFromFavoritesResult result) {
        if (result == null || !result.hasSucceeded()) {
            enableFavoritePhotoButton();
            return;
        }

        dataFavoritesManager.removeFavoritePhoto(result.getPhotoId());

        if (favoritePhotoButton != null)
            favoritePhotoButton.setImageResource(R.drawable.ic_favorite_off);

        enableFavoritePhotoButton();

        updateYummiesCount(photoYummiesTextView, photoId);
    }

    @Override
    public void onCanceled() {
        // Empty
    }

    @Override
    public void onError() {
        // Empty
    }

    private void updateYummiesCount(TextView textView, String photoId) {
        IRestogramCache cache = RestogramClient.getInstance().getCache();
        RestogramPhoto photo = cache.findPhoto(photoId);
        if(photo == null)
            return;

        long yummies = photo.getYummies();
        if(yummies > 0)
            Utils.updateTextView(textView, String.format(String.format("%d yummies", yummies)));
        else
            Utils.updateTextView(textView, R.string.default_yummies_text);
    }

    private void setFavoritePhotoButton(boolean isEnabled) {
        if (favoritePhotoButton != null)
            favoritePhotoButton.setEnabled(isEnabled);
    }

    private void enableFavoritePhotoButton() {
        setFavoritePhotoButton(true);
    }

    private void disableFavoritePhotoButton() {
        setFavoritePhotoButton(false);
    }

    private IDataFavoritesManager dataFavoritesManager;

    private String photoId;

    private ImageButton favoritePhotoButton;
    private TextView photoYummiesTextView;
}
