package rest.o.gram.data_favorites.results;

import rest.o.gram.entities.RestogramVenue;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/23/13
 */
public class RemoveFavoriteVenuesResult extends VenueCommitOperationResult {
    public RemoveFavoriteVenuesResult(boolean hasSucceded, RestogramVenue venue) {
        super(hasSucceded, venue);
    }
}
