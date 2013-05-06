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
        CS_Finished,
        CS_Failed
    }

    /**
     * Executes command
     */
    void execute();

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
}
