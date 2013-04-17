package rest.o.gram.tasks;

import android.os.AsyncTask;
import rest.o.gram.RestogramPhoto;
import rest.o.gram.RestogramService;
import org.json.rpc.client.HttpJsonRpcClientTransport;
import org.json.rpc.client.JsonRpcInvoker;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 4/5/13
 */
public class GetPhotosTask extends AsyncTask<String, Void, RestogramPhoto[]> {

    public GetPhotosTask(HttpJsonRpcClientTransport transport, ITaskObserver observer) {
        this.transport = transport;
        this.observer = observer;
    }

    protected RestogramPhoto[] doInBackground(String... params) {
        String venueID = params[0];

        JsonRpcInvoker invoker = new JsonRpcInvoker();
        RestogramService service = invoker.get(transport, "restogram", RestogramService.class);

        return service.getPhotos(venueID);
    }

    protected void onPostExecute(RestogramPhoto[] result) {
        observer.onFinished(result);
    }

    private HttpJsonRpcClientTransport transport;
    private ITaskObserver observer;
}

