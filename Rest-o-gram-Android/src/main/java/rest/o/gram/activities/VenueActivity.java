package rest.o.gram.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import rest.o.gram.R;
import rest.o.gram.activities.visitors.IActivityVisitor;
import rest.o.gram.cache.IRestogramCache;
import rest.o.gram.cache.RestogramPhotos;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.commands.IRestogramCommand;
import rest.o.gram.common.Defs;
import rest.o.gram.common.Utils;
import rest.o.gram.data_history.IDataHistoryManager;
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.entities.RestogramVenue;
import rest.o.gram.filters.RestogramFilterType;
import rest.o.gram.shared.CommonDefs;
import rest.o.gram.tasks.results.GetInfoResult;
import rest.o.gram.tasks.results.GetPhotosResult;
import rest.o.gram.view.PhotoViewAdapter;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 16/04/13
 */
public class VenueActivity extends RestogramActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.venue);

        // Get venue parameter
        try {
            Intent intent = getIntent();
            venueId = intent.getStringExtra("venue");
        }
        catch(Exception e) {
            // TODO: implementation
            return;
        }

        // Get venue from cache
        IRestogramCache cache = RestogramClient.getInstance().getCache();
        RestogramVenue venue = cache.findVenue(venueId);

        // Save venue if needed
        IDataHistoryManager dataHistoryManager = RestogramClient.getInstance().getDataHistoryManager();
        if(dataHistoryManager != null) {
            dataHistoryManager.save(venue, Defs.Data.SortOrder.SortOrderLIFO);
        }

        final ImageButton favoriteVenueButton =
                (ImageButton)findViewById(R.id.bVenueFavorite);
        if(venue.isfavorite())
            favoriteVenueButton.setImageResource(R.drawable.ic_favorite_on);
        else
            favoriteVenueButton.setImageResource(R.drawable.ic_favorite_off);

        // Initialize using venue parameter
        initialize(venue);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(pendingCommand != null) {
            pendingCommand.cancel();
            pendingCommand = null;
        }

        if (viewAdapter != null)
            viewAdapter.clear();

        dialogManager.clear();
    }

    @Override
    public void onFinished(GetPhotosResult result) {
        super.onFinished(result);

        // Update request pending flag
        isRequestPending = false;

        // Set pending command to null
        pendingCommand = null;

        if(result == null || result.getPhotos() == null) {

            if (viewAdapter.getCount() == 0) {
                // Show error dialog
                dialogManager.showNoPhotosAlert(this);
            }
                return;
        }

        if(RestogramClient.getInstance().isDebuggable())
            Log.d("REST-O-GRAM", "Adding " + result.getPhotos().length + " photos");

        // Add new photos
        addPhotos(result.getPhotos());

        // Update last token
        lastToken = result.getToken();

        // Update last token in cache
        IRestogramCache cache = RestogramClient.getInstance().getCache();
        RestogramPhotos venuePhotos = cache.findPhotos(venueId);
        if(venuePhotos != null)
            venuePhotos.setToken(lastToken);
    }

    @Override
    public void onFinished(GetInfoResult result) {
        super.onFinished(result);

        if(result == null || result.getVenue() == null)
            return;

        final RestogramVenue venue = result.getVenue();
        if(venue.getImageUrl() != null && !venue.getImageUrl().isEmpty()) {
            setVenuePhoto(venue.getImageUrl());
        }
    }

    @Override
    public void onCanceled() {
        super.onCanceled();

        // Update request pending flag
        isRequestPending = false;

        // Set pending command to null
        pendingCommand = null;
    }

    @Override
    public void onError() {
        super.onError();

        // Update request pending flag
        isRequestPending = false;

        // Set pending command to null
        pendingCommand = null;
    }

    @Override
    public void accept(IActivityVisitor visitor) {
        visitor.visit(this);
    }

    public void onNavigationClicked(View view) {
        try {
            // Get venue from cache
            IRestogramCache cache = RestogramClient.getInstance().getCache();
            RestogramVenue venue = cache.findVenue(venueId);

            // Start navigation
            Utils.startNavigation(this, venue.getLatitude(), venue.getLongitude());
        }
        catch(Exception e) {
            // Empty
        }
    }

    /**
     * Initializes using given venue
     */
    private void initialize(RestogramVenue venue) {
        venueId = venue.getFoursquare_id();

        // Init photo grid view
        GridView gv = (GridView)findViewById(R.id.gvPhotos);
        viewAdapter = new PhotoViewAdapter(this);
        gv.setAdapter(viewAdapter);

        // Set scroll listener
        gv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // Empty
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if(totalItemCount == 0)
                    return;

                // Check whether all photos are visible
                if((firstVisibleItem + visibleItemCount >= totalItemCount)
                        && !isRequestPending
                        && !hasMorePhotos
                        && showNoMorePhotosMessage) {
                    showNoMorePhotosMessage = false;
                    Toast.makeText(VenueActivity.this, R.string.venue_no_more_photos, Toast.LENGTH_LONG).show();
                }

                // Check whether enough views are visible
                if(++firstVisibleItem + visibleItemCount > totalItemCount - 20) {
                    onScrollBottom();
                }
            }
        });

        // Set UI with venue information
        Utils.updateTextView((TextView)findViewById(R.id.tvVenueName), venue.getName());
        Utils.updateTextView((TextView)findViewById(R.id.tvVenueAddress), venue.getAddress());
        Utils.updateTextView((TextView)findViewById(R.id.tvVenueCity), venue.getCity());
        Utils.updateTextView((TextView)findViewById(R.id.tvVenuePhone), venue.getPhone());

        // Set UI with venue image
        if(venue.getImageUrl() != null && !venue.getImageUrl().isEmpty()) {
            setVenuePhoto(venue.getImageUrl());
        }
        else {
            // Send get info request
            RestogramClient.getInstance().getInfo(venueId, this);
        }

        // Try to load previous photos from cache
        IRestogramCache cache = RestogramClient.getInstance().getCache();
        RestogramPhotos venuePhotos = cache.findPhotos(venueId);
        if(venuePhotos == null || venuePhotos.getToken() == null) { // No photos found
            isRequestPending = true;

            // Send get photos request
            pendingCommand = RestogramClient.getInstance().getPhotos(venue.getFoursquare_id(), RestogramFilterType.Simple, this);
        }
        else { // Photos were found
            // Save last token
            lastToken = venuePhotos.getToken();

            // Download all photos
            for(final RestogramPhoto photo : venuePhotos.getPhotos()) {
                // Download image
                RestogramClient.getInstance().downloadImage(photo.getThumbnail(), photo, viewAdapter, false, null);
            }
        }
    }

    private void addPhotos(RestogramPhoto[] photos) {
        // Traverse given photos
        for(final RestogramPhoto photo : photos) {
            // Download image
            RestogramClient.getInstance().downloadImage(photo.getThumbnail(), photo, viewAdapter, false, null);
        }
    }

    private void onScrollBottom() {
        if(isRequestPending)
            return;

        // if session is not yet over and more photos exist - request next photos
        if (lastToken != null && !lastToken.equals(CommonDefs.Tokens.FINISHED_FETCHING_FROM_INSTAGRAM)) {
            isRequestPending = true;
            if(RestogramClient.getInstance().isDebuggable())
                Log.d("REST-O-GRAM", "Requesting more photos");

            pendingCommand = RestogramClient.getInstance().getNextPhotos(lastToken, RestogramFilterType.Simple, venueId, this);
        }
        else {
            hasMorePhotos = false;
        }
    }

    private void setVenuePhoto(String url) {
        ImageView iv = (ImageView)findViewById(R.id.ivVenue);
        RestogramClient.getInstance().downloadImage(url, venueId, iv, true, null, 0.5f);
    }

    private String venueId; // Venue object
    private PhotoViewAdapter viewAdapter; // View adapter
    private String lastToken = null; // Last token
    private boolean isRequestPending = false; // Request pending flag
    private boolean showNoMorePhotosMessage = true; // No more photos message flag
    private boolean hasMorePhotos = true; // Has more photos flag
    private IRestogramCommand pendingCommand = null; // Pending get photos command
}
