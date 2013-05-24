package rest.o.gram.tasks.results;

import rest.o.gram.entities.RestogramVenue;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/24/13
 */
public interface FetchVenuesFromCacheResult {
    List<RestogramVenue> getVenues();
}
