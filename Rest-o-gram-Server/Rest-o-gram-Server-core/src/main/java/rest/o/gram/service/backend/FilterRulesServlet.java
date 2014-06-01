package rest.o.gram.service.backend;

import com.google.appengine.api.taskqueue.TaskHandle;
import rest.o.gram.Defs;
import rest.o.gram.InstagramAccessManager;
import rest.o.gram.data.DataManager;
import rest.o.gram.data.DataManagerImpl;
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.server.IRestogramServer;
import rest.o.gram.server.RestogramServer;
import rest.o.gram.tasks.TasksManager;
import rest.o.gram.utils.InstagramUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
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
            processFilterResults();
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e)
        {
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

    private void processFilterResults() {
        final List<TaskHandle> tasks =
                TasksManager.leaseFilterResults(Defs.FilterRulesQueue.LEASE_COUNT,
                                                Defs.FilterRulesQueue.LEASE_PERIOD);
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
                else if (dataManager.isPhotoPending(currPhotoId)) // restore from pending
                    currPhoto = dataManager.getPendingPhoto(currPhotoId);
                else // get from instagram
                {
                    final InstagramAccessManager.PrepareRequest prepareRequest =
                            new InstagramAccessManager.PrepareRequest() {
                                @Override
                                public byte[] getPayload() {
                                    return currPhotoId.getBytes();
                                }
                            };
                    currPhoto = InstagramAccessManager.parallelBackendInstagramRequest(Defs.Instagram.RequestType.GetPhoto,
                                                                                       prepareRequest,
                                                                                       RestogramPhoto.class);
                    if (InstagramUtils.isNullOrEmpty(currPhoto))
                    {
                        log.warning("cannot obtain photo, skips");
                        continue;
                    }
                    //decode string to get the correct encoding
                    currPhoto.decodeStrings();

                    currPhoto.setOriginVenueId(venueId);
                }

                photoToRuleMapping.put(currPhoto,Boolean.parseBoolean(idRulePairs[i+1]));
            }

            // update DS
            if (!dataManager.savePhotoToRuleMapping(photoToRuleMapping))
                log.warning("cannot save rules - will ignore");

            //  photo is no longer pending
            final List<String> photoIdsToRemove = new ArrayList<>(idRulePairs.length/2);
            for (int i = 0; i < idRulePairs.length - 1; i+=2)
                photoIdsToRemove.add(idRulePairs[i]);

            dataManager.removePendingPhotos(photoIdsToRemove);

            // done - removes from queue
            TasksManager.dismissFilterResult(currTask.getName());
        }
    }

    private static final Logger log =
            Logger.getLogger(FilterRulesServlet.class.getName());
    private final DataManager dataManager = RestogramServer.getInstance().getDataManager();
}