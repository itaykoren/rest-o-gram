package rest.o.gram.tasks;

import android.os.AsyncTask;
import com.leanengine.RestService;
import org.json.JSONObject;
import org.json.rpc.client.HttpJsonRpcClientTransport;
import rest.o.gram.common.Defs;
import rest.o.gram.tasks.results.GetNearbyResult;
import rest.o.gram.tasks.results.GetProfilePhotoUrlResult;

/**
 * Created with IntelliJ IDEA.
 * User: Itay
 * Date: 09/06/13
 */
public class GetProfilePhotoUrlTask extends AsyncTask<String, Void, GetProfilePhotoUrlResult> {

    public GetProfilePhotoUrlTask(HttpJsonRpcClientTransport transport, ITaskObserver observer) {
        this.transport = transport;
        this.observer = observer;
    }

    @Override
    protected GetProfilePhotoUrlResult doInBackground(String... params) {

        String facebookId = params[0];

        String pictureParam = "?fields=picture";

        String requestUrl = Defs.FacebookAPI.GRAPH_BASE_URL + facebookId + pictureParam;

        RestService restService = new RestService();
        String profilePhotoUrl = "";

        try {
            JSONObject response = restService.doGet(requestUrl);
            JSONObject picture = response.getJSONObject("picture");
            JSONObject data = picture.getJSONObject("data");
            profilePhotoUrl = data.getString("url");
        } catch (Exception e) {
            // TODO
        }

        return new GetProfilePhotoUrlResultImpl(profilePhotoUrl);
    }

    @Override
    protected void onPostExecute(GetProfilePhotoUrlResult result) {
        observer.onFinished(result);
    }

    @Override
    protected void onCancelled() {
        observer.onCanceled();
    }


    protected class GetProfilePhotoUrlResultImpl implements GetProfilePhotoUrlResult {

        public GetProfilePhotoUrlResultImpl(String profilePhotoUrl) {

            this.profilePhotoUrl = profilePhotoUrl;
        }

        @Override
        public String getProfilePhotoUrl() {
            return profilePhotoUrl;
        }

        private String profilePhotoUrl;
    }

    protected HttpJsonRpcClientTransport transport;
    protected ITaskObserver observer;
}
