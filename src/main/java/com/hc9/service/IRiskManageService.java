package com.hc9.service;

import java.util.List;
import java.util.Map;

import com.hc9.model.InadvanceRepayInfo;
import com.hc9.model.PageModel;

/**
 * 风控管理接口
 * @author Jerry Wong
 *
 */
public interface IRiskManageService {

	/**
	 * 根据查询条件分页查询提前还款记录列表
	 * @param queryCondition
	 * @param page
	 * @return
	 */
	public List<InadvanceRepayInfo> queryInadvanceRepayApplyListByPage(Map<String, Object> queryCondition, PageModel page);
}