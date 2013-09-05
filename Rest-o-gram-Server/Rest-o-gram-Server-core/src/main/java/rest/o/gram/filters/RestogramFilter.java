package rest.o.gram.filters;

import rest.o.gram.entities.RestogramPhoto;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Itay
 * Date: 21/04/13
 */
public interface RestogramFilter {

    List<RestogramPhoto> doFilter(List<RestogramPhoto> data);
}
