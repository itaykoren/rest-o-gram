package rest.o.gram.data;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Transaction;
import com.leanengine.server.LeanException;
import com.leanengine.server.appengine.DatastoreUtils;
import com.leanengine.server.appengine.datastore.PutBatchOperation;
import com.leanengine.server.appengine.datastore.PutUpdateStrategy;
import com.leanengine.server.entity.LeanQuery;
import com.leanengine.server.entity.QueryFilter;
import com.leanengine.server.entity.QueryResult;
import com.leanengine.server.entity.QuerySort;
import org.apache.commons.lang3.StringUtils;
import rest.o.gram.data.results.RestogramPhotosQueryResult;
import rest.o.gram.data.results.RestogramQueryResult;
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

    // NON-AUTH

    public static Map<String,Boolean> getPhotoToRuleMapping(final String... ids) throws LeanException {
        final Collection<Entity> photoMetas =
                DatastoreUtils.getPublicEntities(Kinds.PHOTO, ids);
        final Map<String,Boolean> result = new HashMap<>(photoMetas.size());
        for (final Entity currPhotoMeta : photoMetas)
        {
            final String currPhotoId = currPhotoMeta.getKey().getName();
            final boolean currApproval = (boolean)currPhotoMeta.getProperty(Props.Photo.APPROVED);
            result.put(currPhotoId, currApproval);
        }
        return result;
    }

    public static boolean savePhotoToRuleMapping(final Map<String,Boolean> photoIdToRuleMapping) {
        final PutBatchOperation putOp = DatastoreUtils.startPutBatch();
        for (final Map.Entry<String,Boolean> currEntry :  photoIdToRuleMapping.entrySet())
        {
            final String currName = currEntry.getKey();
            putOp.addEntity(Kinds.PHOTO, currName);
            putOp.addEntityProperty(currName, Props.Photo.APPROVED, currEntry.getValue());
        }
        return putOp.execute(new PutUpdateStrategy());
    }

    public static RestogramQueryResult fetchPhotosFromCache(final String venueId, final String token) {
        final LeanQuery query = new LeanQuery(Kinds.PHOTO);
        query.addFilter(Props.Photo.ORIGIN_VENUE_ID, QueryFilter.FilterOperator.EQUAL, venueId);
        query.addFilter(Props.Photo.APPROVED, QueryFilter.FilterOperator.EQUAL, true);
        query.addSort(Props.Photo.YUMMIES, QuerySort.SortDirection.DESCENDING);
        if (StringUtils.isNotBlank(token))
            query.setCursor(Cursor.fromWebSafeString(token));
        QueryResult result = null;
        try
        {
            result = DatastoreUtils.queryEntityPublic(query);
        } catch (LeanException e)
        {
            e.printStackTrace();
            log.severe("fetching photos from cache has failed. venue: " + venueId);
        }
        return new RestogramPhotosQueryResult(result);
    }

    // AUTH

    public static boolean updatePhotoReference(final String photoId, final boolean isFav) {
        final Map<String, Object> props = new HashMap<>();
        props.put(Props.PhotoRef.INSTAGRAM_ID, photoId);
        props.put(Props.PhotoRef.IS_FAVORITE, isFav);
        try {
            DatastoreUtils.putPublicEntity(Kinds.PHOTO_REFERENCE, photoId, props);
        } catch (LeanException e) {
            e.printStackTrace();
            log.severe("cannot add a photo to favorites");
            return false;
        }
        return true;
    }

    public static boolean changePhotoYummiesCount(final String photoId, final int delta) {
        int retries = 2;
        while (true)
        {
            final Transaction transaction = DatastoreUtils.buildTransaction();
            try
            {
                Entity photo  = null;
                try
                {
                    photo = DatastoreUtils.getPublicEntity(Kinds.PHOTO, photoId);
                } catch (LeanException e)
                {
                    e.printStackTrace();
                    log.severe("cannot get photo from DS");
                    transaction.rollback();
                    return false;
                }

                long yummies = (Long)photo.getProperty(Props.Photo.YUMMIES);
                yummies += delta;
                photo.setProperty(Props.Photo.YUMMIES, yummies);
                try
                {
                    DatastoreUtils.putPublicEntity(Kinds.PHOTO, photoId, photo.getProperties());
                } catch (LeanException e)
                {
                    e.printStackTrace();
                    log.severe("cannot put photo in DS");
                    transaction.rollback();
                    return false;
                }
                transaction.commit();
            }
            catch (ConcurrentModificationException e)
            {
                if (retries == 0)
                {
                    log.severe("exceeded the number of allowed retries for yummies count update transaction");
                    return false;
                }

                --retries;
            } finally
            {
                if (transaction.isActive())
                    transaction.rollback();
            }
        }
    }

    public static boolean isPhotoFavorite(final String photoId) {
        final LeanQuery lquery = new LeanQuery(Kinds.PHOTO_REFERENCE);
        lquery.addFilter(Props.PhotoRef.INSTAGRAM_ID, QueryFilter.FilterOperator.EQUAL, photoId);
        lquery.addFilter(Props.PhotoRef.IS_FAVORITE,  QueryFilter.FilterOperator.EQUAL, true);
        QueryResult result = null;
        try
        {
            result = DatastoreUtils.queryEntityPrivate(lquery);
        } catch (LeanException e)
        {
            log.severe("error while getting private photo info from DS");
            e.printStackTrace();
            return false;
        }
        if (result == null || result.getResult().isEmpty())
            return false;
        return true;
    }

    private static final Logger log = Logger.getLogger(DataManager.class.getName());
}
