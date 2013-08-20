package rest.o.gram.service;

import com.google.gson.Gson;
import org.jinstagram.Instagram;
import org.jinstagram.entity.locations.LocationSearchFeed;
import org.jinstagram.exceptions.InstagramException;
import rest.o.gram.credentials.Credentials;
import rest.o.gram.credentials.ICredentialsFactory;
import rest.o.gram.credentials.RandomCredentialsFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 8/17/13
 */
public class GetInstagramLocationServlet extends HttpServlet {

    public GetInstagramLocationServlet() {
        try
        {
            m_factory = new RandomCredentialsFactory();
        } catch (Exception e)
        {
            log.severe("an error occurred while initializing the service");
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final String venueID = request.getReader().readLine();

        LocationSearchFeed locationSearchFeed = null;
        try
        {
            final Credentials credentials = m_factory.createInstagramCredentials();
            log.info("instagram credentials type = " + credentials.getType());
            final Instagram instagram = new Instagram(credentials.getClientId());
            log.info(String.format("searchFoursquareVenue : [%s]", venueID));
            locationSearchFeed = instagram.searchFoursquareVenue(venueID);
            response.getWriter().write(new Gson().toJson(locationSearchFeed));
            log.info(String.format("found venue : [%s]-[%s]", venueID, locationSearchFeed.getLocationList().get(0).getId()));
        } catch (InstagramException e)
        {
            log.severe(String.format("search for venue: [%s] has failed [%s]", venueID, e.toString()));
            response.sendError(500, "error while contacting instagram");
        }
    }

    private static final Logger log = Logger.getLogger(RestogramServiceImpl.class.getName());
    private ICredentialsFactory m_factory;
}