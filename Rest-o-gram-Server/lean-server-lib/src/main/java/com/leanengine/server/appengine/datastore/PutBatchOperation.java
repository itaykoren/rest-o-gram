package com.leanengine.server.appengine.datastore;

import com.google.appengine.api.datastore.Entity;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 6/12/13
 */
// TODO: use a similar form as a better API for all DatastoreUtils operations -revise
public interface PutBatchOperation {
    PutBatchOperation addEntity(String kind, String name);
    PutBatchOperation addEntityProperty(String name, String  prop, Object value);
    PutBatchOperation addEntityUnindexedProperty(String name, String  prop, Object value);
    List<Entity> getEntities();
    Entity getEntity(String name);
    boolean execute(PutStrategy strategy);
}
