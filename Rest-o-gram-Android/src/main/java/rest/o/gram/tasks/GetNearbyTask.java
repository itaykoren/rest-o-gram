package rest.o.gram.tasks;

import android.os.AsyncTask;
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

        if(params.length == 3)
            return service.getNearby(params[0], params[1], params[2]);
        else
            return service.getNearby(params[0], params[1]);
    }

    protected void onPostExecute(RestogramVenue[] result) {
        observer.onFinished(result);
    }

    private HttpJsonRpcClientTransport transport;
    private ITaskObserver observer;
}

