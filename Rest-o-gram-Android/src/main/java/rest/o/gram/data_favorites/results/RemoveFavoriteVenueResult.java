package rest.o.gram.data_favorites.results;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/23/13
 */
public class RemoveFavoriteVenueResult extends VenueCommitOperationResult {
    public RemoveFavoriteVenueResult(boolean hasSucceded, String venueId) {
        super(hasSucceded, venueId);
    }
}