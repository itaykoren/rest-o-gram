package rest.o.gram.tasks;

import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.entities.RestogramVenue;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 05/04/13
 */
public interface ITaskObserver {

    void onFinished(RestogramVenue[] venues);

    void onFinished(RestogramVenue venue);

    void onFinished(RestogramPhoto[] photos);
}
