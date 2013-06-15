package rest.o.gram.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.widget.*;
import com.leanengine.LeanAccount;
import rest.o.gram.R;
import rest.o.gram.activities.visitors.IActivityVisitor;
import rest.o.gram.authentication.IAuthenticationProvider;
import rest.o.gram.cache.IRestogramCache;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.common.Defs;
import rest.o.gram.common.IRestogramListener;
import rest.o.gram.common.Utils;
import rest.o.gram.data_favorites.GetFavoritePhotosResult;
import rest.o.gram.data_favorites.GetFavoriteVenuesResult;
import rest.o.gram.data_favorites.IDataFavoritesManager;
import rest.o.gram.data_favorites.IDataFavoritesOperationsObserver;
import rest.o.gram.data_favorites.results.*;
import rest.o.gram.data_history.IDataHistoryManager;
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.entities.RestogramVenue;
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

        setContentView(R.layout.personal);

        initUser();
        initHistory();
        initFavorites();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // TODO
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // TODO
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
        profilePhotoUrl = result.getProfilePhotoUrl();
        RestogramClient.getInstance().downloadImage(profilePhotoUrl, profilePhotoUrl, profileImageView, true, null);
    }

    @Override
    public void onFinished(GetFavoritePhotosResult result) {
        isPhotosRequestPending = false;
        setFavoritePhotos(result.getElements());
    }

    @Override
    public void onFinished(AddPhotoToFavoritesResult result) {

    }

    @Override
    public void onFinished(RemovePhotoFromFavoritesResult result) {
        // Empty
    }

    @Override
    public void onFinished(GetFavoriteVenuesResult result) {
        isVenuesRequestPending = false;
        setFavoriteVenues(result.getElements());
    }

    @Override
    public void onFinished(AddVenueToFavoritesResult result) {
        // Empty
    }

    @Override
    public void onFinished(RemoveVenueFromFavoritesResult result) {
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

            toggle();
            viewSwitcher.showPrevious();
        }
    }

    public void onFavoritesClicked(View view) {
        ViewSwitcher viewSwitcher = (ViewSwitcher)findViewById(R.id.viewSwitcher);
        View favoritesView = findViewById(R.id.favoritesView);

        if(viewSwitcher.getCurrentView() != favoritesView) {
            if(!isPhotosRequestPending && !isVenuesRequestPending)
                updateFavorites();

            toggle();
            viewSwitcher.showNext();
        }
    }

    /**
     * Initializes user related data
     */
    private void initUser() {
        try {
            IAuthenticationProvider provider = RestogramClient.getInstance().getAuthenticationProvider();
            updateNickname((TextView)findViewById(R.id.tvFBName), provider);
            updateProfilePhoto((ImageView)findViewById(R.id.ivFBPhoto), provider);
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
        // Init favorite venue list view
        ListView lvFavVenues = (ListView)findViewById(R.id.lvFavVenues);
        favoriteVenueViewAdapter = new VenueViewAdapter(this, this);
        lvFavVenues.setAdapter(favoriteVenueViewAdapter);

        // Init favorite photo grid view
        GridView gvFavPhotos = (GridView)findViewById(R.id.gvFavPhotos);
        favoritePhotoViewAdapter = new PhotoViewAdapter(this);
        gvFavPhotos.setAdapter(favoritePhotoViewAdapter);

        updateFavorites();
    }

    /**
     * Sets favorite venues
     */
    private void setFavoriteVenues(List<RestogramVenue> venues) {
        if(venues == null || venues.size() == 0)
            return;

        for(RestogramVenue venue : venues) {
            favoriteVenueViewAdapter.addVenue(venue.getFoursquare_id());

            // Add venue to cache (if needed)
            IRestogramCache cache = RestogramClient.getInstance().getCache();
            cache.add(venue);
        }

        favoriteVenueViewAdapter.refresh();
    }

    /**
     * Sets favorite photos
     */
    private void setFavoritePhotos(List<RestogramPhoto> photos) {
        if(photos == null || photos.size() == 0)
            return;

        for(RestogramPhoto photo : photos) {
            // Add photo to cache (if needed)
            IRestogramCache cache = RestogramClient.getInstance().getCache();
            cache.add(photo);

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

        // Update favorite venues
        Set<String> venues = dataFavoritesManager.getFavoriteVenues();
        if(venues != null && !venues.isEmpty()) {
            favoriteVenueViewAdapter.clear();

            for(String id : venues) {
                favoriteVenueViewAdapter.addVenue(id);
            }

            favoriteVenueViewAdapter.refresh();
        }
        else {
            //dataFavoritesManager.getFavoriteVenues(this);
            //isVenuesRequestPending = true;
        }

        // Update favorite photos
        Set<String> photos = dataFavoritesManager.getFavoritePhotos();
        if(photos != null && !photos.isEmpty()) {
            favoritePhotoViewAdapter.clear();
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
            showMessage("No yummies yet");
            //dataFavoritesManager.getFavoritePhotos(this);
            //isPhotosRequestPending = true;
        }
    }

    private void updateNickname(final TextView textView, final IAuthenticationProvider provider) {

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                try {
                    Utils.updateTextView(textView, (String)message.obj);
                }
                catch(Exception e) {
                }
            }
        };

        Thread thread = new Thread() {
            @Override
            public void run() {

                LeanAccount account = provider.getAccountData();
                if (account != null) {
                    Message message = handler.obtainMessage(1, account.getNickName());
                    handler.sendMessage(message);
                }
            }
        };
        thread.start();
    }

    private void updateProfilePhoto(final ImageView imageView, final IAuthenticationProvider provider) {
        this.profileImageView = imageView;

        if(profilePhotoUrl != null) {
            onFinished(new GetProfilePhotoUrlResult() {
                @Override
                public String getProfilePhotoUrl() {
                    return profilePhotoUrl;
                }
            });
            return;
        }

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                try {
                    String facebookId = (String) message.obj;
                    getProfilePhotoUrl(facebookId);
                } catch (Exception e) {
                }
            }
        };

        Thread thread = new Thread() {
            @Override
            public void run() {

                LeanAccount account = provider.getAccountData();
                if (account != null) {
                    Message message = handler.obtainMessage(1, account.getProviderId());
                    handler.sendMessage(message);
                }
            }
        };
        thread.start();
    }

    private void getProfilePhotoUrl(String facebookId) {
        RestogramClient.getInstance().getProfilePhotoUrl(facebookId, this);
    }

    private void toggle() {
        View bHistory = findViewById(R.id.bHistory);
        View bFavorites = findViewById(R.id.bFavorites);
        if(bHistory != null && bFavorites != null) {
            // TODO: set icon/text/...
        }
    }

    private void showMessage(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    private VenueViewAdapter historyVenueViewAdapter; // History venue view adapter

    private VenueViewAdapter favoriteVenueViewAdapter; // Favorite venue view adapter
    private PhotoViewAdapter favoritePhotoViewAdapter; // Favorite photo View Adapter

    private ImageView profileImageView; // profile photo image view
    private String profilePhotoUrl = null; // Profile photo url

    private boolean isVenuesRequestPending = false; // Venues request pending flag
    private boolean isPhotosRequestPending = false; // Photos request pending flag
}
