package com.hc9.common.constant.enums;

import com.hc9.common.annotation.FieldConfig;

/**
 * 栏目是否可删除
 * @author My_Ascii
 *
 */
public enum ENUM_COLUMN_ISFIXED{
	/**
	 * 可删除
	 */
	@FieldConfig("可删除")
	DELETE,
	
	/**
	 * 不可删除
	 */
	@FieldConfig("不可删除")
	UNDELETE,
	
	/**
	 * 不可修改
	 */
	@FieldConfig("不可修改")
	UNUPDATE,
}
