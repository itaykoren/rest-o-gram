package rest.o.gram.tasks;

import android.os.AsyncTask;
import rest.o.gram.entities.RestogramVenue;
import rest.o.gram.iservice.RestogramService;
import org.json.rpc.client.HttpJsonRpcClientTransport;
import org.json.rpc.client.JsonRpcInvoker;
import rest.o.gram.tasks.results.GetInfoResult;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 14/04/13
 */
public class GetInfoTask extends AsyncTask<String, Void, GetInfoResult> {
    public GetInfoTask(HttpJsonRpcClientTransport transport, ITaskObserver observer) {
        this.transport = transport;
        this.observer = observer;
    }

    @Override
    protected GetInfoResult doInBackground(String... params) {
        String venueID = params[0];

        JsonRpcInvoker invoker = new JsonRpcInvoker();
        RestogramService service = invoker.get(transport, "restogram", RestogramService.class);

        return new GetInfoResultImpl(service.getInfo(venueID).getResult());
    }

    @Override
    protected void onPostExecute(GetInfoResult result) {
        observer.onFinished(result);
    }

    @Override
    protected void onCancelled() {
        observer.onCanceled();
    }

    class GetInfoResultImpl implements GetInfoResult {

        public GetInfoResultImpl(RestogramVenue venue) {
            this.venue = venue;
        }

        @Override
        public RestogramVenue getVenue() {
            return venue;
        }

        private RestogramVenue venue;
    }

    private HttpJsonRpcClientTransport transport;
    private ITaskObserver observer;
}
