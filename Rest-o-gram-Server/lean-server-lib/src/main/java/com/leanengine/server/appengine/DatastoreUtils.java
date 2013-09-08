package com.leanengine.server.appengine;

import com.google.appengine.api.datastore.*;
import com.leanengine.server.LeanException;
import com.leanengine.server.appengine.datastore.PutBatchOperation;
import com.leanengine.server.appengine.datastore.PutBatchOperationImpl;
import com.leanengine.server.appengine.datastore.PutStrategy;
import com.leanengine.server.auth.AuthService;
import com.leanengine.server.entity.LeanQuery;
import com.leanengine.server.entity.QueryFilter;
import com.leanengine.server.entity.QueryResult;
import com.leanengine.server.entity.QuerySort;
import rest.o.gram.lean.LeanAccount;

import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class DatastoreUtils {

    private static final Logger log = Logger.getLogger(DatastoreUtils.class.getName());
    private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private static final Pattern pattern = Pattern.compile("^[A-Za-z][A-Za-z_0-9]*");

    public static Entity getPrivateEntity(String kind, String entityName) throws LeanException {
        return getPrivateEntity(kind, entityName, false);
    }

    public static Entity getPrivateEntity(String kind, String entityName, boolean isInTransaction) throws LeanException {
        LeanAccount account = findCurrentAccount();

        if (entityName == null || kind == null) throw new LeanException(LeanException.Error.EntityNotFound,
                " Entity 'kind' and 'name' must NOT be null.");

        final Key accountKey = AccountUtils.getAccountKey(account.id);
        if (accountKey == null)
            throw new LeanException(LeanException.Error.NotAuthorized);

        final Key key = KeyFactory.createKey(accountKey, kind, entityName);
        return doGetEntitySafe(key, isInTransaction);
    }

    public static Entity getPublicEntity(String kind, String entityName) throws LeanException {
        return getPublicEntity(kind, entityName, false);
    }

    public static Entity getPublicEntity(String kind, String entityName, boolean isInTransaction) throws LeanException {
        if (entityName == null || kind == null) throw new LeanException(LeanException.Error.EntityNotFound,
                " Entity 'kind' and 'name' must NOT be null.");

        final Key key = KeyFactory.createKey(kind, entityName);
        return doGetEntitySafe(key, isInTransaction);
    }

    private static Entity doGetEntitySafe(Key key, boolean isInTransaction) throws LeanException {
        Entity entity;
        try
        {
            entity = datastore.get(key);
        } catch (EntityNotFoundException e)
        {
            throw new LeanException(LeanException.Error.EntityNotFound);
        }
        catch (DatastoreTimeoutException e)
        {
            // in transaction - no retry
            if (isInTransaction)
            {
                log.warning("a recoverable data store error has occured while in transaction, delegates");
                throw new LeanException(LeanException.Error.RecoverableDataStoreError);
            }

            log.warning("a recoverable data store error has occured, retries");
            // non transaction - retry
            try
            {
                entity = datastore.get(key);
            }
            catch (Exception|Error e2)
            {
                log.severe("a fatal data store error has occured, cannot retry");
                throw new LeanException(LeanException.Error.FatalDataStoreError);
            }
        }
        catch (Exception|Error e)
        {
            log.severe("a fatal data store error has occured, cannot retry");
            throw new LeanException(LeanException.Error.FatalDataStoreError);
        }

        return entity;
    }

    private static LeanAccount findCurrentAccount() throws LeanException {
        LeanAccount account = AuthService.getCurrentAccount();
        // this should not happen, but we check anyway
        if (account == null)
            throw new LeanException(LeanException.Error.NotAuthorized);
        return account;
    }

    public static Collection<Entity> getPrivateEntities(final String kind) throws LeanException {
        return getPrivateEntities(kind, false);
    }

    public static Collection<Entity> getPrivateEntities(final String kind, boolean isInTransaction) throws LeanException {
        return getPrivateEntities(kind, null, isInTransaction);
    }

    public static Collection<Entity> getPrivateEntities(final String kind, final String[] uniqueNames)
            throws LeanException {
        return getPrivateEntities(kind, uniqueNames, false);
    }

    public static Collection<Entity> getPrivateEntities(final String kind, final String[] uniqueNames, boolean isInTransaction)
            throws LeanException {

        if (!pattern.matcher(kind).matches())
            throw new LeanException(LeanException.Error.IllegalEntityKeyFormat);

        final LeanAccount account = AuthService.getCurrentAccount();
        // this should not happen, but we check anyway
        if (account == null)
            throw new LeanException(LeanException.Error.NotAuthorized);

        final Key accountKey = getCurrentAccountKey();
        if (accountKey == null)
            throw new LeanException(LeanException.Error.NotAuthorized);

        if (uniqueNames == null || uniqueNames.length == 0)
        {
            final Query query = new Query(kind, accountKey);
            PreparedQuery pq = datastore.prepare(query);

            try
            {
                return pq.asList(FetchOptions.Builder.withDefaults());
            }
            catch (DatastoreTimeoutException e)
            {
                // in transaction - no retry
                if (isInTransaction)
                {
                    log.warning("a recoverable data store error has occured while in transaction, delegates");
                    throw new LeanException(LeanException.Error.RecoverableDataStoreError);
                }

                log.warning("a recoverable data store error has occured, retries");
                // non transaction - retry
                try
                {
                    return pq.asList(FetchOptions.Builder.withDefaults());
                }
                catch (Exception|Error e2)
                {
                    log.severe("a fatal data store error has occured, cannot retry");
                    throw new LeanException(LeanException.Error.FatalDataStoreError);
                }
            }
            catch (Exception|Error e)
            {
                log.severe("a fatal data store error has occured, cannot retry");
                throw new LeanException(LeanException.Error.FatalDataStoreError);
            }
        }
        else
        {
           final List<Key> keys = new ArrayList<>(uniqueNames.length);
            for (final String currName : uniqueNames)
                keys.add(KeyFactory.createKey(accountKey, kind, currName));

            return doGetEntitiesSafe(keys, isInTransaction).values();
        }
    }

    public static Collection<Entity> getPublicEntities(String kind, String[] uniqueNames)
            throws LeanException {
        return getPublicEntities(kind, uniqueNames, false);
    }

    public static Collection<Entity> getPublicEntities(String kind, String[] uniqueNames, boolean isInTransaction)
            throws LeanException {

        if (!pattern.matcher(kind).matches())
            throw new LeanException(LeanException.Error.IllegalEntityKeyFormat);

        List<Key> keys = new ArrayList<>(uniqueNames.length);
        for (String currName : uniqueNames)
            keys.add(KeyFactory.createKey(kind, currName));

        return doGetEntitiesSafe(keys, isInTransaction).values();
    }

    private static Map<Key,Entity> doGetEntitiesSafe(List<Key> keys, boolean isInTransaction) throws LeanException {
        Map<Key,Entity> result = null;
        try
        {
            result = datastore.get(keys);
        }
        catch (DatastoreTimeoutException e)
        {
            // in transaction - no retry
            if (isInTransaction)
            {
                log.warning("a recoverable data store error has occured while in transaction, delegates");
                throw new LeanException(LeanException.Error.RecoverableDataStoreError);
            }

            log.warning("a recoverable data store error has occured, retries");
            // non transaction - retry
            try
            {
                result = datastore.get(keys);
            }
            catch (Exception|Error e2)
            {
                log.severe("a fatal data store error has occured, cannot retry");
                throw new LeanException(LeanException.Error.FatalDataStoreError);
            }
        }
        catch (Exception|Error e)
        {
            log.severe("a fatal data store error has occured, cannot retry");
            throw new LeanException(LeanException.Error.FatalDataStoreError);
        }
        return result;
    }

    public static void putPrivateEntity(String kind, String name,
                                        Map<String, PropertyDescription> properties)
            throws LeanException {
        putPrivateEntity(kind, name, properties, false);
    }

    public static void putPrivateEntity(String kind, String name,
                                        Map<String, PropertyDescription> properties, boolean isInTransaction)
                                    throws LeanException {
        if (!pattern.matcher(kind).matches()) {
            throw new LeanException(LeanException.Error.IllegalEntityKeyFormat);
        }

        final Key accountKey = getCurrentAccountKey();
        Entity entityEntity = new Entity(kind, name, accountKey);

        if (properties != null)
        {
            for (final Map.Entry<String, PropertyDescription> entry : properties.entrySet())
            {
                if (entry.getValue().indexed)
                    entityEntity.setProperty(entry.getKey(), entry.getValue().value);
                else
                    entityEntity.setUnindexedProperty(entry.getKey(), entry.getValue().value);
            }
        }

        doPutEntitySafe(isInTransaction, entityEntity);
    }

    public static void putPublicEntity(Entity entityEntity)
            throws LeanException {
        putPublicEntity(entityEntity, false);
    }

    public static void putPublicEntity(Entity entityEntity, boolean isInTransaction)
            throws LeanException {
        doPutEntitySafe(isInTransaction, entityEntity);
    }

    public static void putPublicEntity(String kind, String name, Map<String, PropertyDescription> properties)
                                        throws LeanException {
        putPublicEntity(kind, name, properties, false);
    }

    public static void putPublicEntity(String kind, String name, Map<String, PropertyDescription> properties,
                                       boolean isInTransaction)
                                            throws LeanException {

        if (!pattern.matcher(kind).matches()) {
            throw new LeanException(LeanException.Error.IllegalEntityKeyFormat);
        }

        Entity entityEntity = new Entity(kind, name);

        if (properties != null)
        {
            for (final Map.Entry<String, PropertyDescription> entry : properties.entrySet())
            {
                if (entry.getValue().indexed)
                    entityEntity.setProperty(entry.getKey(), entry.getValue().value);
                else
                    entityEntity.setUnindexedProperty(entry.getKey(), entry.getValue().value);
            }
        }
        doPutEntitySafe(isInTransaction, entityEntity);
    }

    public static PutBatchOperation startPutBatch() {
        return new PutBatchOperationImpl();
    }

    public static boolean endPutBatch(final PutBatchOperation operation, final PutStrategy strategy) throws LeanException{
        return endPutBatch(operation, strategy, false);
    }

    public static boolean endPutBatch(final PutBatchOperation operation, final PutStrategy strategy,
                                      boolean isInTransaction) throws LeanException{
        if (operation == null ||  operation.getEntities() == null)
            return false;

        final List<Key> keys = new ArrayList<Key>(entitiesToKeys(operation.getEntities()));
        final Map<Key,Entity> existing = doGetEntitiesSafe(keys,isInTransaction);
        final List<Entity> merged = strategy.merge(operation, existing);
        doPutEntitySafe(isInTransaction, merged.toArray(new Entity[]{}));
        datastore.put(merged);
        return true;
    }

    private static void doPutEntitySafe(boolean isInTransaction, Entity... entityEntity) throws LeanException {
        final List<Entity> entities = Arrays.asList(entityEntity);
        try
        {
            datastore.put(entities);
        }
        catch (DatastoreTimeoutException |ConcurrentModificationException e)
        {
            // in transaction - no retry
            if (isInTransaction)
            {
                log.warning("a recoverable data store error has occured while in transaction, delegates");
                throw new LeanException(LeanException.Error.RecoverableDataStoreError);
            }

            log.warning("a recoverable data store error has occured, retries");
            // non transaction - retry
            try
            {
                datastore.put(entities);
            }
            catch (Exception|Error e2)
            {
                log.severe("a fatal data store error has occured, cannot retry");
                throw new LeanException(LeanException.Error.FatalDataStoreError);
            }
        }
        catch (Exception|Error e)
        {
            log.severe("a fatal data store error has occured, cannot retry");
            throw new LeanException(LeanException.Error.FatalDataStoreError);
        }
    }

    public static QueryResult queryEntityPrivate(LeanQuery leanQuery) throws LeanException {
        return queryEntityPrivate(leanQuery, false);
    }

    public static QueryResult queryEntityPrivate(LeanQuery leanQuery, boolean isInTransaction) throws LeanException {
        final Key accountKey = getCurrentAccountKey();
        final Query query = new Query(leanQuery.getKind(), accountKey);

        return queryEntity(leanQuery, query, isInTransaction);
    }

    public static QueryResult queryEntityPublic(LeanQuery leanQuery) throws LeanException {
        return queryEntityPublic(leanQuery, false);
    }

    public static QueryResult queryEntityPublic(LeanQuery leanQuery, boolean isInTransaction) throws LeanException {
        Query query = new Query(leanQuery.getKind());
        return queryEntity(leanQuery, query, isInTransaction);
    }

    public static Transaction buildTransaction() {
        return buildTransaction(TransactionOptions.Builder.withDefaults());
    }

    public static Transaction buildTransaction(TransactionOptions options) {
        return datastore.beginTransaction(options);
    }

    public static class PropertyDescription {

        public PropertyDescription(Object value, boolean indexed) {
            this.value = value;
            this.indexed = indexed;
        }

        public Object getValue() {
            return value;
        }

        public boolean isIndexed() {
            return indexed;
        }

        private Object value;
        private boolean indexed;
    }

    private static Collection<Key> entitiesToKeys(final Collection<Entity> entities) {
        final Collection<Key> keys = new ArrayList<>(entities.size());
        for (final Entity currEntity : entities)
        {
             keys.add(currEntity.getKey());
        }
        return keys;
    }

    private static QueryResult queryEntity(LeanQuery leanQuery, Query query, boolean isInTransaction) throws LeanException {
        if (leanQuery.getFilters() != null)
        {
            if (leanQuery.getFilters().size() == 1)
            {
                final QueryFilter leanFilter =
                        leanQuery.getFilters().get(0);
                query.setFilter(leanFilterToFilter(leanFilter));
            }
            else //has several filters
            {
                final Collection<Query.Filter> subFilters = new ArrayList<>(leanQuery.getFilters().size());
                for (final QueryFilter leanFilter : leanQuery.getFilters()) {
                    subFilters.add(leanFilterToFilter(leanFilter));
                }
                query.setFilter(Query.CompositeFilterOperator.and(subFilters));
            }
        }
        if (leanQuery.isKeysOnly())
            query.setKeysOnly();

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

        PreparedQuery pq = datastore.prepare(query);
        QueryResultList<Entity> result =  null;
        try
        {
            result = pq.asQueryResultList(fetchOptions);
        } catch (DatastoreNeedIndexException dnie)
        {
            throw new LeanException(LeanException.Error.AppEngineMissingIndex);
        }
        catch (DatastoreTimeoutException e)
        {
            // in transaction - no retry
            if (isInTransaction)
            {
                log.warning("a recoverable data store error has occured while in transaction, delegates");
                throw new LeanException(LeanException.Error.RecoverableDataStoreError);
            }

            log.warning("a recoverable data store error has occured, retries");
            // non transaction - retry
            try
            {
                result = pq.asQueryResultList(fetchOptions);
            }
            catch (Exception|Error e2)
            {
                log.severe("a fatal data store error has occured, cannot retry");
                throw new LeanException(LeanException.Error.FatalDataStoreError);
            }
        }
        catch (Exception|Error e)
        {
            log.severe("a fatal data store error has occured, cannot retry");
            throw new LeanException(LeanException.Error.FatalDataStoreError);
        }
        return new QueryResult(result, result.getCursor());
    }

    private static Query.FilterPredicate leanFilterToFilter(QueryFilter queryFilter) {
        return new Query.FilterPredicate(
                queryFilter.getProperty(),
                queryFilter.getOperator().getFilterOperator(),
                queryFilter.getValue());
    }

    private static Key getCurrentAccountKey() {
        return AccountUtils.getAccountKey(AuthService.getCurrentAccount().id);
    }
}