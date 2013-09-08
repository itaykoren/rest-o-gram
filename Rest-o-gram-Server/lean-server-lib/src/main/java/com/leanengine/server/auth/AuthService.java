package com.leanengine.server.auth;

import com.leanengine.server.LeanException;
import com.leanengine.server.appengine.AccountUtils;
import com.leanengine.server.appengine.ServerUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class AuthService {

    private static final Logger log = Logger.getLogger(AuthService.class.getName());

    private static final ThreadLocal<String> tlAuthToken = new ThreadLocal<>();
    private static final ThreadLocal<LeanAccount> tlLeanAccount = new ThreadLocal<>();

    public static boolean startAuthSession(String token) {
        LeanAccount user = getAccountByToken(token);
        if  (user != null)
        {
            tlAuthToken.set(token);
            tlLeanAccount.set(user);
            return true;
        }
        return false;
    }

    public static void finishAuthSession() {
        tlLeanAccount.remove();
        tlAuthToken.remove();
    }

//    public static AuthToken createMockFacebookAccount(String email) {
//        if(!ServerUtils.isDevServer()){
//            throw new IllegalStateException("Method 'createMockFacebookAccount(email)' should only be called while running Dev Server.");
//        }
//
//        LeanAccount account = AccountUtils.findAccountByEmail(email, "fb-oauth");
//        if (account == null) {
//            //todo this is one-to-one mapping between Account and User
//            //change this in the future
//
//            Map<String, Object> props = new HashMap<String, Object>(1);
//            props.put("email", email);
//
//            // account does not yet exist - create it
//            account = new LeanAccount(
//                    0,
//                    email,
//                    UUID.randomUUID().toString(),
//                    "fb-oauth",
//                    props);
//            AccountUtils.saveAccount(account);
//        }
//
//        // create our own authentication token
//        // todo retrieve existing token if not expired
//        return AuthService.createAuthToken(account.id);
//    }

    private static LeanAccount getAccountByToken(String authToken) {

        AuthToken savedToken = AccountUtils.getAuthToken(authToken);
        if (savedToken == null)
            return null;
        LeanAccount user = AccountUtils.getAccount(savedToken.accountID);
        if (user == null)
            return null;

        return user;
    }

    public static void resetCurrentAuthData() {
        String token = tlAuthToken.get();
        try
        {
            if (token != null)
                AccountUtils.removeAuthToken(token);
        }
        catch (LeanException e)
        {
            log.warning("deletion of auth token has failed. code:" + e.getErrorCode());
            return;
        }
        tlLeanAccount.remove();
        tlAuthToken.remove();
    }

    public static AuthToken createAuthToken(long accountID) {
        AuthToken authToken = new AuthToken(accountID);
        try
        {
            AccountUtils.saveAuthToken(authToken);
        }
        catch (LeanException e)
        {
            log.severe("cannot create auth token. error:" + e.getErrorCode());
            return null;
        }
        return authToken;
    }

    public static LeanAccount getCurrentAccount() {
        return tlLeanAccount.get();
    }

    public static boolean isUserLoggedIn() {
        return tlAuthToken.get() != null;
    }
}
