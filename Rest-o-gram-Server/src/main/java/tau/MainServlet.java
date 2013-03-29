package tau;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 3/29/13
 */
public class MainServlet extends javax.servlet.http.HttpServlet {
    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

    }

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        response.setContentType("text/plain");
        response.getWriter().println("Rest-o-Gram!!!");
    }
}
