package com.hc9.service;

import java.util.List;

import net.sf.json.JSONObject;

import com.hc9.dao.entity.RepaymentRecordDetail;
import com.hc9.dao.entity.Repaymentrecord;
import com.hc9.model.RepaymentRequest;

/**
 * 还款服务接口
 * @author Administrator
 *
 */
public interface IRepayService {
	
	/**
	 * 还款
	 * @param repaymentRequest
	 * @param repaymentRecordDetails
	 */
	public JSONObject repay(RepaymentRequest repaymentRequest, List<RepaymentRecordDetail> repaymentRecordDetails) throws Exception;
	
	/**
	 * 重置提前还款申请
	 * @param repaymentRequest
	 * @param repaymentRecordDetails
	 * @return
	 * @throws Exception
	 */
	public JSONObject reverse(RepaymentRequest repaymentRequest, List<Repaymentrecord> repayRecords, List<RepaymentRecordDetail> repaymentRecordDetails) throws Exception;
	
	/**
	 * 获取当前申请提前还款详情记录列表
	 * @param repayRecordId
	 * @return
	 */
	public List<RepaymentRecordDetail> getApplyInadvanceRepayDetailRecords(long repayRecordId);
	
	/**
	 * 根据标的主键id获取对应申请提前还款记录列表
	 * @param loanId
	 * @return
	 */
	public List<Repaymentrecord> getApplyInadvanceRepayRecords(long loanId);
}