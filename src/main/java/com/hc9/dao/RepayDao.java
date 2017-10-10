package com.hc9.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import com.hc9.dao.entity.Accountinfo;
import com.hc9.dao.entity.Accounttype;
import com.hc9.dao.entity.Loandynamic;
import com.hc9.dao.entity.Loansign;
import com.hc9.dao.entity.RepaymentRecordDetail;
import com.hc9.dao.entity.Repaymentrecord;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.entity.Userfundinfo;
import com.hc9.dao.entity.VoteIncome;
import com.hc9.dao.impl.HibernateSupport;

/**
 * 还款对应的数据库操作
 * @author Jerry Wong
 *
 */
@Component
public class RepayDao {
	
	@Autowired
	private HibernateSupport commonDao;
	
	/**
	 * 保存提前还款申请记录
	 * @param repaymentRecordDetails
	 * @throws DataAccessException
	 */
	public void saveInAdvanceRepayApply(List<RepaymentRecordDetail> repaymentRecordDetails) throws DataAccessException {
		commonDao.saveOrUpdateAll(repaymentRecordDetails);
	}
	
	/**
	 * 设置还款记录的还款状态为提前还款中
	 * @param repaymentRecordId 还款记录主键id
	 * @return 受影响的行数
	 */
	public int setRepaymentRecordInAdvanceRepayState(long repaymentRecordId) {
		String sql = " update repaymentrecord set repayState = 6 where id = ? ";
		return commonDao.executeSql(sql, repaymentRecordId);
	}
	
	/**
	 * 设置还款详细记录的还款状态为1-成功
	 * @param repaymentRecordId 还款记录主键id
	 * @return
	 */
	public int setRepaySuccess4RepaymentRecordDetail(int repayState, long repaymentRecordId, String repayType) {
		String sql = " update repaymentrecordparticulars set repState = ? where repaymentrecordId = ? ";
		if (repayType != null && repayType.equals("inadvanceRepay")) {
			sql += " and repState = 2 ";
		}
		return commonDao.executeSql(sql, repayState, repaymentRecordId);
	}
	
	/**
	 * 记录资金流水账
	 * @param infos
	 * @throws DataAccessException
	 */
	public void saveAccount(List<Accountinfo> infos) throws DataAccessException {
		commonDao.saveOrUpdateAll(infos);
	}
	
	/**
	 * 记录资金流水账
	 * @param info
	 * @throws DataAccessException
	 */
	public void saveAccount(Accountinfo info) throws DataAccessException {
		commonDao.save(info);
	}
	
	/**
	 * 获取资金流水账类型
	 * @param id
	 * @return
	 * @throws DataAccessException
	 */
	public Accounttype getAccounttype(long id) throws DataAccessException {
		return commonDao.get(Accounttype.class, id);
	}
	
	/**
	 * 更新用户资金余额
	 * @param infos
	 * @throws DataAccessException
	 */
	public void updateUserFundBalance(List<Userfundinfo> infos) throws DataAccessException {
		commonDao.saveOrUpdateAll(infos);
	}
	
	/**
	 * 更新用户资金余额
	 * @param info
	 * @throws DataAccessException
	 */
	public void updateUserFundBalance(Userfundinfo info) throws DataAccessException {
		commonDao.update(info);
	}
	
	/**
	 * 根据主键id查询当前还款记录
	 * @param repaymentRecordId
	 * @return
	 * @throws DataAccessException
	 */
	public Repaymentrecord getRepaymentRecordById(long repaymentRecordId) throws DataAccessException {
		return commonDao.get(Repaymentrecord.class, repaymentRecordId);
	}
	
	/**
	 * 根据标的主键id获取融资人还款记录列表
	 * @param loanId
	 * @return
	 */
	public List<Repaymentrecord> getRepayRecords(long loanId) {
		String sql = "select * from repaymentrecord where repaymentrecord.loanSign_id = ?";
		return commonDao.findBySql(sql, Repaymentrecord.class, loanId);
	}
	
	/**
	 * 更新当前还款记录
	 * @param repayRecord
	 * @throws DataAccessException
	 */
	public void updateRepaymentRecord(Repaymentrecord repayRecord) throws DataAccessException {
		commonDao.update(repayRecord);
	}
	
	/**
	 * 更新当前还款记录列表
	 * @param repayRecords
	 * @throws DataAccessException
	 */
	public void updateRepaymentRecords(List<Repaymentrecord> repayRecords) throws DataAccessException {
		commonDao.saveOrUpdateAll(repayRecords);
	}
	
	/**
	 * 更新标的的状态为10-提前还款
	 * @param loan
	 * @throws DataAccessException
	 */
	public void setInadvanceRepayed4Loan(Loansign loan) throws DataAccessException {
		commonDao.update(loan);
	}
	
	/**
	 * 保存项目动态信息
	 * @param record
	 * @throws DataAccessException
	 */
	public void saveLoanDynamic(Loandynamic record) throws DataAccessException {
		commonDao.save(record);
	}
	
	/**
	 * 查询加息奖励列表
	 * @param loanId
	 * @return
	 */
	public List<VoteIncome> getInterestBonusList(long loanId) {
		String sql = " select * from voteIncome where status = 0 and loanId = ? ";
		return commonDao.findBySql(sql, VoteIncome.class, loanId);
	}
	
	/**
	 * 根据主键id查询用户
	 * @param userId
	 * @return
	 * @throws DataAccessException
	 */
	public Userbasicsinfo getUserById(long userId) throws DataAccessException {
		return commonDao.get(Userbasicsinfo.class, userId);
	} 
	
	/**
	 * 根据当前还款记录主键id获取申请提前还款的还款详细记录列表
	 * @param repayRecordId
	 * @return
	 */
	public List<RepaymentRecordDetail> getApplyInadvanceRecords(long repayRecordId) {
		String sql = "select * from repaymentrecordparticulars rrp where rrp.repaymentrecordId = ? and rrp.repState = 2";
		return commonDao.findBySql(sql, RepaymentRecordDetail.class, repayRecordId);
	}
	
	/**
	 * 根据标的主键id获取融资人申请还款记录列表
	 * @param loanId
	 * @return
	 */
	public List<Repaymentrecord> getApplyInadvanceRepayRecords(long loanId) {
		String sql = "select * from repaymentrecord where repaymentrecord.loanSign_id = ? and repaymentrecord.repayState = 6";
		return commonDao.findBySql(sql, Repaymentrecord.class, loanId);
	}
	
	/**
	 * 删除申请提前还款的还款详细记录列表
	 * @param records
	 * @throws DataAccessException
	 */
	public void deleteApplyInadvanceRecords(List<RepaymentRecordDetail> records) throws DataAccessException {
		commonDao.deleteAll(records);
	}
}