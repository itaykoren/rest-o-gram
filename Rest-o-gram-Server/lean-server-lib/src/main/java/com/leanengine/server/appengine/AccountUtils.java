package com.leanengine.server.appengine;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.leanengine.server.auth.AuthToken;
import com.leanengine.server.auth.LeanAccount;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AccountUtils {
    public static Key getAccountKey(long accountID) {
        if (accountID <= 0) return null;
         return KeyFactory.createKey(accountsKind, accountID);
    }

    public static LeanAccount getAccount(long accountID) {
        if (accountID <= 0) return null;
        Entity accountEntity;
        try {
            accountEntity = datastore.get(getAccountKey(accountID));
        } catch (EntityNotFoundException e) {
            return null;
        }

        return toLeanAccount(accountEntity);
    }

    public static LeanAccount findAccountByProvider(String providerID, String provider) {
        if (providerID == null) {
            log.severe("Empty providerID. Can not find account without providerID.");
            return null;
        }
        Query query = new Query(accountsKind);
        final Query.Filter providerIdFilter =
                new Query.FilterPredicate("_provider_id", Query.FilterOperator.EQUAL, providerID);
        final Query.Filter providerFilter =
                new Query.FilterPredicate("_provider", Query.FilterOperator.EQUAL, provider);
        final Query.Filter filter =
                Query.CompositeFilterOperator.and(providerIdFilter, providerFilter);
        query.setFilter(filter);
        PreparedQuery pq = datastore.prepare(query);

        Entity accountEntity = pq.asSingleEntity();

        return (accountEntity == null) ? null : toLeanAccount(accountEntity);
    }

    public static LeanAccount findAccountByEmail(String email, String provider) {
        if (email == null) {
            log.severe("Empty email. Can not find account without email.");
            return null;
        }
        Query query = new Query(accountsKind);
        final Query.Filter mailFilter =
                new Query.FilterPredicate("email", Query.FilterOperator.EQUAL, email);
        final Query.Filter providerFilter =
                new Query.FilterPredicate("_provider", Query.FilterOperator.EQUAL, provider);
        final Query.Filter filter =
                Query.CompositeFilterOperator.and(mailFilter, providerFilter);
        query.setFilter(filter);
        PreparedQuery pq = datastore.prepare(query);

        Entity accountEntity = pq.asSingleEntity();

        return (accountEntity == null) ? null : toLeanAccount(accountEntity);
    }

    public static AuthToken getAuthToken(String token) {
        Entity tokenEntity = (Entity)getMemcacheService().get(token);
        if (tokenEntity != null)
            return createAuthToken(token, tokenEntity);

        try
        {
            tokenEntity = datastore.get(KeyFactory.createKey(authTokenKind, token));
        } catch (EntityNotFoundException e)
        {
            return null;
        }

        return createAuthToken(token, tokenEntity);
    }

    private static AuthToken createAuthToken(String token, Entity tokenEntity) {
        return new AuthToken(
                token,
                (Long) tokenEntity.getProperty("account"),
                (Long) tokenEntity.getProperty("time")
        );
    }

    public static void saveAuthToken(AuthToken authToken) {
        Entity tokenEntity = new Entity(authTokenKind, authToken.token);
        tokenEntity.setUnindexedProperty("account", authToken.accountID);
        tokenEntity.setUnindexedProperty("time", authToken.timeCreated);
        datastore.put(tokenEntity);
        getMemcacheService().put(authToken.token, tokenEntity);
    }

    public static void removeAuthToken(String token) {
        getMemcacheService().delete(token);
        datastore.delete(KeyFactory.createKey(authTokenKind, token));
    }

    public static void saveAccount(LeanAccount leanAccount) {
        Entity accountEntity;

        // Is it a new LeanAccount? They do not have 'id' yet.
        if (leanAccount.id <= 0) {
            // create account
            accountEntity = new Entity(accountsKind);
        } else {
            // update account
            accountEntity = new Entity(accountsKind, leanAccount.id);
        }

        accountEntity.setProperty("_provider_id", leanAccount.providerId);
        accountEntity.setProperty("_provider", leanAccount.provider);
        accountEntity.setProperty("_nickname", leanAccount.nickName);
        for (Map.Entry<String, Object> property : leanAccount.providerProperties.entrySet()) {
            // properties must not start with underscore - this is reserved for system properties
            accountEntity.setProperty(property.getKey(), property.getValue());
        }
        Key accountKey = datastore.put(accountEntity);
        leanAccount.id = accountKey.getId();
    }

    public static LeanAccount toLeanAccount(Entity entity) {

        Map<String, Object> props = new HashMap<>(entity.getProperties().size() - 3);
        for (Map.Entry<String, Object> entityProp : entity.getProperties().entrySet()) {
            if(!entityProp.getKey().startsWith("_"))
                props.put(entityProp.getKey(), entityProp.getValue());
        }

        return new LeanAccount(
                entity.getKey().getId(),
                (String) entity.getProperty("_nickname"),
                (String) entity.getProperty("_provider_id"),
                (String) entity.getProperty("_provider"),
                props
        );
    }

    private static MemcacheService getMemcacheService() {
        if (cache != null)
            return cache;
        cache = MemcacheServiceFactory.getMemcacheService();
        cache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.WARNING));
        return  cache;
    }

    private static MemcacheService cache = null;
    private static final Logger log = Logger.getLogger(AccountUtils.class.getName());
    private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    private static final String authTokenKind = "_auth_tokens";
    private static final String accountsKind = "_accounts";
}
