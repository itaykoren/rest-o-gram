package rest.o.gram.service.InstagramServices;

import com.google.gson.Gson;
import org.jinstagram.Instagram;
import org.jinstagram.entity.locations.LocationSearchFeed;
import rest.o.gram.service.InstagramServices.Entities.EmptyLocationSearchFeed;
import rest.o.gram.utils.InstagramUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 8/17/13
 */
public class GetLocationServlet extends BaseInstagramServlet<LocationSearchFeed> {

    /**
     * Executes the instagram request
     * @param request client request to be translated into an instagram request
     * @param instagram instagram client
     * @return the result of the instagram request
     * @throws IOException thrown when an IO error occurs
     */
    @Override
    protected LocationSearchFeed executeInstagramRequest(final HttpServletRequest request,
                                                         final Instagram instagram) throws IOException {
        final String venueID = request.getReader().readLine();
        log.info(String.format("searchFoursquareVenue : %s", venueID));
        return instagram.searchFoursquareVenue(venueID);
    }

    /**
     * Handles a successful instagram request's result
     * @param response the response to return
     * @param result the instagram request's response
     * @throws IOException thrown when an IO error occurs
     */
    @Override
    protected void onRequestSucceded(final HttpServletResponse response,
                                     final LocationSearchFeed result) throws IOException {
        LocationSearchFeed actualResult;
        if (InstagramUtils.isNullOrEmpty(result))
        {
           actualResult = new EmptyLocationSearchFeed();
           log.info("no venue was found");
        }
        else
        {
            actualResult = result;
            log.info(String.format("found venue : %d", actualResult.getLocationList().get(0).getId()));
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
        log.severe(String.format("search for venue has failed %s", e.getMessage()));
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "error while contacting instagram");
    }

    private static final Logger log = Logger.getLogger(GetLocationServlet.class.getName());
}