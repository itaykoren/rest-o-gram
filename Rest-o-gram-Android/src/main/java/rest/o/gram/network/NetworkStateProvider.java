package rest.o.gram.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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
        if(cm == null) {
            cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        }

        return  cm != null &&
                cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    @Override
    public boolean isWifi() {
        if(cm == null) {
            cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        }

        if(cm == null)
            return false;

        NetworkInfo info = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return info != null && info.isConnected();
    }

    private Context context;
    private ConnectivityManager cm;
}
