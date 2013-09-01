package rest.o.gram.utils;

import org.jinstagram.entity.locations.LocationSearchFeed;
import org.jinstagram.entity.users.feed.MediaFeed;
import rest.o.gram.service.InstagramServices.Entities.EmptyLocationSearchFeed;
import rest.o.gram.service.InstagramServices.Entities.EmptyMediaFeed;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/25/13
 */
public final class InstagramUtils {
//    public static long extractMediaId(String id) {
//        return Long.parseLong(id.split("_")[0]);
//    }

    public static String extractMediaId(String id) {
        return id.split("_")[0];
    }

//    public static long extractUserId(String id) {
//        return Long.parseLong(id.split("_")[1]);
//    }

    public static String extractUserId(String id) {
        return id.split("_")[1];
    }

    public static boolean isNullOrEmpty(final LocationSearchFeed locationSearchFeed) {
        return locationSearchFeed == null || locationSearchFeed.getLocationList() == null ||
               locationSearchFeed.getLocationList().isEmpty() || locationSearchFeed.getClass() == EmptyLocationSearchFeed.class;
    }

    public static boolean isNullOrEmpty(final MediaFeed mediaFeed)  {
        return mediaFeed == null || mediaFeed.getData() == null ||
               mediaFeed.getData().isEmpty() || mediaFeed.getClass() == EmptyMediaFeed.class;
    }
}
