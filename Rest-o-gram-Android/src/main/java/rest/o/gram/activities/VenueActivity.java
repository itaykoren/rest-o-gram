package rest.o.gram.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import rest.o.gram.R;
import rest.o.gram.RestogramPhoto;
import rest.o.gram.RestogramVenue;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.common.Defs;
import rest.o.gram.tasks.DownloadImageTask;
import rest.o.gram.tasks.ITaskObserver;

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
        photosAdapter = new PhotosAdapter();
        gv.setAdapter(photosAdapter);

        // Set UI with venue information
        updateTextView((TextView)findViewById(R.id.tvVenueName), venue.getName());
        updateTextView((TextView)findViewById(R.id.tvVenueAddress), venue.getAddress());
        updateTextView((TextView)findViewById(R.id.tvVenuePhone), venue.getPhone());

        // Set UI with venue image
        if(!venue.getImageUrl().isEmpty()) {
            ImageView iv = (ImageView)findViewById(R.id.ivVenue);
            DownloadImageTask task = new DownloadImageTask(iv);
            task.execute(venue.getImageUrl());
        }

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

            // Add view
            photosAdapter.addView(iv);

            // Download image
            DownloadImageTask task = new DownloadImageTask(iv);
            task.execute(photo.getThumbnail());
        }

        photosAdapter.refresh();
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
        /**
         * Ctor
         * */
        public PhotosAdapter() {
            // Create view list
            viewList = new LinkedList<View>();
        }

        @Override
        public int getCount() {
            return viewList.size();
        }

        @Override
        public Object getItem(int i) {
            if(i < 0 || i >= viewList.size())
                return null;

            return viewList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(i < 0 || i >= viewList.size())
                return null;

            return viewList.get(i);
        }

        /**
         * Adds view
         * */
        public void addView(View view) {
            viewList.add(view);
        }

        /**
         * Refreshes this adapter
         */
        public void refresh() {
            notifyDataSetChanged();
        }

        private List<View> viewList; // View list
    }

    private RestogramVenue venue; // Venue object
    private PhotosAdapter photosAdapter; // Photos adapter
}
