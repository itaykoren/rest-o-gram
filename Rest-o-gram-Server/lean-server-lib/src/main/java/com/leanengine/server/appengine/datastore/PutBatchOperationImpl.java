package com.leanengine.server.appengine.datastore;

import com.google.appengine.api.datastore.Entity;
import com.leanengine.server.appengine.DatastoreUtils;

import java.util.*;

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
    public List<Entity> getEntities() {
        return new ArrayList<Entity>(idToEntityMapping.values());
    }

    @Override
    public Entity getEntity(String name) {
        return idToEntityMapping.get(name);
    }

    @Override
    public boolean execute(PutStrategy strategy) {
        return DatastoreUtils.endPutBatch(this, strategy);
    }

    private Map<String,Entity> idToEntityMapping = new HashMap<>();
}
