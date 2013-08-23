package rest.o.gram.view;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 13/05/13
 */
public interface IPhotoViewAdapter {
    /**
     * Adds photo
     * */
    void addPhoto(String photoId, String bitmapId);

    /**
     * Refreshes this adapter
     */
    void refresh();

    /**
     * Clears all photos
     */
    void clear();
}
