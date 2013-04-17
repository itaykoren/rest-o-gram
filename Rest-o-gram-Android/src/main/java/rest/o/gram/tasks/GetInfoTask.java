package rest.o.gram.tasks;

import android.os.AsyncTask;
import rest.o.gram.RestogramService;
import rest.o.gram.RestogramVenue;
import org.json.rpc.client.HttpJsonRpcClientTransport;
import org.json.rpc.client.JsonRpcInvoker;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 14/04/13
 */
public class GetInfoTask extends AsyncTask<String, Void, RestogramVenue> {
    public GetInfoTask(HttpJsonRpcClientTransport transport, ITaskObserver observer) {
        this.transport = transport;
        this.observer = observer;
    }

    protected RestogramVenue doInBackground(String... params) {
        String venueID = params[0];

        JsonRpcInvoker invoker = new JsonRpcInvoker();
        RestogramService service = invoker.get(transport, "restogram", RestogramService.class);

        return service.getInfo(venueID);
    }

    protected void onPostExecute(RestogramVenue result) {
        observer.onFinished(result);
    }

    private HttpJsonRpcClientTransport transport;
    private ITaskObserver observer;
}
