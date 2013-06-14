package rest.o.gram.service.backend;

import com.google.appengine.api.taskqueue.TaskHandle;
import rest.o.gram.TasksManager.TasksManager;
import rest.o.gram.data.DataManager;

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
            resp.sendError(200, "OK");
        } catch (IOException e)
        {
            e.printStackTrace();
            log.severe("backend cannot reply");
        }
    }

    private static void processFilterResults() {
        List<TaskHandle> tasks = TasksManager.leaseFilterResults(5, 30);
        for (final TaskHandle currTask :  tasks)
        {
            // extract rules results
            final String payLoad = new String(currTask.getPayload());
            final String[] headerBody = payLoad.split(";");
            if (headerBody.length != 2)
                return;
            final String venueId  = headerBody[0]; // TODO: currently not needed since all photos are cached on first sight..
            final String[] idRulePairs = headerBody[1].split(",");
            final Map<String,Boolean> photoIdToRuleMapping  = new HashMap<>(idRulePairs.length/2);
            for (int i = 0; i < idRulePairs.length - 1; i+=2)
                photoIdToRuleMapping.put(idRulePairs[i],Boolean.parseBoolean(idRulePairs[i+1]));

            // update DS
            DataManager.savePhotoToRuleMapping(photoIdToRuleMapping);

            // done - removes from queue
            TasksManager.dismissFilterResult(currTask.getName());
        }
    }

    private static final Logger log =
            Logger.getLogger(FilterRulesServlet.class.getName());
}