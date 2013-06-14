package rest.o.gram.authentication;

import android.content.Context;
import android.net.Uri;
import com.leanengine.*;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/17/13
 */
public class AuthenticationProvider implements IAuthenticationProvider {

    public AuthenticationProvider(Context context, String baseHostname) {
        LeanEngine.init(context, baseHostname);
    }

    @Override
    public boolean isUserLoggedIn() {
        return LeanAccount.isUserLoggedIn();
    }

    @Override
    public Uri getFacebookLoginUri() {
        return LeanEngine.getFacebookLoginUri();
    }

    @Override
    public boolean logout() {
        boolean result = false;
        account = null;
        try {
            result = LeanAccount.logout();
        } catch (LeanException e) {
            // TODO
        }
        return result;
    }

    @Override
    public void logoutInBackground(NetworkCallback<Boolean> callback) {
        account = null;
        LeanAccount.logoutInBackground(callback);
    }

    @Override
    public LeanAccount getAccountData() {
        if(account == null) {
            try {
                account = LeanAccount.getAccountData();
            } catch (LeanException e) {
                // Empty
            }
        }

        return account;
    }

    @Override
    public String getAuthToken() {
        return LeanEngine.getAuthToken();
    }

    private LeanAccount account = null;
}
