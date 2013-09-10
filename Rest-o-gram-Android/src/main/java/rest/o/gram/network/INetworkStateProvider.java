package rest.o.gram.network;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/9/13
 */
public interface INetworkStateProvider {
    /**
     * Returns true whether network is online, false otherwise
     */
    boolean isOnline();

    /**
     * Returns true whether network is connected via wifi, false otherwise
     */
    boolean isWifi();
}
