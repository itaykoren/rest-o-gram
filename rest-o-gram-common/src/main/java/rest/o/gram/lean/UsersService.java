package rest.o.gram.lean;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 9/8/13
 */
public interface UsersService {

    /**
     *  Retrieves the current acount's data(as a JSON string)
     */
    String getCurrentAccountData();

    /**
     *  Logs out frfom the current session
     */
    boolean logout();
}
