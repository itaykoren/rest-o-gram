package rest.o.gram.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ViewSwitcher;
import rest.o.gram.R;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.common.Defs;
import rest.o.gram.common.IRestogramListener;
import rest.o.gram.data.GetFavoritePhotosResult;
import rest.o.gram.data.GetFavoriteVenuesResult;
import rest.o.gram.data.IDataFavoritesManager;
import rest.o.gram.data.IDataFavoritesOperationsObserver;
import rest.o.gram.data.results.*;
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
public class PersonalActivity extends RestogramActivity implements IRestogramListener, IDataFavoritesOperationsObserver {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.personal);

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
    public void onVenueSelected(RestogramVenue venue) {
        // Switch to "VenueActivity" with parameter "venue"
        Intent intent = new Intent(this, VenueActivity.class);
        intent.putExtra("venue", venue);
        startActivityForResult(intent, Defs.RequestCodes.RC_VENUE);
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
    public void onFinished(ClearFavoritePhotosResult result) {
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

    @Override
    public void onFinished(ClearFavoriteVenuesResult result) {
        // Empty
    }

    public void onHistoryClicked(View view) {
        ViewSwitcher viewSwitcher = (ViewSwitcher)findViewById(R.id.viewSwitcher);
        viewSwitcher.showPrevious();
    }

    public void onFavoritesClicked(View view) {
        ViewSwitcher viewSwitcher = (ViewSwitcher)findViewById(R.id.viewSwitcher);
        viewSwitcher.showNext();
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
        favoritePhotoViewAdapter = new PhotoViewAdapter(this, Defs.Photos.THUMBNAIL_WIDTH, Defs.Photos.THUMBNAIL_HEIGHT);
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

    private VenueViewAdapter historyVenueViewAdapter; // History venue view adapter

    private VenueViewAdapter favoriteVenueViewAdapter; // Favorite venue view adapter
    private PhotoViewAdapter favoritePhotoViewAdapter; // Favorite photo View Adapter
}
