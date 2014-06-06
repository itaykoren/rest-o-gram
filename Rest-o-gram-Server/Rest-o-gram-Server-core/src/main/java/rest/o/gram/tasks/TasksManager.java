package rest.o.gram.tasks;

import com.google.appengine.api.taskqueue.TaskHandle;
import rest.o.gram.entities.RestogramPhoto;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 6/6/14
 */
public interface TasksManager {
    TaskHandle enqueueFilterTask(String venueId,
                                 List<RestogramPhoto> rawPhotos);

    List<TaskHandle> leaseFilterResults(long count, long period);

    boolean dismissFilterResult(String resultName);
}
