package rest.o.gram.service.InstagramServices;

import com.google.appengine.api.urlfetch.FetchOptions;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import rest.o.gram.Defs;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 8/31/13
 */
public class InstagramCentralRequestFactory implements IInstagramRequestFactory {
    @Override
    public HTTPRequest createInstagramRequest(Defs.Instagram.RequestType requestType) {
        URL url;
        try
        {
            url = new URL(String.format("%s/%s", Defs.Transport.BASE_HOST_NAME, requestType.getType()));
        } catch (MalformedURLException e)
        {
            log.severe("cannnt build url for requests. error: " + e.getMessage());
            return null;
        }
        return new HTTPRequest(url, HTTPMethod.POST, FetchOptions.Builder.withDeadline(Defs.Instagram.REQUESTS_TIMEOUT));
    }

    private static final Logger log = Logger.getLogger(InstagramCentralRequestFactory.class.getName());
}
