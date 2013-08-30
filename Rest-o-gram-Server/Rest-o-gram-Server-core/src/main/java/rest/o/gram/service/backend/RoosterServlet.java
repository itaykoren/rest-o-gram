package rest.o.gram.service.backend;

import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import rest.o.gram.Defs;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 8/30/13
 */
public class RoosterServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        handleRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        handleRequest(req, resp);
    }

    private void handleRequest(final HttpServletRequest req, final HttpServletResponse resp) {
        try
        {
            final URLFetchService fetcher = URLFetchServiceFactory.getURLFetchService();
            final URL roosterURL = new URL(Defs.Transport.BASE_HOST_NAME + "/_ah/warmup");
            fetcher.fetch(roosterURL);
            log.info("wake-up call!");
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (IOException e)
        {
            e.printStackTrace();
            log.severe("backend cannot reply. error: " + e.getMessage());
            try
            {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "error while processing");
            }
            catch (IOException e2)
            {
                log.warning("error while trying  to report error to client: " + e2.getMessage());
            }
        }
    }

    private static final Logger log =
            Logger.getLogger(RoosterServlet.class.getName());
}
