package com.tau;

import org.json.rpc.server.JsonRpcExecutor;
import org.json.rpc.server.JsonRpcServletTransport;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 03/04/13
 */
public class JsonRpcServlet extends HttpServlet {

    public JsonRpcServlet() {
        executor = bind();
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        executor.execute(new JsonRpcServletTransport(req, resp));
    }

    private JsonRpcExecutor bind() {
        JsonRpcExecutor executor = new JsonRpcExecutor();

        RestogramService impl = new RestogramServiceImpl();
        executor.addHandler("restogram", impl, RestogramService.class);

        // add more services here

        return executor;
    }

    private final JsonRpcExecutor executor;
}
