package com.hc9.common.util;

import javax.servlet.http.HttpServletRequest;

import com.hc9.common.constant.Constant;

/**
 * 获取根目录
 * 
 * @author frank
 * 
 */
public class BaseUrlUtils {

    /**
     * 获取根目录
     * 
     * @param request
     *            request
     * @return 根目录
     */
    public static String rootDirectory(HttpServletRequest request) {
//        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
    	String basePath=Constant.WEBSERVER+request.getContextPath()+ "/";
        return basePath;
    }
}
