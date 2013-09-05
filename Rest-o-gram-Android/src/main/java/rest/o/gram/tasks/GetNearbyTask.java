package rest.o.gram.tasks;

import android.util.Log;
import org.json.rpc.client.HttpJsonRpcClientTransport;
import org.json.rpc.client.JsonRpcInvoker;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.entities.RestogramVenue;
import rest.o.gram.iservice.RestogramService;
import rest.o.gram.results.VenuesResult;
import rest.o.gram.tasks.results.GetNearbyResult;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 4/5/13
 */
public class GetNearbyTask extends RestogramAsyncTask<Double, Void, GetNearbyResult> {

    public GetNearbyTask(HttpJsonRpcClientTransport transport, ITaskObserver observer) {
        super(transport, observer);
    }

    @Override
    protected GetNearbyResult doInBackgroundImpl(Double... params) {
        JsonRpcInvoker invoker = new JsonRpcInvoker();
        RestogramService service = invoker.get(transport, "restogram", RestogramService.class);

        if (RestogramClient.getInstance().isDebuggable())
            Log.d("REST-O-GRAM", "GET NEARBY - LAT:" +  params[0] + " LONG:" + params[1]);
        return safeGetNearby(service, params);
    }

    @Override
    protected void onPostExecute(GetNearbyResult result) {
        observer.onFinished(result);
    }

    private GetNearbyResult safeGetNearby(RestogramService service, Double ... params) {
        VenuesResult result;
        RestogramVenue[] venues;
        try
        {
            if(params.length == 3)
                result = service.getNearby(params[0], params[1], params[2]);
            else
                result = service.getNearby(params[0], params[1]);

            if  (RestogramClient.getInstance().isDebuggable() && result != null)
                Log.d("REST-O-GRAM", "got " +
                        (result.getResult() == null ? 0 : result.getResult().length) + "venues");

            venues = (result == null ? null : result.getResult());
            return new GetNearbyResultImpl(venues, params[0], params[1]);
        }
        catch (Exception e)
        {
            Log.e("REST-O-GRAM", "GET NEARBY - FIRST ATTEMPT FAILED");
            e.printStackTrace();
            if(params.length == 3)
            {
                result = service.getNearby(params[0], params[1], params[2]);
            }
            else
            {
                result = service.getNearby(params[0], params[1]);
            }

            venues = (result == null ? null : result.getResult());
            return new GetNearbyResultImpl(venues, params[0], params[1]);
        }
    }

    class GetNearbyResultImpl implements GetNearbyResult {

        public GetNearbyResultImpl(RestogramVenue[] venues, double latitude, double longitude) {
            if (venues != null)
            {
                for (int i = 0; i < venues.length; ++i)
                    venues[i].decodeStrings();
            }
            this.venues = venues;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        public RestogramVenue[] getVenues() {
            return venues;
        }

        @Override
        public double getLatitude() {
            return latitude;
        }

        @Override
        public double getLongitude() {
            return longitude;
        }

        private RestogramVenue[] venues;
        private double latitude;
        private double longitude;
    }
}

