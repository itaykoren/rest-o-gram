package rest.o.gram.TasksManager;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.repackaged.com.google.common.io.BaseEncoding;

import java.util.Map;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 6/10/13
 */
public class TasksManager {

    public static void enqueueFilterTask(final String venueId, final Map<String,String> photoIdToUrl) {
        final StringBuilder payloadBuilder = new StringBuilder();
        payloadBuilder.append(venueId);
        payloadBuilder.append(";");
        for (final Map.Entry<String,String> currEntry : photoIdToUrl.entrySet())
        {
            payloadBuilder.append(currEntry.getKey());
            payloadBuilder.append(",");
            payloadBuilder.append(currEntry.getValue());
            payloadBuilder.append(",");
        }
        payloadBuilder.substring(0, payloadBuilder.length()-1);

        final String payload = payloadBuilder.toString();
        outgoingQueue.add(TaskOptions.Builder.withMethod(TaskOptions.Method.PULL).payload(payload));
    }

    private static final Queue outgoingQueue = QueueFactory.getQueue("outgoing-queue");
    private static final Logger log = Logger.getLogger(TasksManager.class.getName());
}
