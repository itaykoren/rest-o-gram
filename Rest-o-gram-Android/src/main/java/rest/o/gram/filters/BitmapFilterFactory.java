package rest.o.gram.filters;

import rest.o.gram.common.Defs;
import rest.o.gram.common.Utils;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 8/22/13
 */
public class BitmapFilterFactory implements IBitmapFilterFactory {
    @Override
    public IBitmapFilter create(final Defs.Filtering.BitmapFilterType type) {
        // If hardware is not capable of filtering - falls back to default filter
        if (!Utils.canApplyBitmapFilter())
            return new DefaultBitmapFilter();

        switch(type) {
            case DoNothingBitmapFilter:
                return new DefaultBitmapFilter();
            case AndroidFaceBitmapFilter:
                return new AndroidFaceBitmapFilter(Defs.Filtering.AndroidDetector.MAX_FACES_TO_DETECT);
            case JavaCVFaceBitmapFilter:
            case OpenCVFaceBitmapFilter:
                return new OpenCvBitmapFilter();
            default:
                return null;
        }
    }
}
