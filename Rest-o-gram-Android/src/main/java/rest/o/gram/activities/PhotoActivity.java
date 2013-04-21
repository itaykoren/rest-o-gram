package rest.o.gram.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import rest.o.gram.R;
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.tasks.DownloadImageTask;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 16/04/13
 */
public class PhotoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.photo);

        // Get photo parameter
        RestogramPhoto photo;
        try {
            Intent intent = getIntent();
            photo = (RestogramPhoto)intent.getSerializableExtra("photo");
        }
        catch(Exception e) {
            // TODO: implementation
            return;
        }

        // Initialize using photo parameter
        initialize(photo);
    }

    /**
     * Initializes using given photo
     */
    private void initialize(RestogramPhoto photo) {
        this.photo = photo;

        // TODO: Update UI

        // Set UI with standard resolution image
        ImageView iv = (ImageView)findViewById(R.id.ivPhoto);
        DownloadImageTask task = new DownloadImageTask(iv);
        task.execute(photo.getStandardResolution());
    }

    private RestogramPhoto photo; // Photo object
}
