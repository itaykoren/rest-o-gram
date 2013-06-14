package rest.o.gram.data_favorites.results;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/23/13
 */
public abstract class CommitDataOperationResult {
    public CommitDataOperationResult(boolean hasSucceeded)  {
        this.hasSucceeded = hasSucceeded;
    }

    public boolean hasSucceeded() {
        return hasSucceeded;
    }

    private boolean hasSucceeded;
}
