package rest.o.gram.authentication;

import android.content.Context;
import android.net.Uri;
import com.leanengine.LeanEngine;
import rest.o.gram.lean.LeanAccount;

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
        return LeanEngine.isUserLoggedIn();
    }

    @Override
    public Uri getFacebookLoginUri() {
        return LeanEngine.getFacebookLoginUri();
    }

    @Override
    public LeanAccount getAccountData() {
        return account;
    }

    @Override
    public void setAccountData(LeanAccount account) {
        this.account = account;
    }

    @Override
    public void resetAuthData() {
        this.account = null;
        LeanEngine.resetAuthToken();
    }

    @Override
    public String getAuthToken() {
        return LeanEngine.getAuthToken();
    }

    private LeanAccount account = null;
}
