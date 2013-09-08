package rest.o.gram.data;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.leanengine.server.LeanDefs;
import com.leanengine.server.LeanException;
import com.leanengine.server.appengine.DatastoreUtils;
import com.leanengine.server.appengine.datastore.PutBatchOperation;
import com.leanengine.server.appengine.datastore.PutUpdateStrategy;
import com.leanengine.server.entity.LeanQuery;
import com.leanengine.server.entity.QueryFilter;
import com.leanengine.server.entity.QueryResult;
import com.leanengine.server.entity.QuerySort;
import org.apache.commons.lang3.StringUtils;
import rest.o.gram.DataStoreConverters;
import rest.o.gram.data.results.RestogramPhotoIdsQueryResult;
import rest.o.gram.shared.CommonDefs;
import rest.o.gram.entities.Kinds;
import rest.o.gram.entities.Props;
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.entities.RestogramVenue;
import rest.o.gram.results.PhotosResult;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 6/10/13
 */
public final class DataManager {

    // NON-AUTH

    public static Map<String,Boolean> getPhotoToRuleMapping(final RestogramPhoto... data) {
        if (data == null || data.length == 0)
            return null;

        final String[] instaIds = new String[data.length];
        int i = 0;
        for (final RestogramPhoto currPhoto : data)
            instaIds[i++] = currPhoto.getInstagram_id();

        // gets filter rules for photos
        Map<String, Boolean> photoToRule = DataManager.getPhotoToRuleMapping(instaIds);
        if (photoToRule == null)
            log.severe("cannot get photos filter rules");

        return photoToRule;
    }

    public static Map<String,Boolean> getPhotoToRuleMapping(final String... ids) {
        if (ids == null || ids.length == 0)
            return null;

        Collection<Entity> photoEntities;
        try
        {
            photoEntities = DatastoreUtils.getPublicEntities(Kinds.PHOTO, ids);
        }
        catch (LeanException e)
        {
            log.severe("cannot get photos filter rules. code:" + e.getErrorCode());
            return null;
        }

        final Map<String,Boolean> result = new HashMap<>(photoEntities.size());
        for (final Entity currPhotoEntity : photoEntities)
        {
            final String currPhotoId = currPhotoEntity.getKey().getName();
            if (!currPhotoEntity.hasProperty(Props.Photo.APPROVED))
                continue;
            final boolean currApproval = (boolean) currPhotoEntity.getProperty(Props.Photo.APPROVED);
            result.put(currPhotoId, currApproval);
        }
        return result;
    }

    public static boolean savePhotoToRuleMapping(final Map<RestogramPhoto,Boolean> photoToRuleMapping) {
        if (photoToRuleMapping == null || photoToRuleMapping.isEmpty())
            return false;

        final PutBatchOperation putOp = DatastoreUtils.startPutBatch();
        for (final Map.Entry<RestogramPhoto,Boolean> currEntry :  photoToRuleMapping.entrySet())
        {
            final RestogramPhoto currPhoto = currEntry.getKey();
            final String currName = currPhoto.getInstagram_id();
            putOp.addEntity(Kinds.PHOTO, currName);
            putOp.addEntityProperty(currName, Props.Photo.APPROVED, currEntry.getValue());

            putOp.addEntityProperty(currName, Props.Photo.ORIGIN_VENUE_ID, currPhoto.getOriginVenueId());
            putOp.addEntityUnindexedProperty(currName, Props.Photo.CAPTION, currPhoto.getCaption());
            putOp.addEntityUnindexedProperty(currName, Props.Photo.CREATED_TIME, currPhoto.getCreatedTime());
            putOp.addEntityUnindexedProperty(currName, Props.Photo.IMAGE_FILTER, currPhoto.getImageFilter());
            putOp.addEntityUnindexedProperty(currName, Props.Photo.LIKES, currPhoto.getLikes());
            putOp.addEntityUnindexedProperty(currName, Props.Photo.STANDARD_RESOLUTION, currPhoto.getStandardResolution());
            putOp.addEntityUnindexedProperty(currName, Props.Photo.LINK, currPhoto.getLink());
            putOp.addEntityUnindexedProperty(currName, Props.Photo.THUMBNAIL, currPhoto.getThumbnail());
            putOp.addEntityUnindexedProperty(currName, Props.Photo.TYPE, currPhoto.getType());
            putOp.addEntityUnindexedProperty(currName, Props.Photo.USER, currPhoto.getUser());
            putOp.addEntityProperty(currName, Props.Photo.YUMMIES, 0);
        }
        return putOp.execute(new PutUpdateStrategy());
    }

