package rest.o.gram.service.InstagramServices;

import com.google.appengine.api.urlfetch.FetchOptions;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import rest.o.gram.Defs;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 8/31/13
 */
public class InstagramDistributedRequestFactory implements IInstagramRequestFactory {
    @Override
    public HTTPRequest createInstagramRequest(Defs.Instagram.RequestType requestType) {
        URL url;
        try
        {
            final String baseHostName = String.format(Defs.Transport.HELPERS_HOST_NAME_SCHEME, getNextServiceNumber());
            final String urlString = String.format("%s/%s",  baseHostName,  requestType.getType());
            url = new URL(urlString);
        } catch (MalformedURLException e)
        {
            log.severe("cannnt build url for requests. error: " + e.getMessage());
            return null;
        }
        return new HTTPRequest(url, HTTPMethod.POST, FetchOptions.Builder.withDeadline(Defs.Instagram.REQUESTS_TIMEOUT));

    }

    private int getNextServiceNumber()
    {
        return random.nextInt(Defs.Instagram.ACCESS_SERVICES_AMOUNT) + 1;
    }

    private Random random = new Random();
    private static final Logger log = Logger.getLogger(InstagramDistributedRequestFactory.class.getName());
}
