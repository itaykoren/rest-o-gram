package rest.o.gram.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import com.leanengine.LeanAccount;
import com.leanengine.LeanException;
import rest.o.gram.R;
import rest.o.gram.authentication.IAuthenticationProvider;
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
import rest.o.gram.view.PhotoViewAdapter;
import rest.o.gram.view.VenueViewAdapter;

import java.util.List;


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
        if(!super.onCreateOptionsMenu(menu))
            return false;

        try {
            menu.getItem(menu.size() - 1).setVisible(true); // Enable logout button
        }
        catch(Exception e) {
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
        intent.putExtra("venue", venue);
        Utils.changeActivity(this, intent, Defs.RequestCodes.RC_VENUE, false);
    }

    @Override
    public void onFinished(GetFavoritePhotosResult result) {
        setFavoritePhotos(result.getElements());
    }

    @Override
    public void onFinished(AddFavoritePhotosResult result) {
        // Empty
    }

    @Override
    public void onFinished(RemoveFavoritePhotosResult result) {
        // Empty
    }

    @Override
    public void onFinished(GetFavoriteVenuesResult result) {
        setFavoriteVenues(result.getElements());
    }

    @Override
    public void onFinished(AddFavoriteVenuesResult result) {
        // Empty
    }

    @Override
    public void onFinished(RemoveFavoriteVenuesResult result) {
        // Empty
    }

    public void onHistoryClicked(View view) {
        ViewSwitcher viewSwitcher = (ViewSwitcher)findViewById(R.id.viewSwitcher);
        View historyView = findViewById(R.id.historyView);

        if(viewSwitcher.getCurrentView() != historyView)
            viewSwitcher.showPrevious();
    }

    public void onFavoritesClicked(View view) {
        ViewSwitcher viewSwitcher = (ViewSwitcher)findViewById(R.id.viewSwitcher);
        View favoritesView = findViewById(R.id.favoritesView);

        if(viewSwitcher.getCurrentView() != favoritesView)
            viewSwitcher.showNext();
    }

    /**
     * Initializes user related data
     */
    private void initUser() {
        try {
            IAuthenticationProvider provider = RestogramClient.getInstance().getAuthenticationProvider();
            updateNickname((TextView)findViewById(R.id.tvFBName), provider);
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

        // Get data history manager
        IDataHistoryManager dataHistoryManager = RestogramClient.getInstance().getDataHistoryManager();
        if(dataHistoryManager == null)
            return;

        // Get saved venues
        RestogramVenue[] venues = dataHistoryManager.loadVenues();
        if(venues == null)
            return;

        for(RestogramVenue venue : venues) {
            historyVenueViewAdapter.addVenue(venue);
        }

        historyVenueViewAdapter.refresh();
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

        IDataFavoritesManager dataFavoritesManager = RestogramClient.getInstance().getDataFavoritesManager();
        if(dataFavoritesManager == null)
            return;

        dataFavoritesManager.getFavoriteVenues(this);
        dataFavoritesManager.getFavoritePhotos(this);
    }

    /**
     * Sets favorite venues
     */
    private void setFavoriteVenues(List<RestogramVenue> venues) {
        if(venues == null || venues.size() == 0)
            return;

        for(RestogramVenue venue : venues) {
            favoriteVenueViewAdapter.addVenue(venue);
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
            // Download image
            RestogramClient.getInstance().downloadImage(photo.getThumbnail(), photo, favoritePhotoViewAdapter, false, null);
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
                try {
                    LeanAccount account = provider.getAccountData();
                    Message message = handler.obtainMessage(1, account.getNickName());
                    handler.sendMessage(message);
                } catch (LeanException e) {
                    // TODO
                }
            }
        };
        thread.start();
    }

    private VenueViewAdapter historyVenueViewAdapter; // History venue view adapter

    private VenueViewAdapter favoriteVenueViewAdapter; // Favorite venue view adapter
    private PhotoViewAdapter favoritePhotoViewAdapter; // Favorite photo View Adapter
}
