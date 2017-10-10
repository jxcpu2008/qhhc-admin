package com.hc9.common.util;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import cn.emay.sdk.client.api.Client;

import com.hc9.common.log.LOG;

/**
 * 单例模式
 * 获取Client
 * @author frank
 *
 */
public class SingletonClient {

	private static Client client=null;
	private SingletonClient(){
	}
	/**
	 * 获取Client
	 * @param sn 亿美序列号
	 * @param key 
	 * @return
	 */
	public synchronized static Client getClient(String sn,String key){
		if(client==null){
			try {
				client=new Client(sn,key);
			} catch (Exception e) {
				LOG.error(e.getMessage());
			}
		}
		return client;
	}
	/**
	 * 用默认的序列号和key
	 * @return
	 */
	public synchronized static Client getClient(){
		ResourceBundle bundle=PropertyResourceBundle.getBundle("config");
		if(client==null){
			try {
				client=new Client(bundle.getString("softwareSerialNo"),bundle.getString("key"));
			} catch (Exception e) {
				LOG.error(e.getMessage());
			}
		}
		return client;
	}
	
	
}
