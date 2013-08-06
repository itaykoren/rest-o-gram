package rest.o.gram.utils;

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
}
