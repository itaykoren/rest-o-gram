package rest.o.gram.tasks;

import rest.o.gram.tasks.results.GetInfoResult;
import rest.o.gram.tasks.results.GetNearbyResult;
import rest.o.gram.tasks.results.GetPhotosResult;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 05/04/13
 */
public interface ITaskObserver {

    void onFinished(GetNearbyResult venues);

    void onFinished(GetInfoResult venue);

    void onFinished(GetPhotosResult result);
}
