package com.hc9.common.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.hc9.commons.log.LOG;

/**
 * 黑名单IP过滤器（测试）
 * 
 * @author frank
 * 
 */
public class BlackIPFilter implements Filter {

    /**
     * 黑名单map
     */
    public static final Map<String, Void> MAP_BLACK_IPS = new HashMap<String, Void>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        LOG.info("--->启动黑名单IP过滤器...");

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse rep,
            FilterChain chain) throws IOException, ServletException {

        if (!MAP_BLACK_IPS.containsKey(((HttpServletRequest) req)
                .getRemoteAddr())) {
            chain.doFilter(req, rep);
        }

    }

    @Override
    public void destroy() {
    }

}
