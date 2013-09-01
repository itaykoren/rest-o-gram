package rest.o.gram.service.backend;

import com.google.appengine.api.taskqueue.TaskHandle;
import rest.o.gram.ApisAccessManager;
import rest.o.gram.tasks.TasksManager;
import rest.o.gram.data.DataManager;
import rest.o.gram.entities.RestogramPhoto;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 6/12/13
 */
public class FilterRulesServlet extends HttpServlet {

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
            //TODO: make it a backend servlet....
            processFilterResults();
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e)
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

    private static void processFilterResults() {
        List<TaskHandle> tasks = TasksManager.leaseFilterResults(6, 30);
        for (final TaskHandle currTask :  tasks)
        {
            // extract rules results
            final String payLoad = new String(currTask.getPayload());
            final String[] headerBody = payLoad.split(";");
            if (headerBody.length != 2)
                return;
            final String venueId  = headerBody[0];
            final String[] idRulePairs = headerBody[1].split(",");
            final Map<RestogramPhoto,Boolean> photoToRuleMapping  = new HashMap<>(idRulePairs.length/2);
            for (int i = 0; i < idRulePairs.length - 1; i+=2)
            {
                final String currPhotoId = idRulePairs[i];
                RestogramPhoto currPhoto = null;
                if (!Boolean.parseBoolean(idRulePairs[i+1]))
                {
                    currPhoto = new RestogramPhoto();
                    currPhoto.setInstagram_id(currPhotoId);
                }
                else if (DataManager.isPhotoPending(currPhotoId)) // restore from pending
                    currPhoto = DataManager.getPendingPhoto(currPhotoId, venueId);
                else // get from instagram
                {
                    currPhoto = ApisAccessManager.getPhoto(currPhotoId, venueId);
                    if (currPhoto == null)
                        continue; // TODO: consider allowing more retries...
                }

                photoToRuleMapping.put(currPhoto,Boolean.parseBoolean(idRulePairs[i+1]));
            }

            // update DS
            DataManager.savePhotoToRuleMapping(photoToRuleMapping);

            //  photo is no longer pending
            for (int i = 0; i < idRulePairs.length - 1; i+=2)
                DataManager.removePendingPhoto(idRulePairs[i]);

            // done - removes from queue
            TasksManager.dismissFilterResult(currTask.getName());
        }
    }

    private static final Logger log =
            Logger.getLogger(FilterRulesServlet.class.getName());
}