package com.leanengine.server.appengine.datastore;

import com.google.appengine.api.datastore.Entity;
import com.leanengine.server.appengine.DatastoreUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        if (!idToEntityMapping.containsKey(name))
            return null;

        idToEntityMapping.get(name).setProperty(prop, value);
        return this;
    }

    @Override
    public Collection<Entity> getEntities() {
        return idToEntityMapping.values();
    }

    @Override
    public boolean execute() {
        return DatastoreUtils.endPutBatch(this);
    }

    private Map<String,Entity> idToEntityMapping = new HashMap<>();
}
