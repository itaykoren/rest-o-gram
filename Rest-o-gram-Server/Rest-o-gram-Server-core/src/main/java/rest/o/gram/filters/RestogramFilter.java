package rest.o.gram.filters;

import org.jinstagram.entity.users.feed.MediaFeedData;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Itay
 * Date: 21/04/13
 */
public interface RestogramFilter {

    List<MediaFeedData> doFilter(List<MediaFeedData> data);
}
