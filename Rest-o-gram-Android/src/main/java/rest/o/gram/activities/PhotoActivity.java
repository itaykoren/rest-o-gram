package rest.o.gram.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import rest.o.gram.R;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.commands.IRestogramCommand;
import rest.o.gram.commands.IRestogramCommandObserver;
import rest.o.gram.common.Utils;
import rest.o.gram.entities.RestogramPhoto;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 16/04/13
 */
public class PhotoActivity extends Activity implements IRestogramCommandObserver {

    @Override
    public void onFinished(IRestogramCommand command) {
        //cancelProgress();
    }

    @Override
    public void onError(IRestogramCommand command) {
        //cancelProgress();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.photo);

        // Get photo parameter
        RestogramPhoto photo;
        Bitmap bitmap;
        try {
            Intent intent = getIntent();
            photo = (RestogramPhoto)intent.getSerializableExtra("photo");
            bitmap = (Bitmap)intent.getParcelableExtra("thumbnail_bitmap");
        }
        catch(Exception e) {
            // TODO: implementation
            return;
        }

        // Initialize using photo parameter
        initialize(photo, bitmap);
    }

    /**
     * Initializes using given photo
     */
    private void initialize(RestogramPhoto photo, Bitmap bitmap) {
        this.photo = photo;

        // Get image view
        ImageView iv = (ImageView)findViewById(R.id.ivPhoto);

        // Set thumbnail bitmap (if given) as a temporary photo
        if(bitmap != null)
            iv.setImageBitmap(bitmap);

        // Update UI
        Utils.updateTextView((TextView)findViewById(R.id.tvLikes), String.valueOf(photo.getLikes())+" likes");
        Utils.updateTextView((TextView)findViewById(R.id.tvCreationTime), Utils.convertDate(photo.getCreatedTime()));
        Utils.updateTextView((TextView)findViewById(R.id.tvUsername), photo.getUser());
        Utils.updateTextView((TextView)findViewById(R.id.tvTitle), photo.getCaption());

        // Set UI with standard resolution image
        RestogramClient.getInstance().downloadImage(photo.getStandardResolution(), iv, 200, 200, true, this);
    }

//    private void cancelProgress() {
//        ProgressBar pb = (ProgressBar)findViewById(R.id.pbImageLoading);
//        pb.setVisibility(View.GONE);
//    }

    private RestogramPhoto photo; // Photo object
}
