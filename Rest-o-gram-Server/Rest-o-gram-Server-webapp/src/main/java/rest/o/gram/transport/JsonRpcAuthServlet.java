package rest.o.gram.transport;

import com.leanengine.server.auth.UsersServiceImpl;
import org.json.rpc.server.JsonRpcExecutor;
import org.json.rpc.server.JsonRpcServletTransport;
import rest.o.gram.iservice.RestogramAuthService;
import rest.o.gram.lean.UsersService;
import rest.o.gram.service.RestogramAuthServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/19/13
 */
public class JsonRpcAuthServlet extends HttpServlet {
    public JsonRpcAuthServlet() {
        executor = bind();
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        executor.execute(new JsonRpcServletTransport(req, resp));
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().println("auth-service");
    }

    private JsonRpcExecutor bind() {
        JsonRpcExecutor executor = new JsonRpcExecutor();

        RestogramAuthService impl = new RestogramAuthServiceImpl();
        executor.addHandler("restogram", impl, RestogramAuthService.class);

        UsersService usersService = new UsersServiceImpl();
        executor.addHandler("users", usersService, UsersService.class);

        // add more services here

        return executor;
    }

    private final JsonRpcExecutor executor;
}
