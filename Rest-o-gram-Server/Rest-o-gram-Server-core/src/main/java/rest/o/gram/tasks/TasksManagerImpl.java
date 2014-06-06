package rest.o.gram.tasks;

import com.google.appengine.api.taskqueue.*;
import org.apache.commons.lang3.StringUtils;
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.utils.InstagramUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 6/10/13
 */
public class TasksManagerImpl implements TasksManager {

    @Override
    public TaskHandle enqueueFilterTask(final String venueId,
                                        final List<RestogramPhoto> rawPhotos) {
        final StringBuilder payloadBuilder = new StringBuilder();
        payloadBuilder.append(venueId);
        payloadBuilder.append(";");
        for (final RestogramPhoto currPhoto : rawPhotos)
        {
            if (InstagramUtils.isNullOrEmpty(currPhoto) || StringUtils.isBlank(currPhoto.getStandardResolution()))
                continue;

            payloadBuilder.append(currPhoto.getInstagram_id());
            payloadBuilder.append(",");
            payloadBuilder.append(currPhoto.getStandardResolution());
            payloadBuilder.append(",");
        }
        payloadBuilder.substring(0, payloadBuilder.length()-1);

        final String payload = payloadBuilder.toString();
        return outgoingQueue.add(TaskOptions.Builder.withMethod(TaskOptions.Method.PULL).payload(payload));
    }

    @Override
    public List<TaskHandle> leaseFilterResults(final long count, final long period)
    {
        return incomingQueue.leaseTasks(LeaseOptions.Builder.withCountLimit(count).
                                            leasePeriod(period, TimeUnit.SECONDS));
    }

    @Override
    public boolean dismissFilterResult(String resultName) {
        return incomingQueue.deleteTask(resultName);
    }

    private static final Queue outgoingQueue = QueueFactory.getQueue("outgoing-queue");
    private static final Queue incomingQueue = QueueFactory.getQueue("incoming-queue");
    private static final Logger log = Logger.getLogger(TasksManagerImpl.class.getName());
}
