package rest.o.gram.cache;

import android.graphics.Bitmap;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 6/13/13
 */
public interface IBitmapCacheHandler {
    /**
     * Attempts to save given bitmap with given id to cache
     * Returns true if successful, false otherwise
     */
    boolean save(String id, Bitmap bitmap);

    /**
     * Attempts to load bitmap from cache according to its id
     * Returns bitmap if successful, null otherwise
     */
    Bitmap load(String id);

    /**
     * Clears all data
     * Returns true if successful, false otherwise
     */
    boolean clear();

    /**
     * Sets given handler as next
     */
    void setNext(IBitmapCacheHandler next);
}
