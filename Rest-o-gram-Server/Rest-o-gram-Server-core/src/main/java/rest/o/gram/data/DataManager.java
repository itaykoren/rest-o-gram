package rest.o.gram.data;

import com.google.appengine.api.datastore.Entity;
import com.leanengine.server.LeanException;
import com.leanengine.server.appengine.DatastoreUtils;
import rest.o.gram.entities.Kinds;
import rest.o.gram.entities.Props;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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
        for (final Entity currPhotoMeta  : photoMetas)
        {
            final String currPhotoId = currPhotoMeta.getKey().getName();
            final boolean currApproval = (boolean)currPhotoMeta.getProperty(Props.PhotoMeta.APPROVED);
            result.put(currPhotoId, currApproval);
        }
        return result;
    }

    private static final Logger log = Logger.getLogger(DataManager.class.getName());
}
