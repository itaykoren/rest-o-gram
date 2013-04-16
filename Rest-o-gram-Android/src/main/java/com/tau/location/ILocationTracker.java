package com.tau.location;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 16/04/13
 */
public interface ILocationTracker {
    /**
     * Starts location tracking
     */
    void start();

    /**
     * Stops location tracking
     */
    void stop();

    /**
     * Sets location observer
     */
    void setObserver(ILocationObserver observer);
}
