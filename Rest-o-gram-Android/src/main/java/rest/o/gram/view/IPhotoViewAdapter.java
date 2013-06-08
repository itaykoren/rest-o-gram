package rest.o.gram.view;

import android.graphics.Bitmap;
import rest.o.gram.entities.RestogramPhoto;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 13/05/13
 */
public interface IPhotoViewAdapter {
    /**
     * Adds photo
     * */
    void addPhoto(String photoId, Bitmap bitmap);

    /**
     * Refreshes this adapter
     */
    void refresh();

    /**
     * Clears all photos
     */
    void clear();
}
