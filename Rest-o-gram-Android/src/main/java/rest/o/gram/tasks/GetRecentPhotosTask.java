package rest.o.gram.tasks;

import android.os.AsyncTask;
import com.leanengine.LeanEngine;
import org.json.rpc.client.HttpJsonRpcClientTransport;
import org.json.rpc.client.JsonRpcInvoker;
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.iservice.RestogramAuthService;
import rest.o.gram.tasks.results.GetPhotosResult;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/19/13
 */
public class GetRecentPhotosTask extends AsyncTask<Void, Void, GetPhotosResult> {
    public GetRecentPhotosTask(HttpJsonRpcClientTransport transport, ITaskObserver observer, String authToken) {
        this.transport = transport;
        this.authToken = authToken;
        this.observer = observer;

        // doesn't seems to work on RPC calls...
        //transport.setHeader("lean_token", authToken);
    }

    protected GetPhotosResult doInBackground(Void... params) {
        JsonRpcInvoker invoker = new JsonRpcInvoker();
        RestogramAuthService service = invoker.get(transport, "restogram", RestogramAuthService.class);

       return new GetPhotosResultImpl(service.getRecentPhotos(authToken).getPhotos(), null);
    }

    protected void onPostExecute(GetPhotosResult result) {
        observer.onFinished(result);
    }

    protected class GetPhotosResultImpl implements GetPhotosResult {
        public GetPhotosResultImpl(RestogramPhoto[] photos, String token) {
            this.photos = photos;
            this.token = token;
        }

        @Override
        public RestogramPhoto[] getPhotos() {
            return photos;
        }

        @Override
        public String getToken() {
            return token;
        }

        private RestogramPhoto[] photos;
        private String token;
    }


    protected HttpJsonRpcClientTransport transport;
    protected ITaskObserver observer;
    protected String authToken;
}
