package rest.o.gram.data_favorites.results;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/23/13
 */
public abstract class CommitDataOperationResult {
    public CommitDataOperationResult(boolean hasSucceded)  {
        this.hasSucceded = hasSucceded;
    }

    public boolean hasSucceded() {
        return hasSucceded;
    }

    private boolean hasSucceded;
}
