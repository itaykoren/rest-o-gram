package rest.o.gram.entities;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/23/13
 */
public final class Props {
    public static class Venue {
        public final static String NAME = "name";
        public final static String ADDRESS = "address";
        public final static String CITY = "city";
        public final static String STATE = "state";
        public final static String POSTAL_CODE = "postal_code";
        public final static String COUNTRY = "country";
        public final static String LAT = "lat";
        public final static String LONG = "long";
        public final static String DISTANCE = "distance";
        public final static String URL = "url";
        public final static String PHONE = "phone";

        public final static String DESCRIPTION = "desc";
        public final static String IMAGE_URL = "image_url";
    }

    public static  class VenueRef {
        public final static String FOURSQUARE_ID = "foursquare_id";
        public final static String IS_FAVORITE = "is_fav";
    }

    public static  class VenueMeta {
        public final static String FOURSQUARE_ID = "foursquare_id";
        public final static String APPROVED = "approved";
    }

    public static class Photo {
        public final static String CAPTION = "caption";
        public final static String CREATED_TIME = "created_time";
        public final static String IMAGE_FILTER = "image_filter";
        public final static String THUMBNAIL = "thumbnail";
        public final static String STANDARD_RESOLUTION = "standard_resolution";
        public final static String LIKES = "likes";
        public final static String LINK = "link";
        public final static String TYPE = "type";
        public final static String USER = "user";
        public final static String ORIGIN_VENUE_ID = "origin_venue_id";
    }

    public static  class PhotoRef {
        public final static String INSTAGRAM_ID = "instagram_id";
        public final static String IS_FAVORITE = "is_fav";
    }

    public static  class PhotoMeta {
        public final static String INSTAGRAM_ID = "instagram_id";
        public final static String APPROVED = "approved";
        public final static String VENUE_ID = "venue_id";
    }
}
