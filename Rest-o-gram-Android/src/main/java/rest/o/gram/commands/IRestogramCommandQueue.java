package rest.o.gram.commands;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 06/05/13
 */
public interface IRestogramCommandQueue {
    /**
     * Attempts to push given command to queue back
     */
    boolean pushBack(IRestogramCommand command);

    /**
     * Attempts to push given command to queue front
     */
    boolean pushFront(IRestogramCommand command);

    /**
     * Attempts to force push given command to queue
     * Command will be executed immediately if possible
     */
    boolean pushForce(IRestogramCommand command);

    /**
     * Cancels all commands in this queue
     */
    void cancelAll();
}
