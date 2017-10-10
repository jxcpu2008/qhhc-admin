package com.hc9.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.hc9.common.util.LocalUtil;
import com.hc9.commons.log.LOG;

/**
 * 获得本地服务
 * 
 * @author frank
 * 
 */
@Service
public class LocalService {

    /**
     * 百度key
     */
    private String key;

    /**
     * 构造函数
     */
    public LocalService() {
    }

    /**
     * 构造函数
     * 
     * @param key
     *            key
     */
    public LocalService(String key) {
        this.key = key;
        LOG.info("--->初始化百度KEY成功！");
    }

    /**
     * 得到请求者的IP
     * 
     * @param request   请求
     * @return          ip
     */
    public String getRequesterIP(HttpServletRequest request) {

        String ip = request.getHeader("x-forwarded-for");

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
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

    /**
     * 得到请求者的地址
     * 
     * @param ip    ip
     * @return      地址
     */
    public String getRequesterAddressByIP(String ip) {
        return LocalUtil.getAddress(ip, key);
    }

    /**
     * 得到请求者的地址
     * 
     * @param request   请求
     * @return          地址
     */
    public String getRequesterAddressByRequeste(HttpServletRequest request) {
        return LocalUtil.getAddress(getRequesterIP(request), key);
    }
    /**
     * 获取地址的经纬度
     * @param address 比如：广东省深圳市福田区深南大道4001号或者广东省深圳市福田区国际科技大厦
     * @return Map<String,String> key:longitude经度，key:latitude纬度
     */
    public Map<String,String> getAddressCoordinate(String address){
    	return LocalUtil.getGeocoderLatitude(address, key);
    }
}
