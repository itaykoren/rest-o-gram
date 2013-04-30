package rest.o.gram.results;

import com.google.gson.annotations.SerializedName;
import rest.o.gram.entities.RestogramVenue;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 4/22/13
 */
public class VenueResult {

    public VenueResult() {}

    public VenueResult(RestogramVenue venue) {
        this.venue = venue;
    }

    public RestogramVenue getResult() {
        return venue;
    }

    @SerializedName("venue")
    private RestogramVenue venue;
}
