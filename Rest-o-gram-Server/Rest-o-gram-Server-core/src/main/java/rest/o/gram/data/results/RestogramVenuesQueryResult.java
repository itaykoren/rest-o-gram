package rest.o.gram.data.results;

import rest.o.gram.entities.RestogramVenue;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 6/15/13
 */
public class RestogramVenuesQueryResult implements RestogramQueryResult<RestogramVenue> {
    public RestogramVenuesQueryResult(List<RestogramVenue> result, String token) {
        this.result = result;
        this.token = token;
    }

    @Override
    public List<RestogramVenue> getResult() {
        return result;
    }

    @Override
    public String getToken() {
        return token;
    }

    private List<RestogramVenue> result;
    private String token;
}
