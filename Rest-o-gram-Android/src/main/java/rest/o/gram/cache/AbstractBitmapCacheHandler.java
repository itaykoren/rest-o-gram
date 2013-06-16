package rest.o.gram.cache;

import android.graphics.Bitmap;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 6/13/13
 */
public abstract class AbstractBitmapCacheHandler implements IBitmapCacheHandler {
    @Override
    public boolean save(String id, Bitmap bitmap) {
        doSave(id, bitmap);
        if(next != null)
            next.save(id, bitmap);

        return true;
    }

    @Override
    public Bitmap load(String id) {
        Bitmap res = doLoad(id);
        if(res != null)
            return res;

        if(next != null)
            return next.load(id);

        return null;
    }

    @Override
    public boolean clear() {
        doClear();
        if(next != null)
            next.clear();

        return true;
    }

    @Override
    public void setNext(IBitmapCacheHandler next) {
        this.next = next;
    }

    /**
     * Attempts to save given bitmap with given id to cache
     * Returns true if successful, false otherwise
     */
    protected abstract boolean doSave(String id, Bitmap bitmap);

    /**
     * Attempts to load bitmap from cache according to its id
     * Returns bitmap if successful, null otherwise
     */
    protected abstract Bitmap doLoad(String id);

    /**
     * Clears all data
     * Returns true if successful, false otherwise
     */
    protected abstract boolean doClear();

    private IBitmapCacheHandler next;
}
