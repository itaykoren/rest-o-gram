package rest.o.gram.tasks;

import org.json.rpc.client.HttpJsonRpcClientTransport;
import org.json.rpc.client.JsonRpcInvoker;
import rest.o.gram.entities.RestogramVenue;
import rest.o.gram.iservice.RestogramService;
import rest.o.gram.tasks.results.GetInfoResult;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 14/04/13
 */
public class GetInfoTask extends RestogramAsyncTask<String, Void, GetInfoResult> {
    public GetInfoTask(HttpJsonRpcClientTransport transport, ITaskObserver observer) {
        super(transport, observer);
    }

    @Override
    protected GetInfoResult doInBackgroundImpl(String... params) {
        String venueID = params[0];

        JsonRpcInvoker invoker = new JsonRpcInvoker();
        RestogramService service = invoker.get(transport, "restogram", RestogramService.class);

        return new GetInfoResultImpl(service.getInfo(venueID).getResult());
    }

    @Override
    protected void onPostExecute(GetInfoResult result) {
        observer.onFinished(result);
    }

    class GetInfoResultImpl implements GetInfoResult {

        public GetInfoResultImpl(RestogramVenue venue) {
            if (venue != null)
                venue.decodeStrings();
            this.venue = venue;
        }

        @Override
        public RestogramVenue getVenue() {
            return venue;
        }

        private RestogramVenue venue;
    }
}
