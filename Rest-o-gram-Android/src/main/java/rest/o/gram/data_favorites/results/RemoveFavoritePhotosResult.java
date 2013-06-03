package rest.o.gram.data_favorites.results;

import rest.o.gram.entities.RestogramPhoto;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/23/13
 */
public class RemoveFavoritePhotosResult extends PhotoCommitOperationResult {
    public RemoveFavoritePhotosResult(boolean hasSucceded, RestogramPhoto photo) {
        super(hasSucceded, photo);
    }
}
