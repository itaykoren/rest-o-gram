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
     * Tokens constants
     */
    public static class Tokens {
        public static final String FINISHED_FETCHING_FROM_CACHE = "FINISHED_FETCHING_FROM_CACHE";
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
     * Instagram constants
     */
    public static class Instagram {
        public static final int REQUESTS_TIMEOUT = 8;
        public static final TimeUnit REQUESTS_TIMEOUT_UNIT = TimeUnit.SECONDS;
        public static final int ACCESS_SERVICES_AMOUNT = 7;

        public static enum RequestType
        {
            GetLocation("get-location"),
            GetMediaByLocation("get-media-by-location");

            RequestType(String type) {
                this.type = type;
            }

            public String getType() {
                return type;
            }

            private String type;
        }
    }

    public static class FilterRulesQueue {
        public static final int LEASE_COUNT = 1;
        public static final int LEASE_PERIOD = 30; //secs
    }
}

