package rest.o.gram.cache;

import android.graphics.Bitmap;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 6/13/13
 */
public class DummyBitmapCache implements IBitmapCache {
    @Override
    public boolean save(String id, Bitmap bitmap) {
        return false;
    }

    @Override
    public Bitmap load(String id) {
        return null;
    }

    @Override
    public boolean clear() {
        return false;
    }
}
