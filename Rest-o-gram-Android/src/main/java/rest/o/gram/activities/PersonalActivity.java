package rest.o.gram.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.*;
import rest.o.gram.R;
import rest.o.gram.activities.visitors.IActivityVisitor;
import rest.o.gram.authentication.IAuthenticationProvider;
import rest.o.gram.cache.IRestogramCache;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.common.Defs;
import rest.o.gram.common.IRestogramListener;
import rest.o.gram.common.Utils;
import rest.o.gram.data_favorites.IDataFavoritesManager;
import rest.o.gram.data_favorites.IDataFavoritesOperationsObserver;
import rest.o.gram.data_favorites.results.AddPhotoToFavoritesResult;
import rest.o.gram.data_favorites.results.GetFavoritePhotosResult;
import rest.o.gram.data_favorites.results.RemovePhotoFromFavoritesResult;
import rest.o.gram.data_history.IDataHistoryManager;
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.entities.RestogramVenue;
import rest.o.gram.tasks.results.GetCurrentAccountDataResult;
import rest.o.gram.tasks.results.GetProfilePhotoUrlResult;
import rest.o.gram.view.PhotoViewAdapter;
import rest.o.gram.view.VenueViewAdapter;

import java.util.List;
import java.util.Set;


/**
 * Created with IntelliJ IDEA.
 * User: Hen
 * Date: 24/05/13
 */
public class PersonalActivity extends RestogramActionBarActivity implements IRestogramListener, IDataFavoritesOperationsObserver {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Restart application (if needed)
        if(Utils.restartIfNeeded(this))
            return;

        setContentView(R.layout.personal);

        initUser();
        initHistory();
        initFavorites();

        isInitialized = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(historyVenueViewAdapter != null)
            historyVenueViewAdapter.clear();

        if(favoritePhotoViewAdapter != null)
            favoritePhotoViewAdapter.clear();

