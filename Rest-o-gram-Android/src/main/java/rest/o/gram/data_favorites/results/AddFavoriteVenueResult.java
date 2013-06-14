package rest.o.gram.data_favorites.results;

import rest.o.gram.entities.RestogramVenue;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/23/13
 */
public class AddFavoriteVenueResult extends VenueCommitOperationResult {
    public AddFavoriteVenueResult(boolean hasSucceded, String venueId) {
        super(hasSucceded, venueId);
    }
}
