package rest.o.gram.tasks.results;

import rest.o.gram.entities.RestogramPhoto;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/24/13
 */
public interface FetchPhotosFromCacheResult {
    List<RestogramPhoto> getPhotos();
}
