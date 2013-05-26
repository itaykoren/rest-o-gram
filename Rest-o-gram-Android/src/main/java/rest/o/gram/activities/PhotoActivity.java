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
import rest.o.gram.activities.helpers.FavoriteHelper;
import rest.o.gram.activities.helpers.LoginHelper;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.commands.IRestogramCommand;
import rest.o.gram.commands.IRestogramCommandObserver;
import rest.o.gram.common.Defs;
import rest.o.gram.common.Utils;
import rest.o.gram.data_history.IDataHistoryManager;
import rest.o.gram.entities.RestogramPhoto;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 16/04/13
 */
public class PhotoActivity extends RestogramActivity implements IRestogramCommandObserver {

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

        // Save photo if needed
        IDataHistoryManager dataHistoryManager = RestogramClient.getInstance().getDataHistoryManager();
        if(dataHistoryManager != null)
            dataHistoryManager.save(photo, Defs.Data.SortOrder.SortOrderLIFO);

        // Initialize favorite helper
        favoriteHelper.setPhotoId(photo.getInstagram_id());
        favoriteHelper.setFavoritePhotoButton((ImageButton)findViewById(R.id.bPhotoFavorite));
        favoriteHelper.refresh();

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

    public void onFavoriteClicked(View view) {
        if(!RestogramClient.getInstance().getAuthenticationProvider().isUserLoggedIn()) {
            loginHelper.login(false);
        }
        else {
            // Add\Remove favorite
            favoriteHelper.toggleFavoritePhoto(photo);
        }
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
        command = RestogramClient.getInstance().downloadImage(photo.getStandardResolution(), iv, true, this);
    }

    private void cancelProgress() {
        ProgressBar pb = (ProgressBar)findViewById(R.id.pbImageLoading);
        pb.setVisibility(View.GONE);
    }

    private RestogramPhoto photo; // Photo object
    private IRestogramCommand command; // Command object
}
