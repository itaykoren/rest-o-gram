package rest.o.gram.commands;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 15/04/13
 */
public interface IRestogramCommand {

    /**
     * Command state enumeration
     */
    public enum State {
        CS_Pending,
        CS_Executing,
        CS_Canceling,
        CS_Canceled,
        CS_Finished,
        CS_Failed,
        CS_TimedOut
    }

    /**
     * Attempts to execute command
     * Returns true if successful, false otherwise
     */
    boolean execute();

    /**
     * Attempts to cancel command execution
     * Returns true if successful, false otherwise
     */
    boolean cancel();

    /**
     * Adds command observer
     */
    void addObserver(IRestogramCommandObserver observer);

    /**
     * Removes command observer
     */
    void removeObserver(IRestogramCommandObserver observer);

    /**
     * Returns command state
     */
    State state();

    /**
     * Returns timeout interval in milliseconds
     */
    long getTimeoutInterval();
}
