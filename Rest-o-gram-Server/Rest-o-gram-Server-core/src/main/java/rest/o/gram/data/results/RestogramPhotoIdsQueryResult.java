package rest.o.gram.data.results;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.Entity;
import com.leanengine.server.entity.QueryResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 9/7/13
 */
public class RestogramPhotoIdsQueryResult {
    public RestogramPhotoIdsQueryResult(final QueryResult queryResult) {
        if (queryResult != null && queryResult.getResult() != null)
        {
            cursor = queryResult.getCursor();
            final List<Entity> entities = queryResult.getResult();
            result = new ArrayList<>(entities.size());
            for (final Entity currEntity : entities)
                result.add(currEntity.getKey().getName());
        }
    }

    public List<String> getResult() {
        return result;
    }

    public Cursor getCursor() {
        return cursor;
    }

    private List<String> result;
    private Cursor cursor;
}