package com.hc9.common.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.CharacterEncodingFilter;

public class CharacterFilter extends CharacterEncodingFilter {

	private String encoding;

	private boolean forceEncoding = false;
	
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	public void setForceEncoding(boolean forceEncoding) {
		this.forceEncoding = forceEncoding;
	}

	@Override
	protected void doFilterInternal(
			HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
//		System.out.println(request.getRequestURI());
		if (encoding != null && (this.forceEncoding || request.getCharacterEncoding() == null)) {
			request.setCharacterEncoding(this.encoding);
			if (this.forceEncoding) {
				response.setCharacterEncoding(this.encoding);
			}
		}
		String uri=request.getQueryString();
		if(uri!=null && (uri.toLowerCase().indexOf("script")>0)){
			response.sendRedirect(null);
		}else{
			filterChain.doFilter(request, response);
		}
//		LOG.info("-------------------->showtime------>>RequestURI"+request.getRequestURI()+"------>>QueryString"+request.getQueryString());
		
	}
	

}
