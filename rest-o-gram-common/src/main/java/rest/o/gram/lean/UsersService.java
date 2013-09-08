package rest.o.gram.lean;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 9/8/13
 */
public interface UsersService {

    /**
     *  Retrieves the current account's data(as a JSON string)
     */
    String getCurrentAccountData();

    /**
     *  Logs out from the current session
     */
    boolean logout();
}
