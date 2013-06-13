package rest.o.gram.TasksManager;

import com.google.appengine.api.taskqueue.*;
import com.google.appengine.repackaged.com.google.common.io.BaseEncoding;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 6/10/13
 */
public class TasksManager {

    public static TaskHandle enqueueFilterTask(final String venueId, final Map<String,String> photoIdToUrl) {
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
        return outgoingQueue.add(TaskOptions.Builder.withMethod(TaskOptions.Method.PULL).payload(payload));
    }

    public static List<TaskHandle> leaseFilterResults(final long count, final long period)
    {
        return incomingQueue.leaseTasks(LeaseOptions.Builder.withCountLimit(count).
                                            leasePeriod(period, TimeUnit.SECONDS));
    }

    public static boolean dismissFilterResult(String resultName) {
        return incomingQueue.deleteTask(resultName);
    }

    private static final Queue outgoingQueue = QueueFactory.getQueue("outgoing-queue");
    private static final Queue incomingQueue = QueueFactory.getQueue("incoming-queue");
    private static final Logger log = Logger.getLogger(TasksManager.class.getName());
}
