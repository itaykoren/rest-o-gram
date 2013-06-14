package rest.o.gram.data_favorites.results;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/23/13
 */
public class RemoveVenueFromFavoritesResult extends VenueCommitOperationResult {
    public RemoveVenueFromFavoritesResult(boolean hasSucceeded, String venueId) {
        super(hasSucceeded, venueId);
    }
}
