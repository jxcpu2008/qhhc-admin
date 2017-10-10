package com.hc9.common.util;

import javax.servlet.http.HttpServletRequest;

/**
* <p>Title:GetIpAddress</p>
* <p>Description: 得到请求的ip地址</p>
* <p>Company: 前海红筹</p>
*/
public class GetIpAddress {

    /**
     * <p>
     * Title: getIp
     * </p>
     * <p>
     * Description: 获取ip
     * </p>
     * 
     * @param request
     *            HttpServletRequest
     * @return ip地址
     */
    public static String getIp(HttpServletRequest request) {
        
        String unknow="unknown";
        
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || unknow.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || unknow.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || unknow.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null) {
            String[] ip1 = ip.split(",");
            if (ip1.length > 1) {
                return ip1[0];
            } else {
                return ip;
            }
        } else {
            return ip;
        }

    }
}
