package rest.o.gram.service.InstagramServices;

import org.jinstagram.Instagram;
import rest.o.gram.credentials.ICredentialsFactory;
import rest.o.gram.credentials.RandomCredentialsFactory;
import rest.o.gram.utils.InstagramUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 8/31/13
 */
public abstract class BaseInstagramServlet<T> extends HttpServlet {
    public BaseInstagramServlet() {
        try
        {
            m_credentialsFactory = new RandomCredentialsFactory();
        } catch (Exception e)
        {
            log.severe("an error occurred while initializing the service");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try
        {
            final Instagram instagram = createInstagramAPI();
            onRequestSucceded(response, executeInstagramRequest(request, instagram));
        } catch (IOException e)
        {
            onRequestFailed(response, e);
        }
    }

    private Instagram createInstagramAPI() {
        return InstagramUtils.createInstagramAPI(m_credentialsFactory, log);
    }

    /**
     * Executes the instagram request
     * @param request client request to be translated into an instagram request
     * @param instagram instagram client
     * @return the result of the instagram request
     * @throws IOException thrown when an IO error occurs
     */
    protected abstract T executeInstagramRequest(HttpServletRequest request,
                                                 Instagram instagram) throws IOException;

    /**
     * Handles a successful instagram request's result
     * @param response the response to return
     * @param result the instagram request's response
     * @throws IOException thrown when an IO error occurs
     */
    protected abstract void onRequestSucceded(HttpServletResponse response,
                                              T result) throws IOException;

    /**
     * Handles a failed instagram request's response
     * @param response the response to return
     * @param e an error that occured
     * @throws IOException thrown when an IO error occurs
     */
    protected abstract void onRequestFailed(HttpServletResponse response,
                                            IOException e) throws IOException;

    private static final Logger log = Logger.getLogger(BaseInstagramServlet.class.getName());
    private ICredentialsFactory m_credentialsFactory;
}
