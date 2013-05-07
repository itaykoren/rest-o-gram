package rest.o.gram.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import rest.o.gram.R;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.common.Defs;
import rest.o.gram.common.Utils;
import rest.o.gram.common.ViewAdapter;
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.entities.RestogramVenue;
import rest.o.gram.filters.RestogramFilterType;
import rest.o.gram.tasks.ITaskObserver;
import rest.o.gram.tasks.results.GetInfoResult;
import rest.o.gram.tasks.results.GetNearbyResult;
import rest.o.gram.tasks.results.GetPhotosResult;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 16/04/13
 */
public class VenueActivity extends Activity implements ITaskObserver {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.venue);

        // Get venue parameter
        RestogramVenue venue;
        try {
            Intent intent = getIntent();
            venue = (RestogramVenue)intent.getSerializableExtra("venue");
        }
        catch(Exception e) {
            // TODO: implementation
            return;
        }

        // Initialize using venue parameter
        initialize(venue);
    }

    @Override
    protected  void onDestroy() {
        super.onDestroy();

        if (viewAdapter != null)
            viewAdapter.clear();
    }

    @Override
    public void onFinished(GetNearbyResult result) {
        // Empty
    }

    @Override
    public void onFinished(GetInfoResult result) {
        // Empty
    }

    @Override
    public void onFinished(GetPhotosResult result) {
        if(result == null)
            return;

        if(RestogramClient.getInstance().isDebuggable())
            Log.d("REST-O-GRAM", "Adding " + result.getPhotos().length + " photos");

        // Add new photos
        addPhotos(result.getPhotos());

        // Update last token
        lastToken = result.getToken();

        // Update request pending flag
        isRequestPending = false;
    }

    /**
     * Initializes using given venue
     */
    private void initialize(RestogramVenue venue) {
        this.venue = venue;

        // Init photo grid view
        GridView gv = (GridView)findViewById(R.id.gvPhotos);
        viewAdapter = new ViewAdapter();
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

                // Check whether the last view is visible
                if(++firstVisibleItem + visibleItemCount > totalItemCount) {
                    onScrollBottom();
                }
            }
        });

        // Set UI with venue information
        Utils.updateTextView((TextView)findViewById(R.id.tvVenueName), venue.getName());
        if(venue.getAddress() != null)
            Utils.updateTextView((TextView)findViewById(R.id.tvVenueAddress), venue.getAddress());
        if(venue.getPhone() != null)
            Utils.updateTextView((TextView)findViewById(R.id.tvVenuePhone), venue.getPhone());

        // Set UI with venue image
        if(venue.getImageUrl() != null && !venue.getImageUrl().isEmpty()) {
            ImageView iv = (ImageView)findViewById(R.id.ivVenue);
            RestogramClient.getInstance().downloadImage(venue.getImageUrl(), iv, 80, 80, true);
        }

        // Send get photos request
        RestogramClient.getInstance().getPhotos(venue.getId(), RestogramFilterType.Simple, this);
    }

    private void addPhotos(RestogramPhoto[] photos) {
        // Traverse given photos
        for(final RestogramPhoto photo : photos) {
            // Create new image view
            ImageView iv = new ImageView(this);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onPhotoClicked(photo);
                }
            });

            // Add view
            viewAdapter.addView(iv);

            // Download image
            RestogramClient.getInstance().downloadImage(photo.getThumbnail(), iv, 40, 40, false);
        }

        viewAdapter.refresh();
    }

    private void onPhotoClicked(RestogramPhoto photo) {
        // Switch to "PhotoActivity" with parameter "photo"
        Intent intent = new Intent(this, PhotoActivity.class);
        intent.putExtra("photo", photo);
        startActivityForResult(intent, Defs.RequestCodes.RC_PHOTO);
    }

    private void onScrollBottom() {
        if(isRequestPending)
            return;

        // if session is not yet over - request next photos
        if (lastToken != null) {
            if(RestogramClient.getInstance().isDebuggable())
                Log.d("REST-O-GRAM", "Requesting more photos");

            RestogramClient.getInstance().getNextPhotos(lastToken, this);
            isRequestPending = true;
        }
    }

    private RestogramVenue venue; // Venue object
    private ViewAdapter viewAdapter; // View adapter
    private String lastToken = null; // Last token
    private boolean isRequestPending = false; // Request pending flag
}
