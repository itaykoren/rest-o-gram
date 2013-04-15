package com.tau.commands;

import com.tau.tasks.ITaskObserver;
import org.json.rpc.client.HttpJsonRpcClientTransport;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 15/04/13
 */
public interface IRestogramCommand {
    /**
     * Executes command
     */
    void execute(HttpJsonRpcClientTransport transport, ITaskObserver observer);
}
