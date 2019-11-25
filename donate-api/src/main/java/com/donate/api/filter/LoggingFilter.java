package com.donate.api.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.UUID;

import com.donate.common.model.ConstKeys;
import org.slf4j.MDC;

public class LoggingFilter implements Filter {

    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        boolean clear = false;
        if (MDC.get(ConstKeys.TRACE_ID) == null) {
            clear = true;
            MDC.put(ConstKeys.TRACE_ID, UUID.randomUUID().toString());

            HttpSession session = ((HttpServletRequest) request).getSession(false);
            if (session != null && session.getAttribute(ConstKeys.MAG_USER_ID) != null
                    && session.getAttribute(ConstKeys.MAG_LOGIN_NAME) != null) {
                MDC.put(ConstKeys.MAG_USER_ID, (String) session.getAttribute(ConstKeys.MAG_USER_ID));
                MDC.put(ConstKeys.MAG_LOGIN_NAME, (String) session.getAttribute(ConstKeys.MAG_LOGIN_NAME));
            }
        }

        try {
            chain.doFilter(request, response);

        } finally {
            if (clear)
                MDC.clear();
        }
    }

    public void destroy() {

    }
}
