package rest.o.gram.filters;

import android.graphics.Bitmap;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 5/21/13
 */
public class DefaultBitmapFilter implements IBitmapFilter {
    @Override
    public boolean accept(final Bitmap bitmap) {
        // Accept all bitmaps
        return true;
    }
}
