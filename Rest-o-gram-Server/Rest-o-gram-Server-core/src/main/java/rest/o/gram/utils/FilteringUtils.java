package rest.o.gram.utils;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Itay
 * Date: 24/04/13
 */
public class FilteringUtils {

    public static boolean disjointSets(Set first, Set second) {

        // currently returning false for empty or null sets, as otherwise too many photos will be filtered out
        if (first == null || first.size() == 0 || second == null || second.size() == 0)
            return false;

        if (first.size() <= second.size())
            return disjoint(first, second);

        return disjoint(second, first);
    }


    private static boolean disjoint(Set smaller, Set bigger) {

        for (Object item : smaller) {
            if (bigger.contains(item))
                return false;
        }

        return true;
    }

}
