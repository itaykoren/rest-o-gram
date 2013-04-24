package rest.o.gram.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import org.apache.http.impl.cookie.DateUtils;
import rest.o.gram.R;
import rest.o.gram.common.Utils;
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.tasks.DownloadImageTask;

import java.text.SimpleDateFormat;
import java.util.Date;

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

        // Update UI
        Utils.updateTextView((TextView)findViewById(R.id.tvLikes), String.valueOf(photo.getLikes())+" likes");
        Utils.updateTextView((TextView)findViewById(R.id.tvCreationTime), Utils.convertDate(photo.getCreatedTime()));
        Utils.updateTextView((TextView)findViewById(R.id.tvUsername), photo.getUser());
        Utils.updateTextView((TextView)findViewById(R.id.tvTitle), photo.getCaption());

        // Set UI with standard resolution image
        ImageView iv = (ImageView)findViewById(R.id.ivPhoto);
        DownloadImageTask task = new DownloadImageTask(iv);
        task.execute(photo.getStandardResolution());
    }

    private RestogramPhoto photo; // Photo object
}
