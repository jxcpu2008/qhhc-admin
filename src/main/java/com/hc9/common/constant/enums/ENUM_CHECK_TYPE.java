package com.hc9.common.constant.enums;

import com.hc9.common.annotation.FieldConfig;


public enum ENUM_CHECK_TYPE{
	
    /**
     * 审核中
     */
	@FieldConfig("审核中")
	CHECKING,
	/**
	 * 已通过
	 */
	@FieldConfig("已通过")
	SUCCESS,
	/**
	 * 未通过
	 */
	@FieldConfig("未通过")
	FAIL,
}
