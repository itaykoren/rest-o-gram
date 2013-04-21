package rest.o.gram.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import rest.o.gram.R;
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.entities.RestogramVenue;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.common.Defs;
import rest.o.gram.common.Utils;
import rest.o.gram.common.ViewAdapter;
import rest.o.gram.tasks.DownloadImageTask;
import rest.o.gram.tasks.ITaskObserver;

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
    public void onFinished(RestogramVenue[] venues) {
        // Empty
    }

    @Override
    public void onFinished(RestogramVenue venue) {
        // Empty
    }

    @Override
    public void onFinished(RestogramPhoto[] photos) {
        addPhotos(photos);
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

        // Set UI with venue information
        Utils.updateTextView((TextView)findViewById(R.id.tvVenueName), venue.getName());
        Utils.updateTextView((TextView)findViewById(R.id.tvVenueAddress), venue.getAddress());
        Utils.updateTextView((TextView)findViewById(R.id.tvVenuePhone), venue.getPhone());

        // Set UI with venue image
        if(!venue.getImageUrl().isEmpty()) {
            ImageView iv = (ImageView)findViewById(R.id.ivVenue);
            DownloadImageTask task = new DownloadImageTask(iv);
            task.execute(venue.getImageUrl());
        }

        // Send get photos request
        RestogramClient.getInstance().getPhotos(venue.getId(), this);
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
            DownloadImageTask task = new DownloadImageTask(iv);
            task.execute(photo.getThumbnail());
        }

        viewAdapter.refresh();
    }

    private void onPhotoClicked(RestogramPhoto photo) {
        // Switch to "PhotoActivity" with parameter "photo"
        Intent intent = new Intent(this, PhotoActivity.class);
        intent.putExtra("photo", photo);
        startActivityForResult(intent, Defs.RequestCodes.RC_PHOTO);
    }

    private RestogramVenue venue; // Venue object
    private ViewAdapter viewAdapter; // View adapter
}
