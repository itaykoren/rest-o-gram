package rest.o.gram.tasks.results;

import rest.o.gram.lean.LeanAccount;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 9/8/13
 */
public interface GetCurrentAccountDataResult {

    /**
     * Gets the associated lean account
     */
    LeanAccount getAccount();
}
