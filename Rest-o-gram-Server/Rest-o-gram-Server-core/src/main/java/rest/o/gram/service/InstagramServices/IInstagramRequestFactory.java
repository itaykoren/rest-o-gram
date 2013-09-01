package rest.o.gram.service.InstagramServices;

import com.google.appengine.api.urlfetch.HTTPRequest;
import rest.o.gram.Defs;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 8/31/13
 */
public interface IInstagramRequestFactory {
    HTTPRequest createInstagramRequest(Defs.Instagram.RequestType requestType);
}
