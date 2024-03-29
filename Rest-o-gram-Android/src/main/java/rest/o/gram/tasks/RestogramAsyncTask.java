package rest.o.gram.tasks;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import org.json.rpc.client.HttpJsonRpcClientTransport;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 05/09/13
 */
public abstract class RestogramAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    /**
     * Ctor
     */
    public RestogramAsyncTask(HttpJsonRpcClientTransport transport, ITaskObserver observer) {
        this.transport = transport;
        this.observer = observer;
    }

    @Override
    protected Result doInBackground(Params... params) {
        try {
            return doInBackgroundImpl(params);
        }
        catch(Exception | Error e) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    observer.onError();
                }
            });

            return null;
        }
    }

    @Override
    protected void onCancelled() {
        observer.onCanceled();
    }

    protected abstract Result doInBackgroundImpl(Params... params);

    protected HttpJsonRpcClientTransport transport; // Transport object
    protected ITaskObserver observer; // Task observer object
}
