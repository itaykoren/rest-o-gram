package rest.o.gram.view;

/**
 * Created with IntelliJ IDEA.
 * User: Hen
 * Date: 6/8/13
 */
public interface IPopupView {
    /**
     * Returns true whether this popup view is open, false otherwise
     */
    boolean isOpen();

    /**
     * Attempts to open this popup view
     * Returns true if successful, false otherwise
     */
    boolean open();

    /**
     * Attempts to close this popup view
     * Returns true if successful, false otherwise
     */
    boolean close();
}
