package com.leanengine.server.auth;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/21/13
 */
public class JsonAuthFilter implements Filter {

    private static final Logger log = Logger.getLogger(JsonAuthFilter.class.getName());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // nothing to do
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        // check the session support
        HttpSession session = httpServletRequest.getSession();
        if (session.getId() == null) {
            log.severe("Session support NOT enabled.");
            HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
            httpResponse.sendError(500, "Server not properly configured: sessions not enabled");
            return;
        }

        String token = httpServletRequest.getHeader("lean_token");

        if (token != null)
            AuthService.startAuthSession(token);

        filterChain.doFilter(servletRequest, servletResponse);

        if (token != null)
            AuthService.finishAuthSession();
    }

    @Override
    public void destroy() {
        // nothing to do
    }
}
