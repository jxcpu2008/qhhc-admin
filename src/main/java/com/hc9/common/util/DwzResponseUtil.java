package com.hc9.common.util;

import net.sf.json.JSONObject;

public class DwzResponseUtil {
	/**
	 * 返回数据时设置需要返回的json对象的属性
	 * @param json 返回的json数据
	 * @param statusCode 状态(200成功 300失败 301超时)
	 * @param message 提示信息
	 * @param navTabId 要刷新的页面(页面的rel属性值)
	 * @callbackType 回调要关闭的页面(页面的rel属性值)closeCurrent(关闭当前页)
	 * @return
	 */
	public static JSONObject setJson(JSONObject json, String statusCode, String message, String navTabId,String callbackType){
		json.element("statusCode", statusCode);
		json.element("message", message);
		json.element("navTabId", navTabId);
		if(callbackType != ""){
			json.element("callbackType", callbackType);
		}
		return json;
	}
}
