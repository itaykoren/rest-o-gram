package com.tau.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.tau.R;
import com.tau.RestogramPhoto;
import com.tau.RestogramVenue;
import com.tau.client.RestogramClient;
import com.tau.common.Defs;
import com.tau.tasks.DownloadImageTask;
import com.tau.tasks.ITaskObserver;

import java.util.LinkedList;
import java.util.List;

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
        if(venue.getImageUrl().isEmpty())
            return;

        // Set UI with venue image
        ImageView iv = (ImageView)findViewById(R.id.ivVenue);
        DownloadImageTask task = new DownloadImageTask(iv);
        task.execute(venue.getImageUrl());
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

        // Create view list
        viewList = new LinkedList<View>();

        // Set UI with venue information
        updateTextView((TextView)findViewById(R.id.tvVenueName), venue.getName());
        updateTextView((TextView)findViewById(R.id.tvVenueAddress), venue.getAddress());
        updateTextView((TextView)findViewById(R.id.tvVenuePhone), venue.getPhone());

        // Send get info request if needed
        if(venue.getImageUrl() == null)
            RestogramClient.getInstance().getInfo(venue.getId(), this);
        else
            onFinished(venue);

        // Send get photos request
        RestogramClient.getInstance().getPhotos(venue.getId(), this);
    }

    private void updateTextView(TextView tv, String text) {
        if(tv == null)
            return;

        tv.setText(text);
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

            // Add to list
            viewList.add(iv);

            // Download image
            DownloadImageTask task = new DownloadImageTask(iv);
            task.execute(photo.getThumbnail());
        }

        // Init photo grid view
        GridView gv = (GridView)findViewById(R.id.gvPhotos);
        gv.setAdapter(new PhotosAdapter());
    }

    private void onPhotoClicked(RestogramPhoto photo) {
        // Switch to "PhotoActivity" with parameter "photo"
        Intent intent = new Intent(this, PhotoActivity.class);
        intent.putExtra("photo", photo);
        startActivityForResult(intent, Defs.RequestCodes.RC_PHOTO);
    }

    /**
     * Created with IntelliJ IDEA.
     * User: Roi
     * Date: 17/04/13
     */
    private class PhotosAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return viewList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            return viewList.get(i);
        }
    }

    private RestogramVenue venue; // Venue object
    private List<View> viewList; // View list
}
