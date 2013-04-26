package rest.o.gram.results;

import com.google.gson.annotations.SerializedName;
import rest.o.gram.entities.RestogramVenue;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 4/22/13
 */
public class VenuesResult {

    public VenuesResult() {}

    public VenuesResult(RestogramVenue[] venues){
        this.venues = venues;
    }

    public RestogramVenue[] getResult() {
        return venues;
    }

    @SerializedName("venues")
    private RestogramVenue[] venues;
}
