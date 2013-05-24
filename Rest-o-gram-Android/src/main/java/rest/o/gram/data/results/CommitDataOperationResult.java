package rest.o.gram.data.results;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/23/13
 */
public class CommitDataOperationResult {
    public CommitDataOperationResult(boolean hasSucceded)  {
        this.hasSucceded = hasSucceded;
    }

    public boolean hasSucceded() {
        return hasSucceded;
    }

    private boolean hasSucceded;
}
