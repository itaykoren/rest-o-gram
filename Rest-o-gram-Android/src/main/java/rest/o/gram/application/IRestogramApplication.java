package rest.o.gram.application;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 06/07/13
 */
public interface IRestogramApplication {
    /**
     * Returns true whether the application is in last activity
     */
    public boolean isInLastActivity();

    /**
     * Restarts the application
     */
    void restart();
}
