package rest.o.gram.commands;

import android.os.Handler;
import android.os.Looper;
import org.json.rpc.client.HttpJsonRpcClientTransport;
import rest.o.gram.common.Defs;
import rest.o.gram.tasks.ITaskObserver;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 06/05/13
 */
public abstract class AbstractRestogramCommand implements IRestogramCommand {

    public AbstractRestogramCommand() {
        observers = new HashSet<>();
        state = State.CS_Pending;
    }

    public AbstractRestogramCommand(HttpJsonRpcClientTransport transport, ITaskObserver observer) {
        this();
        this.transport = transport;
        this.observer = observer;
    }

    @Override
    public boolean execute() {
        if(state != State.CS_Pending)
            return false;

        state = State.CS_Executing;
        startTimer();
        return true;
    }

    @Override
    public boolean cancel() {
        if(state != State.CS_Executing &&
           state != State.CS_Pending)
            return false;

        state = State.CS_Canceling;
        return true;
    }

    @Override
    public void addObserver(IRestogramCommandObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(IRestogramCommandObserver observer) {
        observers.remove(observer);
    }

    @Override
    public State state() {
        return state;
    }

    @Override
    public long getTimeoutInterval() {
        return Defs.Commands.DEFAULT_SHORT_TIMEOUT;
    }

    /**
     * Notifies observers on canceled event
     */
    protected void notifyCanceled() {
        if(state == State.CS_TimedOut)
            return;

        state = State.CS_Canceled;
        stopTimer();

        for(IRestogramCommandObserver o : observers)
            o.onCanceled(this);
    }

    /**
     * Notifies observers on finished event
     */
    protected void notifyFinished() {
        state = State.CS_Finished;
        stopTimer();

        for(IRestogramCommandObserver o : observers)
            o.onFinished(this);
    }

    /**
     * Notifies observers on error event
     */
    protected void notifyError() {
        state = State.CS_Failed;
        stopTimer();

        for(IRestogramCommandObserver o : observers)
            o.onError(this);
    }

    /**
     * Notifies observers on timeout event
     */
    protected void notifyTimeout() {
        state = State.CS_TimedOut;
        stopTimer();

        for(IRestogramCommandObserver o : observers)
            o.onTimeout(this);
    }

    /**
     * Starts command timeout timer
     */
    private void startTimer() {
        if(timer != null)
            return;

        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        notifyTimeout();
                        cancel();
                    }
                });
            }

            private Handler handler = new Handler(Looper.getMainLooper());
        };

        timer.schedule(task, getTimeoutInterval());
    }

    /**
     * Stops command timeout timer
     */
    private void stopTimer() {
        if(timer == null)
            return;

        timer.cancel();
        timer.purge();
    }

    protected HttpJsonRpcClientTransport transport; // Transport object
    protected ITaskObserver observer; // Task observer object

    private State state; // Task state
    private Set<IRestogramCommandObserver> observers; // Command observers

    private Timer timer; // Timer object
}
