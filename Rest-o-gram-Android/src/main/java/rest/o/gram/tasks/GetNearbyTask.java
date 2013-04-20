package rest.o.gram.tasks;

import android.os.AsyncTask;
import android.util.Log;
import rest.o.gram.BuildConfig;
import rest.o.gram.RestogramService;
import rest.o.gram.RestogramVenue;
import org.json.rpc.client.HttpJsonRpcClientTransport;
import org.json.rpc.client.JsonRpcInvoker;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 4/5/13
 */
public class GetNearbyTask extends AsyncTask<Double, Void, RestogramVenue[]> {

    public GetNearbyTask(HttpJsonRpcClientTransport transport, ITaskObserver observer) {
        this.transport = transport;
        this.observer = observer;
    }

    protected RestogramVenue[] doInBackground(Double... params) {
        JsonRpcInvoker invoker = new JsonRpcInvoker();
        RestogramService service = invoker.get(transport, "restogram", RestogramService.class);

        if (BuildConfig.DEBUG)
            Log.d("REST-O-GRAM", "GET NEARBY - LAT:" +  params[0] + " LONG:" + params[1]);
        return safeGetNearby(service, params);
    }

    protected void onPostExecute(RestogramVenue[] result) {
        observer.onFinished(result);
    }

    private RestogramVenue[] safeGetNearby(RestogramService service, Double ... params) {
        try
        {
            if(params.length == 3)
                return service.getNearby(params[0], params[1], params[2]);
            else
                return service.getNearby(params[0], params[1]);
        }
        catch (Exception e)
        {
            Log.e("REST-O-GRAM", "GET NEARBY - FIRST ATTEMPT FAILED");
            e.printStackTrace();
            if(params.length == 3)
                return service.getNearby(params[0], params[1], params[2]);
            else
                return service.getNearby(params[0], params[1]);
        }
    }

    private HttpJsonRpcClientTransport transport;
    private ITaskObserver observer;
}

