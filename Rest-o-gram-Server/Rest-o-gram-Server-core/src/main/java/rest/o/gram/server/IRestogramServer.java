package rest.o.gram.server;

import rest.o.gram.data.DataManager;
import rest.o.gram.foursquare.FoursquareManager;
import rest.o.gram.tasks.TasksManager;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 6/1/14
 */
public interface IRestogramServer {
    DataManager getDataManager();

    TasksManager getTasksManager();

    FoursquareManager getFoursquareManager();
}
