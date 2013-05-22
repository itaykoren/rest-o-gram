package rest.o.gram.filters;

import android.graphics.Bitmap;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 5/21/13
 */
public interface IBitmapFilter {
    /**
     * Returns true whether this bitmap is accepted by this filter
     */
    boolean accept(Bitmap bitmap);
}
