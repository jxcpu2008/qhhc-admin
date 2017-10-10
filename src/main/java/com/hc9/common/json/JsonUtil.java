package com.hc9.common.json;

import java.util.List;

import com.alibaba.fastjson.JSON;

/** json转换工具类 */
public class JsonUtil {
	/** 将json字符串转换成对象 */
	public static <T> T jsonToObject(String json, Class<T> clazz) {
		T object = JSON.parseObject(json, clazz);
		return object;
	}
	
	/** 将json字符串转换成List列表 */
	public static <T> List<T> jsonToList(String json, Class<T> clazz) {
		List<T> object = JSON.parseArray(json, clazz);
		return object;
	}
	
	/** 将对象转换成json字符串 */
	public static String toJsonStr(Object object) {
		return JSON.toJSONString(object);
	}
	
	public static void main(String[] args) {

	}
}
