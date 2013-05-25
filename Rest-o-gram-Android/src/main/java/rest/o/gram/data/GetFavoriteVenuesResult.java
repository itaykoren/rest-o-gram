package rest.o.gram.data;

import com.leanengine.LeanQuery;
import rest.o.gram.entities.RestogramVenue;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/23/13
 */
public  class GetFavoriteVenuesResult extends GetDataResult<RestogramVenue> {
    public GetFavoriteVenuesResult(List<RestogramVenue> elements, LeanQuery query) {
        super(elements, query);
    }
}
