package rest.o.gram.filters;

import org.jinstagram.entity.users.feed.MediaFeedData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 6/10/13
 */
public class RuleSetRestogramFilter implements RestogramFilter {
    public RuleSetRestogramFilter(Map<String, Boolean> filterRules) {
        this.filterRules = filterRules;
    }

    @Override
    public List<MediaFeedData> doFilter(List<MediaFeedData> data) {
        if (filterRules == null || filterRules.isEmpty())
            return data;

        final List<MediaFeedData> filtered = new ArrayList<>(filterRules.size());
        for (final MediaFeedData currMediaFeedData : data)
        {
            if (filterRules.get(currMediaFeedData.getId()))
                filtered.add(currMediaFeedData);
        }
        return  filtered;
    }

    private Map<String,Boolean> filterRules;
}
