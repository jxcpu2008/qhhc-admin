package com.hc9.common.constant.enums;

import com.hc9.common.annotation.FieldConfig;
/**
 * 是否显示状态enum
 * 0显示、1不显示
 * @author Administrator
 *
 */
public enum ENUM_SHOW_STATE{
    /**
     * 否
     */
	@FieldConfig("否")
	FALSE,
	/**
	 * 是
	 */
	@FieldConfig("是")
	TRUE,
}