    public static PhotosResult fetchPhotosFromCache(final String venueId, final String token) {
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
            log.severe(String.format("fetching photos from cache has failed. venue:%s, code:%d", venueId, e.getErrorCode()));
        }

        return createPhotosResultFromQueryResult(result);
    }


    public static Map<String,RestogramVenue> fetchVenuesFromCache(final String[] ids) {

        if (ids == null || ids.length == 0)
            return null;

        Collection<Entity> entities = null;
        try
        {
            entities = DatastoreUtils.getPublicEntities(Kinds.VENUE, ids);
        } catch (LeanException e)
        {
            log.severe("fetching venues from cache has failed. code:" + e.getErrorCode());
        }

        if (entities == null)
            return null;

        final Map<String,RestogramVenue> idToVenueMapping = new HashMap<>();
        for (final Entity currEntity : entities)
        {
            final RestogramVenue currrVenue = DataStoreConverters.entityToVenue(currEntity).encodeStrings();
            idToVenueMapping.put(currrVenue.getFoursquare_id(), currrVenue);
        }
        return idToVenueMapping;
    }

    public static boolean cacheVenue(final RestogramVenue venue) {
        if (venue == null)
            return false;

        try
        {
            DatastoreUtils.putPublicEntity(Kinds.VENUE, venue.getFoursquare_id(),
                                           DataStoreConverters.venueToProps(venue));
        } catch (LeanException e)
        {
            log.severe("caching the venue in DS has failed. code:" + e.getErrorCode());
            return false;
        }
        return true;
    }

    public static boolean isValidCursor(String token) {
        try
        {
            Cursor.fromWebSafeString(token);
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }

    // AUTH

    public static boolean updatePhotoReference(final String photoId, final boolean isFav) {

        if (StringUtils.isBlank(photoId))
            return false;

        final Map<String, DatastoreUtils.PropertyDescription> props = new HashMap<>();
        props.put(Props.PhotoRef.IS_FAVORITE, new DatastoreUtils.PropertyDescription(isFav, true));
        try
        {
            DatastoreUtils.putPrivateEntity(Kinds.PHOTO_REFERENCE, photoId, props);
        } catch (LeanException e)
        {
            log.severe("cannot add a photo to favorites. code:" + e.getErrorCode());
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
                    if (e.getErrorCode() != LeanException.Error.RecoverableDataStoreError.errorCode)
                    {
                        log.warning("cannot get photo from DS");
                        transaction.rollback();
                        return false;
                    }
                    throw e;
                }

                long yummies = 0;
                if (photo.hasProperty(Props.Photo.YUMMIES))
                    yummies = (Long)photo.getProperty(Props.Photo.YUMMIES);
                yummies += delta;
                photo.setProperty(Props.Photo.YUMMIES, yummies);
                try
                {
                    DatastoreUtils.putPublicEntity(photo);
                } catch (LeanException e)
                {
                    if (e.getErrorCode() != LeanException.Error.RecoverableDataStoreError.errorCode)
                    {
                        log.severe("cannot put photo in DS");
                        transaction.rollback();
                        return false;
                    }
                    throw e;
                }
                transaction.commit();
                break;
            }
            catch (LeanException e)
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
        return true;
    }

    public static Set<String> fetchFavoritePhotoIds() {
        final LeanQuery query = new LeanQuery(Kinds.PHOTO_REFERENCE);
        query.addFilter(Props.PhotoRef.IS_FAVORITE, QueryFilter.FilterOperator.EQUAL, true);
        QueryResult result = null;
        try
        {
            result = DatastoreUtils.queryEntityPrivate(query);
        } catch (LeanException e)
        {
            log.severe("could not query for fav photos. code:" + e.getErrorCode());
            return null;
        }

        if (result == null || result.getResult() == null)
            return null;
        final List<Entity> resultEntities = result.getResult();
        final Set<String> resultIds = new HashSet<>(resultEntities.size());
        for (final Entity currEntity : resultEntities)
            resultIds.add(currEntity.getKey().getName());
        return resultIds;
    }

    public static PhotosResult queryFavoritePhotos(final String token) {

        final RestogramPhotoIdsQueryResult favIdsResult = queryFavoritePhotoIds(token);

        if (favIdsResult == null || favIdsResult.getResult() == null ||
            favIdsResult.getResult().isEmpty())
            return null;

        Collection<Entity> entities = null;
        try
        {
            entities =
                    DatastoreUtils.getPublicEntities(Kinds.PHOTO,
                                                     favIdsResult.getResult().toArray(new String[]{}));
        }
        catch (LeanException e)
        {
            log.severe("could not query for fav photos. code:" + e.getErrorCode());
            return null;
        }

        final QueryResult queryResult =
                new QueryResult(new ArrayList<>(entities), favIdsResult.getCursor());
        final PhotosResult result = createPhotosResultFromQueryResult(queryResult);
        if (result != null && result.getPhotos() != null)
        {
            for (final RestogramPhoto currPhoto : result.getPhotos())
                currPhoto.set_favorite(true);
        }
        return result;
    }

    private static RestogramPhotoIdsQueryResult queryFavoritePhotoIds(final String token) {

        final LeanQuery query = new LeanQuery(Kinds.PHOTO_REFERENCE);
        query.addFilter(Props.PhotoRef.IS_FAVORITE, QueryFilter.FilterOperator.EQUAL, true);
        query.setKeysOnly();
        if (isValidCursor(token))
            query.setCursor(Cursor.fromWebSafeString(token));
        QueryResult result = null;
        try
        {
            result = DatastoreUtils.queryEntityPrivate(query);
        } catch (LeanException e)
        {
            log.severe("could not query for fav photos. code:" + e.getErrorCode());
            return null;
        }

        return new RestogramPhotoIdsQueryResult(result);
    }

    public static boolean cachePhoto(RestogramPhoto photo) {

    if (photo == null)
        return  false;

    try
    {
        DatastoreUtils.putPublicEntity(Kinds.PHOTO,
                photo.getInstagram_id(), DataStoreConverters.photoToProps(photo));
    }
    catch (LeanException e)
    {
        log.severe("caching the photo in DS has failed. code: " + e.getErrorCode());
        return false;
    }
    return true;
}

    public static boolean isPhotoInCache(final String photoId) {

        if (StringUtils.isBlank(photoId))
            return false;

        final LeanQuery query = new LeanQuery(Kinds.PHOTO);
        query.addFilter(Entity.KEY_RESERVED_PROPERTY, QueryFilter.FilterOperator.EQUAL,
                        KeyFactory.createKey(Kinds.PHOTO, photoId));
        query.setKeysOnly();
        QueryResult result = null;
        try
        {
            result = DatastoreUtils.queryEntityPublic(query);
        } catch (LeanException e)
        {
            log.severe("cannot query for entity existence. code:" + e.getErrorCode());
        }

        return result != null  && result.getResult() != null &&
               !result.getResult().isEmpty();
    }

    private static PhotosResult createPhotosResultFromQueryResult(final QueryResult queryResult) {

        if (queryResult != null && queryResult.getResult() != null)
        {
            final Cursor cursor = queryResult.getCursor();
            String token = null;
            if (!hasMoreResults(queryResult)) // no more results
                token = CommonDefs.Tokens.FINISHED_FETCHING_FROM_CACHE;
            else // has more results
                token = cursor.toWebSafeString();
            final List<Entity> entities = queryResult.getResult();
            final RestogramPhoto[] result = entitiesToRestogramPhotos(entities);

            return new PhotosResult(result, token);
        }

        log.severe("cannot init photos query result");
        return null; // error
    }

    private static RestogramPhoto[] entitiesToRestogramPhotos(final Collection<Entity> entities) {
        if (entities == null)
            return null;
        final RestogramPhoto[] result = new RestogramPhoto[entities.size()];
        int i = 0;
        for (final Entity currEntity : entities)
            result[i++] = (DataStoreConverters.entityToPhoto(currEntity));
        return result;
    }

    private static boolean hasMoreResults(QueryResult queryResult) {
        return queryResult.getCursor() != null &&
               queryResult.getResult().size() == LeanDefs.DataStore.RESULTS_LIMIT;
    }

    // mem-cache

    public static boolean isPhotoPending(final String photoId) {
        return getMemcacheService().get(photoId) !=  null;
    }

    public static RestogramPhoto getPendingPhoto(final String photoId) {
        return (RestogramPhoto)getMemcacheService().get(photoId);
    }

    public static void addPendingPhotos(final Map<String,RestogramPhoto> pendingPhotos) {
        getMemcacheService().putAll(pendingPhotos);
    }

    public static void removePendingPhotos(final Collection<String> photoIds) {
        getMemcacheService().deleteAll(photoIds);
    }

    private static MemcacheService getMemcacheService() {
        if (cache != null)
            return cache;
        cache = MemcacheServiceFactory.getMemcacheService();
        cache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.WARNING));
        return  cache;
    }

    private static final Logger log = Logger.getLogger(DataManager.class.getName());
    private static MemcacheService cache = null;
}
