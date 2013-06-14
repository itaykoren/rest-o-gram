package com.leanengine.server.appengine.datastore;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 6/14/13
 */
public  interface PutStrategy {
    List<Entity> merge(PutBatchOperation putOp, Map<Key,Entity> existingEntities);
}
