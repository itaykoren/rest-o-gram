package rest.o.gram.tasks;

import android.util.Log;
import org.json.rpc.client.HttpJsonRpcClientTransport;
import org.json.rpc.client.JsonRpcInvoker;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.results.PhotosResult;
import rest.o.gram.service.RestogramService;
import rest.o.gram.tasks.results.GetPhotosResult;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 4/22/13
 */
public class GetNextPhotosTask extends GetPhotosTask {

    public GetNextPhotosTask(HttpJsonRpcClientTransport transport, ITaskObserver observer) {
        super(transport, observer);
    }

    @Override
    protected GetPhotosResult doInBackground(String... params) {
        String token =  params[0];

        JsonRpcInvoker invoker = new JsonRpcInvoker();
        RestogramService service = invoker.get(transport, "restogram", RestogramService.class);

        if (RestogramClient.getInstance().isDebuggable())
            Log.d("REST-O-GRAM", "Getting some more photos");

        PhotosResult result = service.getNextPhotos(token);
        if (result == null)
            return null;
        return (new GetPhotosResultImpl(result.getPhotos(), result.getToken()));
    }

    @Override
    protected void onPostExecute(GetPhotosResult result) {
        if (RestogramClient.getInstance().isDebuggable())
            Log.d("REST-O-GRAM", "Got some more photos");
        observer.onFinished(result);
    }
}