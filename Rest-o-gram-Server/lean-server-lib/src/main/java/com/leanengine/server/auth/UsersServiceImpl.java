package com.leanengine.server.auth;

import com.leanengine.server.JsonUtils;
import rest.o.gram.lean.LeanAccount;
import rest.o.gram.lean.UsersService;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 9/8/13
 */
public class UsersServiceImpl implements UsersService {

    /**
     *  Retrieves the current acount's data(as a JSON string)
     */
    @Override
    public String getCurrentAccountData() {
        final LeanAccount account = AuthService.getCurrentAccount();
        return leanAccountToJson(account);
    }

    /**
     *  Logs out frfom the current session
     */
    @Override
    public boolean logout() {
        if (AuthService.getCurrentAccount() == null)
            return false;
        else
        {
            AuthService.resetCurrentAuthData();
            return true;
        }
    }

    private static String leanAccountToJson(final LeanAccount account) {
        try
        {
            return JsonUtils.getObjectMapper().writeValueAsString(account);
        } catch (IOException e)
        {
            return "{}";
        }
    }
}
