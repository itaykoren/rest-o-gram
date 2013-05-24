package rest.o.gram.authentication;

import android.net.Uri;
import com.leanengine.NetworkCallback;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/17/13
 */
public interface IAuthenticationProvider {
    boolean isUserLoggedIn();
    Uri getFacebookLoginUri();
    boolean logout() throws com.leanengine.LeanException;
    void logoutInBackground(NetworkCallback<Boolean> callback);
    com.leanengine.LeanAccount getAccountData() throws com.leanengine.LeanException;
    String getAuthToken();
}
