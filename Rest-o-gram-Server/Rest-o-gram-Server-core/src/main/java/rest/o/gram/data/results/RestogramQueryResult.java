package rest.o.gram.data.results;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 6/15/13
 */
public interface RestogramQueryResult<T> {
    List<T> getResult();
    String getToken();
}
