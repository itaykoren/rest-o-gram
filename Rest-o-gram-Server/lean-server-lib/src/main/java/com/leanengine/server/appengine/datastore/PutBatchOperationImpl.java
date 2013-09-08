package com.leanengine.server.appengine.datastore;

import com.google.appengine.api.datastore.Entity;
import com.leanengine.server.LeanException;
import com.leanengine.server.appengine.DatastoreUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 6/12/13
 */
public class PutBatchOperationImpl implements PutBatchOperation {
    @Override
    public PutBatchOperationImpl addEntity(String kind, String name) {
        if (idToEntityMapping.containsKey(name))
            return null; //TODO: handle
        final Entity entity = new Entity(kind, name);
        idToEntityMapping.put(name, entity);
        return this;
    }

    @Override
    public PutBatchOperationImpl addEntityProperty(String name, String prop, Object value) {
        return addEntityProperty(name, prop, value, true);
    }

    @Override
    public PutBatchOperationImpl addEntityUnindexedProperty(String name, String prop, Object value) {
        return addEntityProperty(name, prop, value, false);
    }

    private PutBatchOperationImpl addEntityProperty(String name, String prop, Object value, boolean indexed) {
        if (!idToEntityMapping.containsKey(name))
            return null;

        if (indexed)
            idToEntityMapping.get(name).setProperty(prop, value);
        else
            idToEntityMapping.get(name).setUnindexedProperty(prop, value);
        return this;
    }

    @Override
    public List<Entity> getEntities() {
        return new ArrayList<Entity>(idToEntityMapping.values());
    }

    @Override
    public Entity getEntity(String name) {
        return idToEntityMapping.get(name);
    }

    @Override
    public boolean execute(PutStrategy strategy) {
        try
        {
            return DatastoreUtils.endPutBatch(this, strategy);
        }
        catch (LeanException e)
        {
            log.severe("batch put operation has failed. code:" + e.getErrorCode());
            return false;
        }
    }

    private Map<String,Entity> idToEntityMapping = new HashMap<>();
    private static final Logger log = Logger.getLogger(PutBatchOperationImpl.class.getName());
}
