package rest.o.gram.authentication;

import android.content.Context;
import android.net.Uri;
import com.leanengine.LeanAccount;
import com.leanengine.LeanEngine;
import com.leanengine.LeanException;
import com.leanengine.NetworkCallback;

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
    public boolean logout() throws LeanException {
        return LeanAccount.logout();
    }

    @Override
    public void logoutInBackground(NetworkCallback<Boolean> callback) {
        LeanAccount.logoutInBackground(callback);
    }

    @Override
    public LeanAccount getAccountData() throws LeanException {
        return LeanAccount.getAccountData();
    }
}
