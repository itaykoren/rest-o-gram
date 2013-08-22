package rest.o.gram.application;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 06/07/13
 */
public interface IRestogramApplication {
    /**
     * Returns activity amount
     */
    int activityAmount();

    /**
     * Returns true whether some activity is finishing
     */
    boolean isActivityFinishing();

    /**
     * Restarts the application
     */
    void restart();
}
