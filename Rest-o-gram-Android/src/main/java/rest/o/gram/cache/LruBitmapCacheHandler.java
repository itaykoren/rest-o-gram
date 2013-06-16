package rest.o.gram.cache;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 6/13/13
 */
public class LruBitmapCacheHandler extends AbstractBitmapCacheHandler {
    /**
     * Ctor
     */
    public LruBitmapCacheHandler() {
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int)(Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        cache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    @Override
    protected boolean doSave(String id, Bitmap bitmap) {
        if(doLoad(id) != null)
            return false;

        return cache.put(id, bitmap) != null;
    }

    @Override
    protected Bitmap doLoad(String id) {
        return cache.get(id);
    }

    @Override
    protected boolean doClear() {
        cache.evictAll();
        return true;
    }

    private LruCache<String, Bitmap> cache;
}
