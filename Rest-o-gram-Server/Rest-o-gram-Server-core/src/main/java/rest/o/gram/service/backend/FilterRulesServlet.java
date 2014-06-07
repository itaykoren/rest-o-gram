package rest.o.gram.service.backend;

import com.google.appengine.api.taskqueue.TaskHandle;
import rest.o.gram.Defs;
import rest.o.gram.InstagramAccessManager;
import rest.o.gram.data.DataManager;
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.server.RestogramServer;
import rest.o.gram.tasks.TasksManager;
import rest.o.gram.utils.InstagramUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
                m_tasksManager.leaseFilterResults(Defs.FilterRulesQueue.LEASE_COUNT,
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
            final ArrayList<RestogramPhoto> photos  =
                    new ArrayList<>(idRulePairs.length/2);
            for (int i = 0; i < idRulePairs.length - 1; i+=2)
            {
                final String currPhotoId = idRulePairs[i];
                RestogramPhoto currPhoto = null;
                if (!Boolean.parseBoolean(idRulePairs[i+1]))
                {
                    currPhoto = new RestogramPhoto();
                    currPhoto.setInstagram_id(currPhotoId);
                    currPhoto.setApproved(false);

                    // TODO: uncomment if filter rules can override photo that have been "yummied"
//                    HashMap<String, DatastoreUtils.PropertyDescription> props = new HashMap<>();
//                    props.put(Props.Photo.APPROVED,
//                            new DatastoreUtils.PropertyDescription(false, true));
//                    try {
//                        DatastoreUtils.putPublicEntity(Kinds.PHOTO, currPhotoId, props);
//                    } catch (LeanException e) {
//                        log.warning(String.format("cannot store unapproved photo in DS. id:%s, error:%s",
//                                   currPhotoId, e.getMessage()));
//                    }
//                    finally {
//                        continue;
//                    }
                }
                // TODO: remove when pending list is DS based
                else if (m_dataManager.isPhotoPending(currPhotoId)) // restore from pending
                {
                    currPhoto = m_dataManager.getPendingPhoto(currPhotoId);

                    if (currPhoto != null)
                        currPhoto.setApproved(true);
                }
                else // get from instagram
                {
                    currPhoto =
                            RestogramServer.getInstance().getInstagramManager().getPhoto(currPhotoId);
                    if (currPhoto == null)
                    {
                        log.warning("cannot obtain photo, skips");
                        continue;
                    }
                    //decode string to get the correct encoding
                    currPhoto.decodeStrings();

                    currPhoto.setOriginVenueId(venueId);
                    currPhoto.setApproved(true);
                }

                photos.add(currPhoto);
            }

            // update DS
            if (!m_dataManager.savePhotosFilterRules(photos))
                log.warning("cannot save rules - will ignore");

            //  photo is no longer pending
            final List<String> photoIdsToRemove = new ArrayList<>(idRulePairs.length/2);
            for (int i = 0; i < idRulePairs.length - 1; i+=2)
                photoIdsToRemove.add(idRulePairs[i]);

            m_dataManager.removePendingPhotos(photoIdsToRemove);

            // done - removes from queue
            m_tasksManager.dismissFilterResult(currTask.getName());
        }
    }

    private static final Logger log =
            Logger.getLogger(FilterRulesServlet.class.getName());
    private final DataManager m_dataManager =
            RestogramServer.getInstance().getDataManager();
    private final TasksManager m_tasksManager =
            RestogramServer.getInstance().getTasksManager();
}