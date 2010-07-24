package org.allmon.client.agent.advices;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class HttpServletCallFilter implements Filter {

	public void init(FilterConfig config) throws ServletException {
		
	}

	public void destroy() {
		
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
		throws IOException, ServletException {

		// start 
		
		try {
			chain.doFilter(request, response);
			
			// log responses codes
			
			// log http errors codes
			
		} catch (Throwable error) {
			// log exceptions
			
		} finally {
			// stop
		}
		
	}

}