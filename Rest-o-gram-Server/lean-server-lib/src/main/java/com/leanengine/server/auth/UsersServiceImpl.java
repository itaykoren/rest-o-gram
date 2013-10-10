package com.leanengine.server.auth;

import com.leanengine.server.JsonUtils;
import rest.o.gram.lean.LeanAccount;
import rest.o.gram.lean.UsersService;

import java.io.IOException;
import java.util.logging.Logger;

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
            log.warning("a logout operarion called when client is not authenticated");
        try
        {
            AuthService.resetCurrentAuthData();
            return true;
        }
        catch (Exception|Error e)
        {
            log.severe("cannot clear current auth data");
            return true;
        }
    }

    private static String leanAccountToJson(final LeanAccount account) {
        if (account  == null)
            return "{}";

        try
        {
            return JsonUtils.getObjectMapper().writeValueAsString(account);
        } catch (Exception|Error e)
        {
            return "{}";
        }
    }

    private static final Logger log = Logger.getLogger(UsersServiceImpl.class.getName());
}
