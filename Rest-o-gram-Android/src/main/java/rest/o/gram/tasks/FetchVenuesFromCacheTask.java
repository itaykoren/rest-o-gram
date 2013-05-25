package rest.o.gram.tasks;

import android.os.AsyncTask;
import android.util.Log;
import org.json.rpc.client.HttpJsonRpcClientTransport;
import org.json.rpc.client.JsonRpcInvoker;
import rest.o.gram.entities.RestogramVenue;
import rest.o.gram.iservice.RestogramService;
import rest.o.gram.tasks.results.FetchVenuesFromCacheResult;

import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/24/13
 */
public class FetchVenuesFromCacheTask extends AsyncTask<String,Void,FetchVenuesFromCacheResult> {
    public FetchVenuesFromCacheTask(HttpJsonRpcClientTransport transport, ITaskObserver observer) {
        this.transport = transport;
        this.observer = observer;
    }

    @Override
    protected FetchVenuesFromCacheResult doInBackground(String... params) {
        JsonRpcInvoker invoker = new JsonRpcInvoker();
        RestogramService service = invoker.get(transport, "restogram", RestogramService.class);
        return safeFetchVenuesFromCache(service, params);
    }

    private FetchVenuesFromCacheResult safeFetchVenuesFromCache(RestogramService service, String... ids) {
        try
        {
            return new FetchVenuesFromCacheResultImpl(service.fetchVenuesFromCache(ids));
        }
        catch (Exception e)
        {
            Log.e("REST-O-GRAM", "FETCH VENUES - FIRST ATTEMPT FAILED");
            e.printStackTrace();
            return new FetchVenuesFromCacheResultImpl(service.fetchVenuesFromCache(ids));
        }
    }
    
    @Override
    protected void onPostExecute(FetchVenuesFromCacheResult result) {
        observer.onFinished(result);
    }

    class FetchVenuesFromCacheResultImpl implements FetchVenuesFromCacheResult {

        FetchVenuesFromCacheResultImpl(RestogramVenue[] venues) {
            this.venues = Arrays.asList(venues);
            if (this.venues != null)
            {
                for (RestogramVenue currVenue : this.venues)
                    currVenue.decodeStrings();
            }
        }

        @Override
        public List<RestogramVenue> getVenues() {
            return venues;
        }

        private List<RestogramVenue> venues;
    }

    private HttpJsonRpcClientTransport transport;
    private ITaskObserver observer;
}
