package rest.o.gram.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import rest.o.gram.R;
import rest.o.gram.cache.IRestogramCache;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.commands.IRestogramCommand;
import rest.o.gram.commands.IRestogramCommandObserver;
import rest.o.gram.common.Defs;
import rest.o.gram.common.Utils;
import rest.o.gram.data_history.IDataHistoryManager;
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.entities.RestogramVenue;
import rest.o.gram.tasks.results.GetInfoResult;
import rest.o.gram.view.PhotoInfoView;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 16/04/13
 */
public class PhotoActivity extends RestogramActionBarActivity implements IRestogramCommandObserver {

    @Override
    public void onCanceled(IRestogramCommand command) {
        cancelProgress();
        this.command = null;
    }

    @Override
    public void onFinished(IRestogramCommand command) {
        cancelProgress();
        this.command = null;
    }

    @Override
    public void onError(IRestogramCommand command) {
        cancelProgress();
        this.command = null;
    }

    @Override
    public void onTimeout(IRestogramCommand command) {
        cancelProgress();
        this.command = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.photo);

        // Get photo parameter
        Bitmap bitmap;
        try {
            Intent intent = getIntent();
            photoId = intent.getStringExtra("photo");
            bitmap = (Bitmap)intent.getParcelableExtra("thumbnail_bitmap");
        }
        catch(Exception | OutOfMemoryError e) {
            // TODO: implementation
            return;
        }

        // Get venue from cache
        IRestogramCache cache = RestogramClient.getInstance().getCache();
        RestogramPhoto photo = cache.findPhoto(photoId);

        // Save photo if needed
        IDataHistoryManager dataHistoryManager = RestogramClient.getInstance().getDataHistoryManager();
        if(dataHistoryManager != null)
            dataHistoryManager.save(photo, Defs.Data.SortOrder.SortOrderLIFO);

        // Initialize favorite helper
        favoriteHelper.setPhotoId(photo.getInstagram_id());
        favoriteHelper.setFavoritePhotoButton((ImageButton)findViewById(R.id.bPhotoFavorite));
        //favoriteHelper.refresh();

        final ImageButton favoritePhotoButton =
                (ImageButton)findViewById(R.id.bPhotoFavorite);
        if(photo.is_favorite())
            favoritePhotoButton.setImageResource(R.drawable.ic_favorite_on);
        else
            favoritePhotoButton.setImageResource(R.drawable.ic_favorite_off);

        // Initialize using photo parameter
        initialize(photo, bitmap);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(command != null) {
            command.removeObserver(this);
            command.cancel();
            command = null;
        }
    }

    @Override
    public void onFinished(GetInfoResult result) {
        super.onFinished(result);

        if(result == null || result.getVenue() == null)
            return;

        initialize(result.getVenue());
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

    public void onFavoriteClicked(View view) {
        if(!RestogramClient.getInstance().getAuthenticationProvider().isUserLoggedIn()) {
            loginHelper.login(false);
        }
        else {
            // Add\Remove favorite
            favoriteHelper.toggleFavoritePhoto(photoId);
        }
    }

    public void onInfoClicked(View view) {
        if(photoInfoView == null)
            return;

        if(photoInfoView.isOpen())
            return;

        photoInfoView.open();
    }

    public void onVenueInfoClicked(View view) {
        // Get venue from cache
        IRestogramCache cache = RestogramClient.getInstance().getCache();
        if(cache.findVenue(venueId) == null)
            return;

        // Switch to "VenueActivity" with parameter "venue"
        Intent intent = new Intent(this, VenueActivity.class);
        intent.putExtra("venue", venueId);
        Utils.changeActivity(this, intent, Defs.RequestCodes.RC_VENUE, false);
    }

    /**
     * Initializes using given photo
     */
    private void initialize(RestogramPhoto photo, Bitmap bitmap) {
        photoId = photo.getInstagram_id();

        // Get image view
        ImageView iv = (ImageView)findViewById(R.id.ivPhoto);

        // Set thumbnail bitmap (if given) as a temporary photo
        if(bitmap != null)
            iv.setImageBitmap(bitmap);

        // Set UI with standard resolution image
        command = RestogramClient.getInstance().downloadImage(photo.getStandardResolution(), photoId, iv, true, this);

        // init yummies count
        // TODO - change from getLikes to getYummies (currently there's no such field in restogramphoto)
        long yummies = photo.getLikes();
        if (yummies > 0)
            Utils.updateTextView((TextView) findViewById(R.id.tvPhotoYummies), String.format(String.format("%d yummies", yummies)));

        // Init photo info view
        photoInfoView = new PhotoInfoView(this, photo);

        // Init venue information: attempt to load from cache. if not found - send request to server
        String id = photo.getOriginVenueId();

        // Load venue from cache if possible
        IRestogramCache cache = RestogramClient.getInstance().getCache();
        if(cache != null) {
            RestogramVenue venue = cache.findVenue(id);
            if(venue != null) {
                initialize(venue);
                return;
            }
        }

        // Send get info request
        RestogramClient.getInstance().getInfo(id, this);
    }

    /**
     * Initializes using given venue
     */
    private void initialize(RestogramVenue venue) {
        venueId = venue.getFoursquare_id();

        // Set UI with venue information
        Utils.updateTextView((TextView)findViewById(R.id.tvVenueName), venue.getName());
        if(venue.getAddress() != null)
            Utils.updateTextView((TextView)findViewById(R.id.tvVenueAddress), venue.getAddress());
        if(venue.getPhone() != null)
            Utils.updateTextView((TextView)findViewById(R.id.tvVenuePhone), venue.getPhone());

        // Set UI with venue image
        String imageUrl = venue.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            ImageView iv = (ImageView) findViewById(R.id.ivVenue);
            RestogramClient.getInstance().downloadImage(imageUrl, venueId, iv, true, null);
        } else {
            // Send get info request
            RestogramClient.getInstance().getInfo(venueId, this);
        }
    }

    private void cancelProgress() {
        ProgressBar pb = (ProgressBar)findViewById(R.id.pbImageLoading);
        pb.setVisibility(View.GONE);
    }

    private String photoId; // Photo object
    private IRestogramCommand command; // Command object
    private PhotoInfoView photoInfoView; // Photo info view
    private String venueId; // Venue object
}
