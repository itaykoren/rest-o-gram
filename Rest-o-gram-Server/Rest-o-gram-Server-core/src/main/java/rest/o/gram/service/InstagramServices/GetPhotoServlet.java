package rest.o.gram.service.InstagramServices;

import com.google.gson.Gson;
import org.jinstagram.Instagram;
import org.jinstagram.entity.media.MediaInfoFeed;
import rest.o.gram.service.InstagramServices.Entities.EmptyMediaInfoFeed;
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
public class GetPhotoServlet extends BaseInstagramServlet<MediaInfoFeed> {
    @Override
    protected MediaInfoFeed executeInstagramRequest(HttpServletRequest request, Instagram instagram) throws IOException {
        final String mediaId = request.getReader().readLine();
        log.info(String.format("getMediaInfo : %s", mediaId));
        return instagram.getMediaInfo(mediaId);
    }

    @Override
    protected void onRequestSucceded(HttpServletResponse response, MediaInfoFeed result) throws IOException {
        MediaInfoFeed actualResult;
        if (InstagramUtils.isNullOrEmpty(result))
        {
            actualResult = new EmptyMediaInfoFeed();
            log.info("no photo was found");
        }
        else
        {
            actualResult = result;
            log.info(String.format("found photo : %s", actualResult.getData().getId()));
        }
        response.getWriter().write(new Gson().toJson(actualResult));
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void onRequestFailed(HttpServletResponse response, IOException e) throws IOException {
        log.severe(String.format("get media info has failed %s", e.getMessage()));
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "error while contacting instagram");
    }

    private static final Logger log = Logger.getLogger(GetPhotoServlet.class.getName());
}