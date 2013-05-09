package rest.o.gram.network;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/9/13
 */
public class NetworkStateProvider implements INetworkStateProvider {

    /**
     * Ctor
     */
    public NetworkStateProvider(Context context) {
        this.context = context;
    }

    @Override
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return  cm != null &&
                cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    private Context context;
}
