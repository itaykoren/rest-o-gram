package com.leanengine.server.appengine.datastore;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 6/14/13
 */
public class PutUpdateStrategy implements PutStrategy {
    @Override
    public List<Entity> merge(final PutBatchOperation putOp, final Map<Key,Entity> existingEntities) {
        log.info("merging entities with existing");
        // combines updated entities
        final List<Entity> newEntites = putOp.getEntities();
        final List<Entity> updatedEntities = new ArrayList<>(newEntites.size() + existingEntities.size());
        updatedEntities.addAll(existingEntities.values());
        for (final Entity currNewEntity : newEntites)
        {
            if  (!existingEntities.containsKey(currNewEntity.getKey()))
            {
                log.info("new entity: " + currNewEntity.getKey().getName());
                updatedEntities.add(currNewEntity);
            }
            else // if exists - updates properties
            {
                log.info("existing entity: " + currNewEntity.getKey().getName());
                // does not override exisitng props
                final Entity existingEntity = existingEntities.get(currNewEntity.getKey());
                for (final String currExistingPropName : existingEntity.getProperties().keySet())
                {
                    if (currNewEntity.hasProperty(currExistingPropName))
                    {
                        log.info("proprty " + currExistingPropName + "already exists, will not override");
                        currNewEntity.removeProperty(currExistingPropName);
                    }
                }

                //existingEntity.setPropertiesFrom(currNewEntity);
                for (final String currNewProp : currNewEntity.getProperties().keySet())
                    existingEntity.setProperty(currNewProp, currNewEntity.getProperty(currNewProp));
            }
        }

        return updatedEntities;
    }

    private static final Logger log = Logger.getLogger(PutUpdateStrategy.class.getName());
}