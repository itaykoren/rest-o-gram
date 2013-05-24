package rest.o.gram.data;

import com.leanengine.LeanQuery;
import rest.o.gram.entities.RestogramPhoto;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/23/13
 */
public class GetFavoritePhotosResult extends GetDataResult<RestogramPhoto> {
    public GetFavoritePhotosResult(List<RestogramPhoto> elements, LeanQuery query) {
        super(elements, query);
    }
}
