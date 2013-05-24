package rest.o.gram.data;

import com.leanengine.LeanQuery;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/23/13
 */
public class GetDataResult<T> {
    public GetDataResult(List<T> elements, LeanQuery query){
        this.elements = elements;
        this.query = query;
    }

    public List<T> getElements() {
        return elements;
    }

    public boolean hasMore() {
        return query.getCursor() != null;
    }

    LeanQuery getQuery() {
        return query;
    }

    private List<T> elements = null;
    private LeanQuery query;
}
