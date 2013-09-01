package rest.o.gram;

import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.gson.Gson;
import org.jinstagram.Instagram;
import org.jinstagram.entity.media.MediaInfoFeed;
import org.jinstagram.exceptions.InstagramException;
import rest.o.gram.credentials.Credentials;
import rest.o.gram.credentials.ICredentialsFactory;
import rest.o.gram.credentials.RandomCredentialsFactory;
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.service.InstagramServices.IInstagramRequestFactory;
import rest.o.gram.service.InstagramServices.InstagramCentralRequestFactory;
import rest.o.gram.service.InstagramServices.InstagramDistributedRequestFactory;
import rest.o.gram.utils.InstagramUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 6/16/13
 */
public final class InstagramAccessManager {

    public static RestogramPhoto getPhoto(final String id, final String originVenueId)  {
        String mediaId;
        try
        {
            mediaId = InstagramUtils.extractMediaId(id);
        }
        catch (Exception e)
        {
            log.severe("cannot extract media id from media feed string id");
            return null;
        }

        MediaInfoFeed mediaInfo = null;
        try
        {
            Credentials credentials = credentialsFactory.createInstagramCredentials();
            log.info("instagram credentials type = " + credentials.getType());
            Instagram instagram = new Instagram(credentials.getClientId());
            mediaInfo = instagram.getMediaInfo(mediaId);
        }
        catch (InstagramException e)
        {
            log.warning("first get photo has failed, retry");
            try
            {
                Credentials credentials = credentialsFactory.createInstagramCredentials();
                log.info("instagram credentials type = " + credentials.getType());
                Instagram instagram = new Instagram(credentials.getClientId());
                mediaInfo = instagram.getMediaInfo(mediaId);
            }
            catch (InstagramException e2)
            {
                log.severe("second get photo has failed");
                return null;
            }
        }

        return ApisConverters.convertToRestogramPhoto(mediaInfo.getData(), originVenueId);
    }

    public static <T> T parallelInstagramRequest(final Defs.Instagram.RequestType requestType,
                                                 final PrepareRequest prepareRequest,
                                                 final java.lang.Class<T> resultType) {
        final Future<HTTPResponse> firstRequest = sendRequest(requestType, prepareRequest);
        log.info("instagram first request sent");

        final Future<HTTPResponse> secondRequest = sendRequest(requestType, prepareRequest);
        log.info("instagram second request sent");

        while (!firstRequest.isDone() && !secondRequest.isDone()) { }
        log.info("one of the requests is done!");
        Future<HTTPResponse> done = null;
        Future<HTTPResponse> other = null;
        if (firstRequest.isDone())
        {
            log.info("first operation is done");
            done = firstRequest;
            other = secondRequest;
        }
        else // if (secondRequest.isDone())
        {
            log.info("second operation is done");
            done = secondRequest;
            other = firstRequest;
        }

        HTTPResponse resp = null;
        try
        {
            if (done.isCancelled())
            {
                log.info("request has been cacncelled");
                resp = other.get(Defs.Instagram.REQUESTS_TIMEOUT, Defs.Instagram.REQUESTS_TIMEOUT_UNIT);
            }
            else
            {
                log.info("request has been successful");
                resp = done.get(Defs.Instagram.REQUESTS_TIMEOUT, Defs.Instagram.REQUESTS_TIMEOUT_UNIT);
                other.cancel(false);
                log.info("got instagram response");
            }
            if (resp.getResponseCode() != HttpServletResponse.SC_OK)
            {
                log.warning("error while executing an instagarm request : " + resp.getResponseCode());
                return null;
            }
        }
        catch (ExecutionException|InterruptedException|TimeoutException e)
        {
            log.severe(String.format("instagram request has failed - error: %s", e.getMessage()));
            return null;
        }

        log.info("getting instagram result");
        return new Gson().fromJson(new String(resp.getContent()), resultType);
    }

    public  abstract static class PrepareRequest {
        public abstract byte[] getPayload();
    }

    private static Future<HTTPResponse> sendRequest(final Defs.Instagram.RequestType requestType,
                                                    final PrepareRequest prepareRequest) {
        final HTTPRequest req = requestFactory.createInstagramRequest(requestType);
        req.setPayload(prepareRequest.getPayload());
        return fetchService.fetchAsync(req);
    }

    private static final Logger log = Logger.getLogger(InstagramAccessManager.class.getName());
    private static final ICredentialsFactory credentialsFactory = new RandomCredentialsFactory();
    private static final IInstagramRequestFactory requestFactory = new InstagramDistributedRequestFactory();
    private static final URLFetchService fetchService = URLFetchServiceFactory.getURLFetchService();
}