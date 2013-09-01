package rest.o.gram.service.InstagramServices;

import com.google.gson.Gson;
import org.jinstagram.Instagram;
import org.jinstagram.entity.users.feed.MediaFeed;
import rest.o.gram.service.InstagramServices.Entities.EmptyMediaFeed;
import rest.o.gram.utils.InstagramUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 8/31/13
 */
public class GetRecentMediaByLocationServlet extends BaseInstagramServlet<MediaFeed> {

    /**
     * Executes the instagram request
     * @param request client request to be translated into an instagram request
     * @param instagram instagram client
     * @return the result of the instagram request
     * @throws java.io.IOException thrown when an IO error occurs
     */
    @Override
    protected MediaFeed executeInstagramRequest(final HttpServletRequest request,
                                                final Instagram instagram) throws IOException {
        final long locationId =  Long.parseLong(request.getReader().readLine());
        log.info(String.format("getRecentMediaByLocation : %d", locationId));
        return instagram.getRecentMediaByLocation(locationId);
    }

    /**
     * Handles a successful instagram request's result
     * @param response the response to return
     * @param result the instagram request's response
     * @throws IOException thrown when an IO error occurs
     */
    @Override
    protected void onRequestSucceded(final HttpServletResponse response,
                                     final MediaFeed result) throws IOException {
        MediaFeed actualResult;
        if (InstagramUtils.isNullOrEmpty(result))
        {
            actualResult = new EmptyMediaFeed();
            log.info("no media feed found");
        }
        else
        {
            actualResult = result;
            log.info(String.format("got media"));
        }
        response.getWriter().write(new Gson().toJson(actualResult));
        response.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * Handles a failed instagram request's response
     * @param response the response to return
     * @param e an error that occured
     * @throws IOException thrown when an IO error occurs
     */
    @Override
    protected void onRequestFailed(final HttpServletResponse response,
                                   final IOException e) throws IOException {
        log.severe(String.format("search for media has failed %s", e.getMessage()));
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "error while contacting instagram");
    }

    private static final Logger log = Logger.getLogger(GetRecentMediaByLocationServlet.class.getName());
}
