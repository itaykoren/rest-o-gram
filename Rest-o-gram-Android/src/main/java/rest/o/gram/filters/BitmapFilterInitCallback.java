package rest.o.gram.filters;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 6/24/14
 */
public interface BitmapFilterInitCallback {

    /**
     * Called when a new bitmap filter has finished initialization.
     * @param filter the new bitmap filter
     */
    void onBitmapFilterInit(IBitmapFilter filter);
}
