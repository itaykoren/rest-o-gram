package com.leanengine.server.auth;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Logger;

public class AuthFilter implements Filter {

    private static final Logger log = Logger.getLogger(AuthFilter.class.getName());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // nothing to do
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        // REST requests have auth_token set as parameter
        String token = httpServletRequest.getParameter("lean_token");

        // check the session support
        HttpSession session = httpServletRequest.getSession();
        if (session.getId() == null) {
            log.severe("Session support NOT enabled.");
            HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
            httpResponse.sendError(500, "Server not properly configured: sessions not enabled");
            return;
        }

        // if we did not get token from parameters, try getting it from session
        if (token == null) {
            // Web requests have auth_token set in session
            final Object tok = session.getAttribute("lean_token");
            if (tok != null)
                token = (String)tok;
        }

        if (token != null)
        {
            if (!AuthService.startAuthSession(token))
                log.warning("cannot start auth session");
        }

        filterChain.doFilter(servletRequest, servletResponse);

        if (token != null)
            AuthService.finishAuthSession();
    }

    @Override
    public void destroy() {
        // nothing to do
    }
}
