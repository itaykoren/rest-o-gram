package rest.o.gram.service.InstagramServices;

import com.google.gson.Gson;
import org.jinstagram.Instagram;
import rest.o.gram.ApisConverters;
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.service.InstagramServices.Entities.EmptyRestogramPhoto;
import rest.o.gram.utils.InstagramUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 9/3/13
 */
public class GetPhotoServlet extends BaseInstagramServlet<RestogramPhoto> {
    @Override
    protected RestogramPhoto executeInstagramRequest(HttpServletRequest request, Instagram instagram) throws IOException {
        final String mediaId = request.getReader().readLine();
        log.info(String.format("getMediaInfo : %s", mediaId));
        return ApisConverters.convertToRestogramPhoto(instagram.getMediaInfo(mediaId));
    }

    @Override
    protected void onRequestSucceded(HttpServletResponse response, RestogramPhoto result) throws IOException {
        RestogramPhoto actualResult;
        if (InstagramUtils.isNullOrEmpty(result))
        {
            actualResult = new EmptyRestogramPhoto();
            log.info("no photo was found");
        }
        else
        {
            actualResult = result;
            log.info(String.format("found photo : %s", actualResult.getInstagram_id()));
        }
        response.getWriter().write(new Gson().toJson(actualResult.encodeStrings()));
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void onRequestFailed(HttpServletResponse response, IOException e) throws IOException {
        log.severe(String.format("get media info has failed, error: %s", e.getMessage()));
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "error while contacting instagram");
    }

    private static final Logger log = Logger.getLogger(GetPhotoServlet.class.getName());
}