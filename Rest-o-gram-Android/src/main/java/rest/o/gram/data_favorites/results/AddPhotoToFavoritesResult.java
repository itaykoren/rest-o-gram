package rest.o.gram.data_favorites.results;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/23/13
 */
public class AddPhotoToFavoritesResult extends PhotoCommitOperationResult {
    public AddPhotoToFavoritesResult(boolean hasSucceeded, String photoId) {
        super(hasSucceeded, photoId);
    }
}
