package com.leanengine.server.appengine;

import com.google.appengine.api.datastore.*;
import com.leanengine.server.LeanException;
import com.leanengine.server.auth.AuthService;
import com.leanengine.server.auth.LeanAccount;
import com.leanengine.server.entity.*;

import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class DatastoreUtils {

    private static final Logger log = Logger.getLogger(DatastoreUtils.class.getName());

    private static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private static Pattern pattern = Pattern.compile("^[A-Za-z][A-Za-z_0-9]*");

    public static Entity getPrivateEntity(String kind, long entityId) throws LeanException {
        LeanAccount account = findCurrentAccount();

        if (entityId <= 0 || kind == null) throw new LeanException(LeanException.Error.EntityNotFound,
                " Entity 'kind' and 'id' must NOT be null.");

        Entity entity;
        try {
            final Key accountKey = AccountUtils.getAccountKey(account.id);
            entity = datastore.get(KeyFactory.createKey(accountKey, kind, entityId));
        } catch (EntityNotFoundException e) {
            throw new LeanException(LeanException.Error.EntityNotFound);
        }

//        if (account.id != (Long) entity.getProperty("_account"))
//                  throw new LeanException(LeanException.Error.NotAuthorized,
//                          " Account not authorized to access entity '" + kind + "'with ID '" + entityId + "'");

        return entity;
    }

    public static Entity getPublicEntity(String kind, String entityName) throws LeanException {
        if (entityName == null || kind == null) throw new LeanException(LeanException.Error.EntityNotFound,
                " Entity 'kind' and 'name' must NOT be null.");

        Entity entity;
        try {
            entity = datastore.get(KeyFactory.createKey(kind, entityName));
        } catch (EntityNotFoundException e) {
            throw new LeanException(LeanException.Error.EntityNotFound);
        }

        return entity;
    }

    public static void deletePrivateEntity(String entityKind, long entityId) throws LeanException {
        LeanAccount account = findCurrentAccount();

        if (entityId <= 0 || entityKind == null) throw new LeanException(LeanException.Error.EntityNotFound,
                " Entity 'kind' and 'id' must NOT be null.");

        Entity entity;
        try {
            final Key accountKey = AccountUtils.getAccountKey(account.id);
            entity = datastore.get(KeyFactory.createKey(accountKey, entityKind, entityId));
        } catch (EntityNotFoundException e) {
            throw new LeanException(LeanException.Error.EntityNotFound);
        }

//        if (account.id != (Long) entity.getProperty("_account"))
//            throw new LeanException(LeanException.Error.NotAuthorized,
//                    " Account not authorized to access entity '" + entityKind + "'with ID '" + entityId + "'");

        datastore.delete(entity.getKey());
    }

    public static void deletePrivateEntity(String entityKind, String entityName) throws LeanException {
        if (entityName == null || entityKind == null) throw new LeanException(LeanException.Error.EntityNotFound,
                " Entity 'kind' and 'name' must NOT be null.");

        Entity entity;
        try {
            entity = datastore.get(KeyFactory.createKey(entityKind, entityName));
        } catch (EntityNotFoundException e) {
            throw new LeanException(LeanException.Error.EntityNotFound);
        }

        datastore.delete(entity.getKey());
    }

    private static LeanAccount findCurrentAccount() throws LeanException {
        LeanAccount account = AuthService.getCurrentAccount();
        // this should not happen, but we check anyway
        if (account == null) throw new LeanException(LeanException.Error.NotAuthorized);
        return account;
    }

    public static List<Entity> getPrivateEntities() throws LeanException {
        findCurrentAccount();

        List<String> kindNames = findAllEntityKinds();

        List<Entity> result = new ArrayList<Entity>();

        for (String kindName : kindNames) {
            result.addAll(getPrivateEntities(kindName));
        }

        return result;
    }

    public static List<Entity> getPrivateEntities(String kind) throws LeanException {

        if (!pattern.matcher(kind).matches()) {
            throw new LeanException(LeanException.Error.IllegalEntityKeyFormat);
        }

        LeanAccount account = AuthService.getCurrentAccount();
        // this should not happen, but we check anyway
        if (account == null) throw new LeanException(LeanException.Error.NotAuthorized);

        final Key accountKey = getCurrentAccountKey();
        Query query = new Query(kind, accountKey);
        //query.setFilter(new Query.FilterPredicate("_account", Query.FilterOperator.EQUAL, account.id));
        PreparedQuery pq = datastore.prepare(query);

        return pq.asList(FetchOptions.Builder.withDefaults());
    }

    public static Collection<Entity> getPublicEntities(String kind, String[] uniqueNames) throws LeanException {

        if (!pattern.matcher(kind).matches()) {
            throw new LeanException(LeanException.Error.IllegalEntityKeyFormat);
        }

        List<Key> keys = new ArrayList<>(uniqueNames.length);
        for (String currName : uniqueNames)
            keys.add(KeyFactory.createKey(kind, currName));

        Map<Key,Entity> result = datastore.get(keys);
        return result.values();
    }

    public static long putPrivateEntity(String kind, Map<String, Object> properties) throws LeanException {

        if (!pattern.matcher(kind).matches()) {
            throw new LeanException(LeanException.Error.IllegalEntityKeyFormat);
        }

        final Key accountKey = getCurrentAccountKey();
        Entity entityEntity = new Entity(kind, accountKey);
        //entityEntity.setProperty("_account", AuthService.getCurrentAccount().id);

        if (properties != null) {
            for (Map.Entry<String, Object> entry : properties.entrySet()) {
                entityEntity.setProperty(entry.getKey(), entry.getValue());
            }
        }
        Key result = datastore.put(entityEntity);
        return result.getId();
    }

    public static void putPublicEntity(String kind, String name, Map<String, Object> properties) throws LeanException {

        if (!pattern.matcher(kind).matches()) {
            throw new LeanException(LeanException.Error.IllegalEntityKeyFormat);
        }

        Entity entityEntity = new Entity(kind, name);

        if (properties != null) {
            for (Map.Entry<String, Object> entry : properties.entrySet()) {
                entityEntity.setProperty(entry.getKey(), entry.getValue());
            }
        }
        datastore.put(entityEntity);
    }

    public static QueryResult queryEntityPrivate(LeanQuery leanQuery) throws LeanException {
        LeanAccount account = findCurrentAccount();

        final Key accountKey = getCurrentAccountKey();
        Query query = new Query(leanQuery.getKind(), accountKey);
        //query.setFilter(new Query.FilterPredicate("_account", Query.FilterOperator.EQUAL, account.id));

        return queryEntity(leanQuery, query);
    }

    public static QueryResult queryEntityPublic(LeanQuery leanQuery) throws LeanException {
        Query query = new Query(leanQuery.getKind());
        return queryEntity(leanQuery, query);
    }

    private static QueryResult queryEntity(LeanQuery leanQuery, Query query) throws LeanException {
        for (QueryFilter queryFilter : leanQuery.getFilters()) {
            query.setFilter(new Query.FilterPredicate(
                    queryFilter.getProperty(),
                    queryFilter.getOperator().getFilterOperator(),
                    queryFilter.getValue()));
        }

        for (QuerySort querySort : leanQuery.getSorts()) {
            query.addSort(querySort.getProperty(), querySort.getDirection().getSortDirection());
        }

        FetchOptions fetchOptions = FetchOptions.Builder.withDefaults();
        if(leanQuery.getCursor() != null )
            fetchOptions.startCursor(leanQuery.getCursor());
        if(leanQuery.getOffset() != null)
            fetchOptions.offset(leanQuery.getOffset());
        if(leanQuery.getLimit() != null)
            fetchOptions.limit(leanQuery.getLimit());

        try {
            PreparedQuery pq = datastore.prepare(query);

            QueryResultList<Entity> result;
            result = pq.asQueryResultList(fetchOptions);

            return new QueryResult(result, result.getCursor());
        } catch (DatastoreNeedIndexException dnie) {
            throw new LeanException(LeanException.Error.AppEngineMissingIndex);
        }
    }

    public static List<String> findAllEntityKinds() throws LeanException {

        Query q = new Query(Entities.KIND_METADATA_KIND );

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery pq = datastore.prepare(q);

        List<Entity> list = pq.asList(FetchOptions.Builder.withDefaults());

        List<String> result = new ArrayList<String>();
        for (Entity entity : list) {
            if (!entity.getKey().getName().startsWith("_"))
                result.add(entity.getKey().getName());
        }

        return result;
    }

    private static Key getCurrentAccountKey() {
        return AccountUtils.getAccountKey(AuthService.getCurrentAccount().id);
    }
}