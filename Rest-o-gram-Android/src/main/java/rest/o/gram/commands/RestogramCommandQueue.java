package rest.o.gram.commands;

import rest.o.gram.common.Defs;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Yarmo
 * Date: 06/05/13
 */
public class RestogramCommandQueue implements IRestogramCommandQueue, IRestogramCommandObserver {

    public RestogramCommandQueue() {
        pending = new ArrayDeque<>();
        executing = new HashSet<>(Defs.Commands.MAX_EXECUTING_COMMANDS);
    }

    @Override
    public boolean pushBack(IRestogramCommand command) {
        command.addObserver(this);
        pending.addLast(command);
        update();
        return true;
    }

    @Override
    public boolean pushFront(IRestogramCommand command) {
        command.addObserver(this);
        pending.addFirst(command);
        update();
        return true;
    }

    @Override
    public boolean pushForce(IRestogramCommand command) {
        // Allow only minor overflow from MAX_EXECUTING_COMMANDS
        if(executing.size() > Defs.Commands.MAX_EXECUTING_COMMANDS + 1) {
            return pushFront(command);
        }

        command.addObserver(this);
        executing.add(command);
        command.execute();
        return true;
    }

    @Override
    public void onFinished(IRestogramCommand command) {
        command.removeObserver(this);
        executing.remove(command);
        update();
    }

    @Override
    public void onError(IRestogramCommand command) {
        command.removeObserver(this);
        executing.remove(command);
        update();
    }

    /**
     * Updates this command queue
     */
    private void update() {
        while(executing.size() < Defs.Commands.MAX_EXECUTING_COMMANDS) {
            IRestogramCommand command = pending.pollFirst();
            if(command == null)
                break;

            executing.add(command);
            command.execute();
        }
    }

    private Deque<IRestogramCommand> pending; // Pending commands
    private Set<IRestogramCommand> executing; // Executing commands
}
