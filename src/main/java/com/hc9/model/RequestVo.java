package com.hc9.model;

import java.util.Map;

/** 请求参数相关 */
public class RequestVo {
	/** 需要调用的方法名称  */
	private String methodName;
	
	/** 所调用方法需要的相关参数信息 */
	private Map<String, String> paramMap;

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Map<String, String> getParamMap() {
		return paramMap;
	}

	public void setParamMap(Map<String, String> paramMap) {
		this.paramMap = paramMap;
	}
}