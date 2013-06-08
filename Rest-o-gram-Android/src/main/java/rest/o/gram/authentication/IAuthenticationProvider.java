package rest.o.gram.authentication;

import android.net.Uri;
import com.leanengine.NetworkCallback;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/17/13
 */
public interface IAuthenticationProvider {

    /**
     * Returns whether or not user is logged in
     */
    boolean isUserLoggedIn();

    /**
     * Returns Facebook Login URI
     */
    Uri getFacebookLoginUri();

    /**
     * Performs Logout of user and returns whether or not the action was successful
     */
    boolean logout();

    /**
     * Performs Logout of user asynchronously
     */
    void logoutInBackground(NetworkCallback<Boolean> callback);

    /**
     * Returns account data of user
     */
    com.leanengine.LeanAccount getAccountData();

    /**
     * Returns authentication token of user
     */
    String getAuthToken();
}
