package rest.o.gram.tasks.results;

import rest.o.gram.entities.RestogramPhoto;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 4/22/13
 */
public interface GetPhotosResult {
    RestogramPhoto[] getPhotos();
    String getToken();
    boolean hasMorePhotos();
}
