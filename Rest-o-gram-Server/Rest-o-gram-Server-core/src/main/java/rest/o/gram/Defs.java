package rest.o.gram;

import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Itay
 * Date: 15/06/13
 */
public final class Defs {

    /**
     * Request constants
     */
    public static class Request {
        public static final int MIN_PHOTOS_PER_REQUEST = 6;
    }

    /**
     * Transport constants
     */
    public static class Transport {
        public static final String BASE_HOST_NAME = "http://rest-o-gram.appspot.com";
        public static final String HELPERS_HOST_NAME_SCHEME = "http://rest-o-helper%d.appspot.com";
    }

    /**
     * Credentials constants
     */
    public static class Credentials {
        public static final int CREDENTIALS_AMOUNT = 4;
    }

    /**
     * Foursquare  constants
     */
    public static class Foursquare {
        // TODO: manage foursquare categories...
        public static final String VENUE_CATEGORY = "4d4b7105d754a06374d81259";
    }

    /**
     * Instagram constants
     */
    public static class Instagram {
        public static final int REQUESTS_TIMEOUT = 8;
        public static final TimeUnit REQUESTS_TIMEOUT_UNIT = TimeUnit.SECONDS;
        public static final int FRONTEND_ACCESS_SERVICES_AMOUNT = 4;
        public static final int BACKEND_ACCESS_SERVICES_AMOUNT = 11;

        public static enum RequestType
        {
            GetLocation("get-location"),
            GetMediaByLocation("get-media-by-location"),
            GetPhoto("get-photo");

            RequestType(String type) {
                this.type = type;
            }

            public String getType() {
                return type;
            }

            private String type;
        }
    }

    /**
     * Filter rules constants
     */
    public static class FilterRulesQueue {
        public static final int LEASE_COUNT = 1;
        public static final int LEASE_PERIOD = 120; //secs
    }
}

