package rest.o.gram.utils;

import org.jinstagram.entity.locations.LocationSearchFeed;
import org.jinstagram.entity.media.MediaInfoFeed;
import org.jinstagram.entity.users.feed.MediaFeed;
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.service.InstagramServices.Entities.EmptyLocationSearchFeed;
import rest.o.gram.service.InstagramServices.Entities.EmptyRestogramPhoto;
import rest.o.gram.service.InstagramServices.Entities.EmptyRestogramPhotos;
import rest.o.gram.service.InstagramServices.Entities.RestogramPhotos;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/25/13
 */
public final class InstagramUtils {

    public static boolean isNullOrEmpty(final LocationSearchFeed locationSearchFeed) {
        return locationSearchFeed == null || locationSearchFeed.getLocationList() == null ||
               locationSearchFeed.getLocationList().isEmpty() || locationSearchFeed.getClass() == EmptyLocationSearchFeed.class;
    }

    public static boolean isNullOrEmpty(final MediaFeed mediaFeed)  {
        return mediaFeed == null || mediaFeed.getData() == null ||
               mediaFeed.getData().isEmpty();
    }

    public static boolean isNullOrEmpty(final MediaInfoFeed mediaInfoFeed)  {
        return mediaInfoFeed == null || mediaInfoFeed.getData() == null;
    }

    public static boolean isNullOrEmpty(final RestogramPhotos restogramPhotos) {
        return restogramPhotos == null || restogramPhotos.getPhotos() == null ||
               restogramPhotos.getPhotos().isEmpty() || restogramPhotos.getClass() == EmptyRestogramPhotos.class;
    }

    public static boolean isNullOrEmpty(final RestogramPhoto restogramPhoto) {
        return restogramPhoto == null ||  restogramPhoto.getClass() == EmptyRestogramPhoto.class;
    }
}
