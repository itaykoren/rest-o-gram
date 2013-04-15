package com.tau.tasks;

import android.os.AsyncTask;
import com.tau.RestogramService;
import com.tau.RestogramVenue;
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
        double latitude = params[0];
        double longitude = params[1];
        double radius = params[2];

        JsonRpcInvoker invoker = new JsonRpcInvoker();
        RestogramService service = invoker.get(transport, "restogram", RestogramService.class);

        return service.getNearby(latitude, longitude, radius);
    }

    protected void onPostExecute(RestogramVenue[] result) {
        observer.onFinished(result);
    }

    private HttpJsonRpcClientTransport transport;
    private ITaskObserver observer;
}

