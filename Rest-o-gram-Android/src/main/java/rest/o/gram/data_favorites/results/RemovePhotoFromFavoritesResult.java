package rest.o.gram.data_favorites.results;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/23/13
 */
public class RemovePhotoFromFavoritesResult extends PhotoCommitOperationResult {
    public RemovePhotoFromFavoritesResult(boolean hasSucceeded, String photoId) {
        super(hasSucceeded, photoId);
    }
}
