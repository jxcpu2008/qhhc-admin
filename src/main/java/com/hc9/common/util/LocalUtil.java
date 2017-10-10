package com.hc9.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import com.hc9.common.log.LOG;


/**
 * 本地工具
 * @author frank
 * 2015-1-14
 *
 */
public class LocalUtil {

    /**
     * 得到地址
     * @param ip    ip
     * @param key   key
     * @return      地址
     */
    public static String getAddress(String ip, String key) {

        String address = "未知";

        URL url = null;
        try {
            url = new URL("http://api.map.baidu.com/location/ip?ak=" + key
                    + "&ip=" + ip);
        } catch (MalformedURLException e) {
            LOG.error("不能连接到百度服务器", e);
            return address;
        }

        InputStreamReader isr = null;
        try {
            isr = new InputStreamReader(url.openStream());
        } catch (IOException e) {
            LOG.error("不能连接到百度服务器", e);
            return address;
        }

        StringBuffer sb = new StringBuffer();

        BufferedReader br = new BufferedReader(isr);
        String str;
        try {
            while ((str = br.readLine()) != null) {
                sb.append(str.trim());
            }
        } catch (IOException e) {
            LOG.error("从服务器返回的流无法读取!", e);
            return address;
        } finally {
            try {
                br.close();
                isr.close();
            } catch (IOException e) {
                LOG.error("关闭输入流失败！", e);
            }
        }

        if (sb != null && sb.length() > 10) {

            JSONObject json = JSONObject.fromObject(sb.toString());

            if ("0".equals(json.get("status").toString())) {
                json = json.getJSONObject("content");
                json = json.getJSONObject("address_detail");
                address = json.getString("city");
                if (address != null && !"".equals(address)
                        && address.length() > 1) {
                    address = address.substring(0, address.length() - 1);
                }
            }
        }

        return address;

    }
    /**
     * 获取地址的经纬度
     * @param address 比如：广东省深圳市福田区深南大道4001号
     * @param key 百度KEY
     * @return
     */
    public static Map<String,String> getGeocoderLatitude(String address,String key){
    	String longitude="";//经度
    	String latitude="";//纬度
    	Map<String,String> map = null;
    	URL url=null;
    	BufferedReader br=null;
    	InputStreamReader ir=null;
    	StringBuffer sb=new StringBuffer();
    	String str=null;
    	try {
    		url=new URL("http://api.map.baidu.com/geocoder?address="+ address +"&output=json&key="+ key);
			ir=new InputStreamReader(url.openStream(), "UTF-8");
			br=new BufferedReader(ir);
			while((str=br.readLine())!=null){
				sb.append(str.trim());
			}
			str=sb.toString();
			if (sb != null && sb.length() > 10) {
				JSONObject json = JSONObject.fromObject(sb.toString());
				if("OK".equals(json.get("status").toString())){
					json=json.getJSONObject("result");
					json=json.getJSONObject("location");
					longitude=json.get("lng").toString();
					latitude=json.get("lat").toString();
				}
			}
			map=new HashMap<String, String>();
			map.put("longitude", longitude);
			map.put("latitude",latitude);
			return map;
		} catch (IOException e) {
			LOG.error("读取数据错误，不能连接到百度服务器");
		}finally{
			try {
				br.close();
				ir.close();
			} catch (IOException e) {
				LOG.error("流关闭失败");
			}
		}
		return null;
    }
    
}
