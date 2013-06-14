package rest.o.gram.data_favorites.results;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 6/3/13
 */
public abstract class PhotoCommitOperationResult extends CommitDataOperationResult {
    public PhotoCommitOperationResult(boolean hasSucceded, String photoId) {
        super(hasSucceded);
        this.photoId = photoId;
    }

    public String getPhotoId() {
        return photoId;
    }

    private String photoId;
}
