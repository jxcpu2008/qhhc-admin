package com.hc9.common.filter;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hc9.common.servlet.CompressionResponse;

/**   
 * Company:     前海红筹  
 * @version:    3.0   
 * @since:  JDK 1.7.0_25  
 * Create at:   2015年5月24日 下午   
 * Description:  
 */

public class CompressionFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // TODO Auto-generated method stub

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        boolean compress = false;
        if (request instanceof HttpServletRequest) {
            Enumeration headers = httpRequest.getHeaders("Accept-Encoding");
            while (headers.hasMoreElements()) {
                String value = (String) headers.nextElement();
                if (value.indexOf("gzip") != -1) {
                    compress = true;
                }
            }
        }
        if (compress) {// 如果浏览器支持则压缩
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.addHeader("Content-Encoding", "gzip");
            httpResponse.setCharacterEncoding("UTF-8");
            CompressionResponse compressionResponse = new CompressionResponse(
                    httpResponse);
            
            compressionResponse.setCharacterEncoding("UTF-8");
            chain.doFilter(request, compressionResponse);
            compressionResponse.close();
        } else {// 如果浏览器不支持则不压缩
            chain.doFilter(request, response);
        }

    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }

}
