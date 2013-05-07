package rest.o.gram.filters;

/**
 * Created with IntelliJ IDEA.
 * User: Itay
 * Date: 21/04/13
 */
public class RestogramFilterFactory {

    public static RestogramFilter createFilter(RestogramFilterType filterType) {

        if (filterType == RestogramFilterType.Simple) {
            return new HashtagRestogramFilter();
        }

        if (filterType == RestogramFilterType.Complex) {
            // TODO - complex filterType
            return null;
        } else {
            throw new IllegalArgumentException("could not find RestogramFilter that matches filterType: " + filterType);
        }
    }

}
