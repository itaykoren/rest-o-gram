package com.leanengine;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/23/13
 */
public interface ContinuedNetworkCallback<E, T> {

    /**
     * REST service was successfully invoked and result is available.
     * @param result Result as returned by REST service.
     */
    public abstract void onResult(T token, E... result);

    /**
     * There was an error invoking the REST service.
     * @param error A {@link LeanError} containing detailed error code and description of error.
     */
    public abstract void onFailure(LeanError error);

}
