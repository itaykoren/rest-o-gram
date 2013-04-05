package com.tau;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 05/04/13
 */
public interface ITaskObserver {

    void onFinished(RestogramVenue[] venues);

    void onFinished(RestogramPhoto[] photos);
}