        isInitialized = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!super.onCreateOptionsMenu(menu))
            return false;

        try {
            menu.getItem(menu.size() - 1).setVisible(true); // Enable logout button
        } catch (Exception e) {
            // Empty
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        if(RestogramClient.getInstance().getApplication().isInLastActivity())
            dialogManager.showExitAlert(this);
        else
            finish();
    }

    @Override
    public void onUserLoggedOut() {
        super.onUserLoggedOut();

        // Switch to "ExploreActivity" with no parameters
        Intent intent = new Intent(this, ExploreActivity.class);
        Utils.changeActivity(this, intent, Defs.RequestCodes.RC_EXPLORE, true);
    }

    @Override
    public void onVenueSelected(RestogramVenue venue) {
        // Switch to "VenueActivity" with parameter "venue"
        Intent intent = new Intent(this, VenueActivity.class);
        intent.putExtra("venue", venue.getFoursquare_id());
        Utils.changeActivity(this, intent, Defs.RequestCodes.RC_VENUE, false);
    }

    @Override
    public void onFinished(GetProfilePhotoUrlResult result) {
        if (result != null && result.getProfilePhotoUrl() != null)
            doDownloadProfilePhoto(result.getProfilePhotoUrl());
    }

    private void doDownloadProfilePhoto(String profilePhotoUrl) {
        RestogramClient.getInstance().downloadImage(profilePhotoUrl, profilePhotoUrl, profileImageView, true, null);
    }

    @Override
    public void onFinished(GetFavoritePhotosResult result) {
        isPhotosRequestPending = false;
        setFavoritePhotos(result.getPhotos());
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
    public void accept(IActivityVisitor visitor) {
        visitor.visit(this);
    }

    public void onHistoryClicked(View view) {
        ViewSwitcher viewSwitcher = (ViewSwitcher)findViewById(R.id.viewSwitcher);
        View historyView = findViewById(R.id.historyView);

        if(viewSwitcher.getCurrentView() != historyView) {
            updateHistory();

            Button bHistory = (Button)findViewById(R.id.bHistory);
            Button bFavorites = (Button)findViewById(R.id.bFavorites);
            if(bHistory != null && bFavorites != null) {
                bHistory.setBackgroundResource(R.drawable.custom_button_on);
                bFavorites.setBackgroundResource(R.drawable.custom_button);
            }

            viewSwitcher.showPrevious();
        }
    }

    public void onFavoritesClicked(View view) {
        ViewSwitcher viewSwitcher = (ViewSwitcher)findViewById(R.id.viewSwitcher);
        View favoritesView = findViewById(R.id.favoritesView);

        if(viewSwitcher.getCurrentView() != favoritesView) {
            if(!isPhotosRequestPending)
                updateFavorites();

            Button bHistory = (Button)findViewById(R.id.bHistory);
            Button bFavorites = (Button)findViewById(R.id.bFavorites);
            if(bHistory != null && bFavorites != null) {
                bHistory.setBackgroundResource(R.drawable.custom_button);
                bFavorites.setBackgroundResource(R.drawable.custom_button_on);
            }

            viewSwitcher.showNext();
        }
    }

    /**
     * Initializes user related data
     */
    private void initUser() {
        try {
            updateNicknameAsync();
            profileImageView = (ImageView)findViewById(R.id.ivFBPhoto);
            final IAuthenticationProvider authProvider =
                    RestogramClient.getInstance().getAuthenticationProvider();
            if (authProvider != null && authProvider.getFacebookProfilePhotoUrl() != null &&
                    !authProvider.getFacebookProfilePhotoUrl().isEmpty())
                doDownloadProfilePhoto(authProvider.getFacebookProfilePhotoUrl());
        } catch (Exception e) {
            // TODO
        }
    }

    /**
     * Initializes history related data
     */
    private void initHistory() {
        // Init history venue list view
        ListView lv = (ListView)findViewById(R.id.lvHistory);
        historyVenueViewAdapter = new VenueViewAdapter(this, this);
        historyVenueViewAdapter.showDistance(false);
        lv.setAdapter(historyVenueViewAdapter);

        updateHistory();
    }

    /**
     * Initializes favorites related data
     */
    private void initFavorites() {
        // Init favorite photo grid view
        GridView gvFavPhotos = (GridView)findViewById(R.id.gvFavPhotos);
        favoritePhotoViewAdapter = new PhotoViewAdapter(this);
        gvFavPhotos.setAdapter(favoritePhotoViewAdapter);

        updateFavorites();
    }

    /**
     * Sets favorite photos
     */
    private void setFavoritePhotos(List<RestogramPhoto> photos) {
        if(photos == null || photos.size() == 0)
            return;

        IRestogramCache cache = RestogramClient.getInstance().getCache();
        IDataFavoritesManager favoritesManager = RestogramClient.getInstance().getDataFavoritesManager();

        for(RestogramPhoto photo : photos) {
            // Add photo to cache (if needed)
            if (cache != null)
                cache.add(photo);

            // Add photo id to the photo ids set in favorites manager (if needed)
            if (favoritesManager != null)
                favoritesManager.updateFavoritePhotos(photo.getInstagram_id());

            // Download image
            RestogramClient.getInstance().downloadImage(photo.getThumbnail(), photo, favoritePhotoViewAdapter, false, null);
        }
    }

    /**
     * Updates history
     */
    private void updateHistory() {
        // Get data history manager
        IDataHistoryManager dataHistoryManager = RestogramClient.getInstance().getDataHistoryManager();
        if(dataHistoryManager == null)
            return;

        // Get saved venues
        RestogramVenue[] venues = dataHistoryManager.loadVenues();
        if(venues == null || venues.length == 0) {
            showMessage("No restaurant history yet");
            return;
        }

        historyVenueViewAdapter.clear();
        for(RestogramVenue venue : venues) {
            // Add venue to cache (if needed)
            IRestogramCache cache = RestogramClient.getInstance().getCache();
            cache.add(venue);

            historyVenueViewAdapter.addVenue(venue.getFoursquare_id());
        }

        historyVenueViewAdapter.refresh();
    }

    /**
     * Updates favorites
     */
    private void updateFavorites() {
        // Get data favorites manager
        IDataFavoritesManager dataFavoritesManager = RestogramClient.getInstance().getDataFavoritesManager();
        if(dataFavoritesManager == null)
            return;

        // Update favorite photos
        Set<String> photos = dataFavoritesManager.getFavoritePhotos();
        favoritePhotoViewAdapter.clear();
        if(photos != null && !photos.isEmpty()) {
            for(String id : photos) {
                // Get photo from cache
                IRestogramCache cache = RestogramClient.getInstance().getCache();
                RestogramPhoto photo = cache.findPhoto(id);
                if(photo == null)
                    continue;

                // Download image
                RestogramClient.getInstance().downloadImage(photo.getThumbnail(), photo, favoritePhotoViewAdapter, false, null);
            }
        }
        else {
            if(isInitialized)
                showMessage("No yummies yet");
        }
    }

    private void updateNicknameAsync() {
        RestogramClient.getInstance().getCurrentAccount(this);
    }

    @Override
    public void onFinished(GetCurrentAccountDataResult result) {
        super.onFinished(result);

       if (result != null && result.getAccount() != null)
       {
           doUpdateNickname();
           RestogramClient.getInstance().getProfilePhotoUrl(result.getAccount().getProviderId(), this);
       }
    }

    private void doUpdateNickname() {
        final IAuthenticationProvider authProvider = RestogramClient.getInstance().getAuthenticationProvider();
        if (authProvider != null && authProvider.getAccountData() != null)
        {
            final TextView nickname = (TextView)findViewById(R.id.tvFBName);
            Utils.updateTextView(nickname, authProvider.getAccountData().getNickName());
        }
    }

    private void showMessage(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    private VenueViewAdapter historyVenueViewAdapter; // History venue view adapter

    private PhotoViewAdapter favoritePhotoViewAdapter; // Favorite photo view Adapter

    private ImageView profileImageView; // profile photo image view
    private String profilePhotoUrl = null; // Profile photo url

    private boolean isPhotosRequestPending = false; // Photos request pending flag

    private boolean isInitialized = false; // Is initialized flag
}
