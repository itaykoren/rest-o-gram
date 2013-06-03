package rest.o.gram.data_favorites.results;

import rest.o.gram.entities.RestogramPhoto;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 6/3/13
 */
public abstract class PhotoCommitOperationResult extends CommitDataOperationResult {
    public PhotoCommitOperationResult(boolean hasSucceded, RestogramPhoto photo) {
        super(hasSucceded);
        this.photo = photo;
    }

    public RestogramPhoto getPhoto() {
        return photo;
    }

    private RestogramPhoto photo;
}
