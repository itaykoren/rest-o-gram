package rest.o.gram.data_favorites.results;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/23/13
 */
public class RemoveFavoritePhotoResult extends PhotoCommitOperationResult {
    public RemoveFavoritePhotoResult(boolean hasSucceded, String photoId) {
        super(hasSucceded, photoId);
    }
}
