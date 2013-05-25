package rest.o.gram.commands;

import android.os.AsyncTask;
import org.json.rpc.client.HttpJsonRpcClientTransport;
import rest.o.gram.tasks.ITaskObserver;
import rest.o.gram.tasks.results.*;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 5/23/13
 */
public class  AsyncTaskRestogramCommand extends AbstractRestogramCommand implements ITaskObserver {

    /**
     * Ctor
     */
    public AsyncTaskRestogramCommand(HttpJsonRpcClientTransport transport, ITaskObserver observer) {
        super(transport, observer);
    }

    @Override
    public boolean cancel() {
        if(!super.cancel())
            return false;

        task.cancel(true);
        return true;
    }

    @Override
    public void onFinished(GetNearbyResult venues) {
        notifyFinished();
        observer.onFinished(venues);
    }

    @Override
    public void onFinished(GetInfoResult venue) {
        notifyFinished();
        observer.onFinished(venue);
    }

    @Override
    public void onFinished(GetPhotosResult result) {
        notifyFinished();
        observer.onFinished(result);
    }

    @Override
    public void onFinished(CachePhotoResult result) {
        notifyFinished();
        observer.onFinished(result);
    }

    @Override
    public void onFinished(FetchPhotosFromCacheResult result) {
        notifyFinished();
        observer.onFinished(result);
    }

    @Override
    public void onFinished(CacheVenueResult result) {
        notifyFinished();
        observer.onFinished(result);
    }

    @Override
    public void onFinished(FetchVenuesFromCacheResult result) {
        notifyFinished();
        observer.onFinished(result);
    }

    @Override
    public void onCanceled() {
        notifyCanceled();
        observer.onCanceled();
    }

    protected AsyncTask task;
}