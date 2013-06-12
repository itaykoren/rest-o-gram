package rest.o.gram.filters;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Itay
 * Date: 21/04/13
 */
public class RestogramFilterFactory {

    public static RestogramFilter createFilter(RestogramFilterType filterType, Map<String,Boolean> filterRules) {

        if (filterType == RestogramFilterType.Simple)
            return new HashtagRestogramFilter();
        else if (filterType == RestogramFilterType.Complex)
            return new RuleSetRestogramFilter(filterRules);
        else
            throw new IllegalArgumentException("could not find RestogramFilter that matches filterType: " + filterType);
    }
}
