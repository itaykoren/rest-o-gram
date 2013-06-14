package rest.o.gram.data_favorites.results;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 6/3/13
 */
public abstract class VenueCommitOperationResult extends CommitDataOperationResult {
    public VenueCommitOperationResult(boolean hasSucceded, String venueId) {
        super(hasSucceded);
        this.venueId = venueId;
    }

    public String getVenueId() {
        return venueId;
    }

    private String venueId;
}
