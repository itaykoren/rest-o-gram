package rest.o.gram;

import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.gson.Gson;
import rest.o.gram.service.InstagramServices.IInstagramRequestFactory;
import rest.o.gram.service.InstagramServices.InstagramDistributedRequestFactory;

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

    public static <T> T parallelFrontendInstagramRequest(final Defs.Instagram.RequestType requestType,
                                                  final PrepareRequest prepareRequest,
                                                  final java.lang.Class<T> resultType) {
        return parallelInstagramRequest(requestType, prepareRequest, resultType, true);
    }

    public static <T> T parallelBackendInstagramRequest(final Defs.Instagram.RequestType requestType,
                                                         final PrepareRequest prepareRequest,
                                                         final java.lang.Class<T> resultType) {
        return parallelInstagramRequest(requestType, prepareRequest, resultType, false);
    }

    private static <T> T parallelInstagramRequest(final Defs.Instagram.RequestType requestType,
                                                  final PrepareRequest prepareRequest,
                                                  final java.lang.Class<T> resultType,
                                                  boolean isFrontend) {

        int workersCount = 0;
        int workersOffset = 0;
        if (isFrontend) {
            workersCount = Defs.Instagram.FRONTEND_ACCESS_WORKERS_AMOUNT;
            workersOffset = 1;
        }
        else {
            workersCount = Defs.Instagram.BACKEND_ACCESS_WORKERS_AMOUNT;
            workersCount = Defs.Instagram.FRONTEND_ACCESS_WORKERS_AMOUNT + 1;
        }

        final IInstagramRequestFactory requestFactory =
                new InstagramDistributedRequestFactory(workersCount, workersOffset);

        final Future<HTTPResponse> firstRequest = sendRequest(requestType, prepareRequest, requestFactory);
        log.info("instagram first request sent");

        final Future<HTTPResponse> secondRequest = sendRequest(requestType, prepareRequest, requestFactory);
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
                                                    final PrepareRequest prepareRequest,
                                                    final IInstagramRequestFactory requestFactory) {
        final HTTPRequest req = requestFactory.createInstagramRequest(requestType);
        req.setPayload(prepareRequest.getPayload());
        log.info(String.format("sending request to: %s", req.getURL().toString()));
        return fetchService.fetchAsync(req);
    }

    private static final Logger log = Logger.getLogger(InstagramAccessManager.class.getName());
    private static final URLFetchService fetchService = URLFetchServiceFactory.getURLFetchService();
}