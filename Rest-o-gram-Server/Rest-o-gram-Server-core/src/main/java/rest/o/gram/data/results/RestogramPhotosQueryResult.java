package rest.o.gram.data.results;

import com.google.appengine.api.datastore.Entity;
import com.leanengine.server.entity.QueryResult;
import rest.o.gram.DataStoreConverters;
import rest.o.gram.entities.RestogramPhoto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 6/15/13
 */
public class RestogramPhotosQueryResult implements RestogramQueryResult<RestogramPhoto> {
    public RestogramPhotosQueryResult(final QueryResult queryResult) {
        if (queryResult != null && queryResult.getResult() != null)
        {
            token = queryResult.getCursor().toWebSafeString();
            final List<Entity> entities = queryResult.getResult();
            result = new ArrayList<>(entities.size());
            for (final Entity currEntity : entities)
                result.add(DataStoreConverters.entityToPhoto(currEntity));
        }
    }

    public RestogramPhotosQueryResult(final List<RestogramPhoto> result, final String token) {
        this.result = result;
        this.token = token;
    }

    @Override
    public List<RestogramPhoto> getResult() {
        return result;
    }

    @Override
    public String getToken() {
        return token;
    }

    private List<RestogramPhoto> result;
    private String token;
}
