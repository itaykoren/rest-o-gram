package rest.o.gram.data;

import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.entities.RestogramVenue;
import rest.o.gram.results.PhotosResult;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 6/1/14
 */
public interface DataManager {
    Map<String,Boolean> getPhotoToRuleMapping(RestogramPhoto... data);

    Map<String,Boolean> getPhotoToRuleMapping(String... ids);

    boolean savePhotoToRuleMapping(Map<RestogramPhoto, Boolean> photoToRuleMapping);

    PhotosResult fetchPhotosFromCache(String venueId, String token);

    Map<String,RestogramVenue> fetchVenuesFromCache(String[] ids);

    boolean cacheVenue(RestogramVenue venue);

    boolean isValidCursor(String token);

    boolean updatePhotoReference(String photoId, boolean isFav);

    boolean changePhotoYummiesCount(String photoId, int delta);

    Set<String> fetchFavoritePhotoIds();

    PhotosResult queryFavoritePhotos(String token);

    boolean cachePhoto(RestogramPhoto photo);

    boolean isPhotoInCache(String photoId);

    boolean isPhotoPending(String photoId);

    RestogramPhoto getPendingPhoto(String photoId);

    void addPendingPhotos(Map<String, RestogramPhoto> pendingPhotos);

    void removePendingPhotos(Collection<String> photoIds);
}
