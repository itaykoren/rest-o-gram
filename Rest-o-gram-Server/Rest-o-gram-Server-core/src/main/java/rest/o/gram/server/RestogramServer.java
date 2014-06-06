package rest.o.gram.server;

import rest.o.gram.data.DataManager;
import rest.o.gram.data.DataManagerImpl;
import rest.o.gram.foursquare.FoursquareManager;
import rest.o.gram.foursquare.FoursquareManagerImpl;
import rest.o.gram.tasks.TasksManager;
import rest.o.gram.tasks.TasksManagerImpl;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 6/1/14
 */
public final class RestogramServer implements IRestogramServer {

    // NOTE: currently avoiding dependency injection frameworks as these may
    //       cause overhead on GAE instance init
    //      (which could affect responsivness due to the inherent cold-start problem with JAVA GAE)
    //      see: https://groups.google.com/forum/#!topic/google-appengine/Nz4Yt8V6PB0

    public static IRestogramServer getInstance() {
        return m_instance;
    }

    @Override
    public DataManager getDataManager() {
        return m_dataManager;
    }

    @Override
    public TasksManager getTasksManager() {
        return m_tasksManager;
    }

    @Override
    public FoursquareManager getFoursquareManager() {
        return m_foursquareManager;
    }

    private RestogramServer () {}

    private final static IRestogramServer m_instance = new RestogramServer();
    private final DataManager m_dataManager = new DataManagerImpl();
    private final TasksManager m_tasksManager = new TasksManagerImpl();
    private final FoursquareManager m_foursquareManager = new FoursquareManagerImpl();
}
