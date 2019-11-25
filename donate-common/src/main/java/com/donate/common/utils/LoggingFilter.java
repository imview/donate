package com.donate.common.utils;

import com.donate.commmon.model.ConstKeys;
import org.apache.log4j.MDC;

import javax.servlet.*;
import java.io.IOException;
import java.util.UUID;

public class LoggingFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		//super.()
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		boolean clear = false;
		if (MDC.get(ConstKeys.TRACE_ID) == null) {
			clear = true;
			MDC.put(ConstKeys.TRACE_ID, UUID.randomUUID().toString());
		}

		try {
			chain.doFilter(request, response);
			
		} finally {
			if (clear)
				MDC.clear();
		}
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

}
