package rest.o.gram.data_favorites.results;

import rest.o.gram.entities.RestogramVenue;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 6/3/13
 */
public abstract class VenueCommitOperationResult extends CommitDataOperationResult {
    public VenueCommitOperationResult(boolean hasSucceded, RestogramVenue venue) {
        super(hasSucceded);
        this.venue = venue;
    }

    public RestogramVenue getVenue() {
        return venue;
    }

    private RestogramVenue venue;
}
