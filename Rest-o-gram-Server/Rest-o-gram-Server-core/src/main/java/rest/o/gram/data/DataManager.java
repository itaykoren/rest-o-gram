package rest.o.gram.data;

import com.google.appengine.api.datastore.Entity;
import com.leanengine.server.LeanException;
import com.leanengine.server.appengine.DatastoreUtils;
import rest.o.gram.entities.Kinds;
import rest.o.gram.entities.Props;

import java.util.*;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 6/10/13
 */
public class DataManager {

    public static Map<String,Boolean> getPhotoToRuleMapping(final String... ids) throws LeanException {
        final Collection<Entity> photoMetas =
                DatastoreUtils.getPublicEntities(Kinds.PHOTO_META, ids);
        final Map<String,Boolean> result = new HashMap<>(photoMetas.size());
        for (final Entity currPhotoMeta : photoMetas)
        {
            final String currPhotoId = currPhotoMeta.getKey().getName();
            final boolean currApproval = (boolean)currPhotoMeta.getProperty(Props.PhotoMeta.APPROVED);
            result.put(currPhotoId, currApproval);
        }
        return result;
    }

    // TODO: must refactor DS put access...
    public static void savePhotoToRuleMapping(final String venueId, final Map<String,Boolean> photoIdToRuleMapping) {
        Collection<Entity> exisitngEntites = null;
        final String[] keys = new String[photoIdToRuleMapping.size()];
        photoIdToRuleMapping.keySet().toArray(keys);
        try
        {
            exisitngEntites = DatastoreUtils.getPublicEntities(Kinds.PHOTO_META, keys);
        } catch (LeanException e) {
            e.printStackTrace();
            return;
        }

        Set<String> existingKeys = new HashSet<>();
        for (final Entity currEntity : exisitngEntites)
        {
            final String currName = currEntity.getKey().getName();
            existingKeys.add(currName);
            currEntity.setProperty(Props.PhotoMeta.APPROVED, photoIdToRuleMapping.get(currName));
        }

        final List<Entity> updatedEntities = new ArrayList<>(photoIdToRuleMapping.size());
        updatedEntities.addAll(exisitngEntites);
        for (final Map.Entry<String, Boolean> currEntry : photoIdToRuleMapping.entrySet())
        {
            final String currKey = currEntry.getKey();
            if  (!existingKeys.contains(currKey))
            {
                final Entity newEntity = new Entity(Kinds.PHOTO_META, currKey);
                newEntity.setProperty(Props.PhotoMeta.VENUE_ID, venueId);
                newEntity.setProperty(Props.PhotoMeta.APPROVED, currEntry.getValue());
                updatedEntities.add(newEntity);
            }
        }

        DatastoreUtils.putPublicEntities(updatedEntities);
    }

    private static final Logger log = Logger.getLogger(DataManager.class.getName());
}
