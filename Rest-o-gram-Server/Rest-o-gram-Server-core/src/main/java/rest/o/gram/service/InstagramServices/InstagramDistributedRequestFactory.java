package rest.o.gram.service.InstagramServices;

import com.google.appengine.api.urlfetch.FetchOptions;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import rest.o.gram.Defs;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 8/31/13
 */
public class InstagramDistributedRequestFactory implements IInstagramRequestFactory {
    /**
     * Creates a new {@link InstagramDistributedRequestFactory}
     * @param helperWorkersCount the count of available helper workers.
     * @param counterOffset the offset being added to the serial number of helper worker to access it.
     */
    public InstagramDistributedRequestFactory(final int helperWorkersCount, final int counterOffset) {
        m_helperWorkersCount = helperWorkersCount;
        m_counterOffset = counterOffset;
    }

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
        return new HTTPRequest(url, HTTPMethod.POST,
                FetchOptions.Builder.withDeadline(Defs.Instagram.REQUESTS_TIMEOUT).doNotFollowRedirects());

    }

    private int getNextServiceNumber()
    {
        int current = -1;

        // makes sure that history doesn't repeat itself =]
        do {
            current = random.nextInt(m_helperWorkersCount) + m_counterOffset;
        } while (m_history.contains(current));
        m_history.add(current);
        return current;
    }

    private Random random = new Random();
    private final int m_helperWorkersCount;
    private final int m_counterOffset;
    private Set<Integer> m_history = new HashSet<>();
    private static final Logger log =
            Logger.getLogger(InstagramDistributedRequestFactory.class.getName());
}
