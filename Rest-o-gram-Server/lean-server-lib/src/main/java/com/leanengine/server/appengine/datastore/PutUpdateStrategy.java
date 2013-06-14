package com.leanengine.server.appengine.datastore;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 6/14/13
 */
public class PutUpdateStrategy implements PutStrategy {
    @Override
    public List<Entity> merge(final PutBatchOperation putOp, final Map<Key,Entity> existingEntities) {
        // updates existing entities
        for (final Map.Entry<Key,Entity> currEntry : existingEntities.entrySet())
        {
            final String currName = currEntry.getKey().getName();
            currEntry.getValue().setPropertiesFrom(putOp.getEntity(currName));
        }

        // combine new entities
        final List<Entity> newEntites = putOp.getEntities();
        final List<Entity> updatedEntities = new ArrayList<>(newEntites.size());
        updatedEntities.addAll(existingEntities.values());
        for (final Entity currEntity : newEntites)
        {
            if  (!existingEntities.containsKey(currEntity.getKey().getName()))
                updatedEntities.add(currEntity);
        }

        return updatedEntities;
    }
}