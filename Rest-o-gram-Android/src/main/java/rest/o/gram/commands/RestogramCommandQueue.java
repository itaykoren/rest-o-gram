package rest.o.gram.commands;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Yarmo
 * Date: 06/05/13
 */
public class RestogramCommandQueue implements IRestogramCommandQueue, IRestogramCommandObserver {

    public RestogramCommandQueue(int maxSize) {
        this.maxSize = maxSize;
        pending = new ArrayDeque<>();
        executing = new HashSet<>();
    }

    @Override
    public boolean pushBack(IRestogramCommand command) {
        if(isCanceling)
            return false;

        command.addObserver(this);
        pending.addLast(command);
        update();
        return true;
    }

    @Override
    public boolean pushFront(IRestogramCommand command) {
        if(isCanceling)
            return false;

        command.addObserver(this);
        pending.addFirst(command);
        update();
        return true;
    }

    @Override
    public boolean pushForce(IRestogramCommand command) {
        if(isCanceling)
            return false;

        // Allow only minor overflow from maxSize
        if(executing.size() > maxSize + 1) {
            return pushFront(command);
        }

        command.addObserver(this);
        executing.add(command);
        command.execute();
        return true;
    }

    @Override
    public void cancelAll() {
        // Clear all pending commands
        pending.clear();

        // Set is canceling flag
        isCanceling = true;

        // Cancel all executing commands
        for(IRestogramCommand command : executing)
            command.cancel();

        // Clear all executing commands
        executing.clear();

        // Reset is canceling flag
        isCanceling = false;
    }

    @Override
    public void onCanceled(IRestogramCommand command) {
        if(isCanceling)
            return;

        if(pending.contains(command))
            pending.remove(command);
        else if(executing.contains(command))
            executing.remove(command);

        update();
    }

    @Override
    public void onFinished(IRestogramCommand command) {
        if(isCanceling)
            return;

        executing.remove(command);
        update();
    }

    @Override
    public void onError(IRestogramCommand command) {
        if(isCanceling)
            return;

        executing.remove(command);
        update();
    }

    @Override
    public void onTimeout(IRestogramCommand command) {
        if(isCanceling)
            return;

        executing.remove(command);
        update();
    }

    /**
     * Updates this command queue
     */
    private void update() {
        while(executing.size() < maxSize) {
            IRestogramCommand command = pending.pollFirst();
            if(command == null)
                break;

            executing.add(command);
            command.execute();
        }
    }

    private int maxSize; // Max queue size
    private Deque<IRestogramCommand> pending; // Pending commands
    private Set<IRestogramCommand> executing; // Executing commands
    private boolean isCanceling = false; // Is canceling flag
}
