package com.tau.commands;

import com.tau.tasks.GetPhotosTask;
import com.tau.tasks.ITaskObserver;
import org.json.rpc.client.HttpJsonRpcClientTransport;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 15/04/13
 */
public class GetPhotosCommand implements IRestogramCommand {

    public GetPhotosCommand(String venueID) {
        this.venueID = venueID;
    }

    @Override
    public void execute(HttpJsonRpcClientTransport transport, ITaskObserver observer) {
        GetPhotosTask task = new GetPhotosTask(transport, observer);
        task.execute(venueID);
    }

    private String venueID;
}
