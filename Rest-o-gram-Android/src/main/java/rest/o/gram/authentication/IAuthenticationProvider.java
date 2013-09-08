package rest.o.gram.authentication;

import android.net.Uri;
import com.leanengine.NetworkCallback;
import rest.o.gram.lean.LeanAccount;

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
     * Returns account data of user
     */
    LeanAccount getAccountData();

    /**
     * Sets the current account
     */
    void setAccountData(LeanAccount account);

    /**
     * Resets authentication data
     */
    void resetAuthData();

    /**
     * Returns authentication token of user
     */
    String getAuthToken();
}
