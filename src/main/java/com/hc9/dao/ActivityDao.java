package com.hc9.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.jfree.util.Log;
import org.springframework.stereotype.Service;

import com.hc9.common.redis.QiXiActivitiCache;
import com.hc9.common.util.Arith;
import com.hc9.common.util.DateFormatUtil;
import com.hc9.common.util.StatisticsUtil;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.VoteIncome;
import com.hc9.dao.entity.VoteRecord;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;
import com.hc9.model.VoteRecordVo;
import com.jubaopen.commons.LOG;

/** 活动相关Dao */
@Service
public class ActivityDao {
	/** 注入数据库操作层 */
	@Resource
	private HibernateSupport dao;
	
	/** 记录投票人、被投票人基本信息 */
	public void saveVoteRecord(Long voterId, String votedId) {
		String sql = "select * from voterecord where voterId=? and votedId=?";
		List list = dao.findBySql(sql, voterId, votedId);
		if(list != null && list.size() > 0) {
			Log.error("数据库中已保存了该用户的相关投票信息！");
		} else {
			String createTime = DateFormatUtil.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
			VoteRecord voteRecord = new VoteRecord();
			voteRecord.setVoterId(voterId);
			voteRecord.setVotedId(votedId);
			voteRecord.setStatus(0);
			voteRecord.setTotalNum(Long.valueOf(1));
			voteRecord.setCreateTime(createTime);
			dao.save(voteRecord);
		}
	}
	
	/** 分页查询七夕活动相关投票人列表 */
	public List<VoteRecordVo> querySevenMoonList(PageModel page, String votedId) {
		List<VoteRecordVo> voteRecordList = new ArrayList<VoteRecordVo>();
		
		String select = "select v.id,v.voterId,v.votedId,v.totalNum,v.status,v.createTime,u.name ";
		String fromSql = " from voterecord v,userbasicsinfo u where u.id=v.voterId ";

		if (StringUtil.isNotBlank(votedId)) {
			fromSql += "and v.votedId='" + votedId + "'";
		}
		String orderBy = " order by v.id desc";

		String querySql = select + fromSql + orderBy;

		String countSql = "select count(v.id) " + fromSql;
		List list = dao.pageListBySql(page, countSql, querySql, null);
		if (list != null && list.size() > 0) {
			for (Object obj : list) {
				Object[] arr = (Object[]) obj;
				VoteRecordVo vo = new VoteRecordVo();
				vo.setId(StatisticsUtil.getLongFromBigInteger(arr[0]));
				vo.setVoterId(StatisticsUtil.getLongFromBigInteger(arr[1]));
				vo.setVotedId(StatisticsUtil.getStringFromObject(arr[2]));
				vo.setTotalNum(StatisticsUtil.getLongFromBigInteger(arr[3]));
				vo.setStatus(StatisticsUtil.getIntegerFromObject(arr[4]));
				vo.setCreateTime(StatisticsUtil.getStringFromObject(arr[5]));
				vo.setVoterName(StatisticsUtil.getStringFromObject(arr[6]));
				voteRecordList.add(vo);
			}
		}
		
		return voteRecordList;
	}
	
	/** 分页查询七夕活动相关投票人列表 */
	public List<VoteRecord> queryAllVoteRecordList() {
		List<VoteRecord> voteRecordList = new ArrayList<VoteRecord>();
		String sql = "select * from voterecord";
		voteRecordList = dao.findBySql(sql, VoteRecord.class);
		return voteRecordList;
	}
	
	/** 更新投票总数信息 */
	public void updateVoteStatisticsInfo(VoteRecord voteRecord) {
		Long voterId = voteRecord.getVoterId();
		String votedId = voteRecord.getVotedId();
		Long totalNum = voteRecord.getTotalNum();
		/** redis中缓存的选手所支持的投票人的票数 */
		Long totalNumOfVotedIdByVoterId = QiXiActivitiCache.getUserVoteNumOfVotedId(voterId, votedId);
		if(totalNumOfVotedIdByVoterId > totalNum) {
			Long id = voteRecord.getId();
			String sql = "update voterecord set totalNum=? where id=?";
			dao.executeSql(sql, totalNumOfVotedIdByVoterId, id);
		}
	}
	
	/** 查询投票排行榜列表 */
	public List<VoteRecordVo> queryVoteRankList() {
		List<VoteRecordVo> voteRecordList = new ArrayList<VoteRecordVo>();
		String sql = "select sum(totalNum) as totalNum,votedId from voterecord group by votedId order by totalNum desc";
		List list = dao.findBySql(sql);
		if (list != null && list.size() > 0) {
			for (Object obj : list) {
				VoteRecordVo vo = new VoteRecordVo();
				Object[] arr = (Object[]) obj;
				BigDecimal val = (BigDecimal)arr[0];
				Long totalNum = val.longValue();
				String votedId = StatisticsUtil.getStringFromObject(arr[1]);
				Long votedInTotalNum = Long.valueOf(QiXiActivitiCache.getVoteNumByVotedId(votedId));
				if(totalNum > votedInTotalNum) {
					QiXiActivitiCache.setVoteNumByVotedId(votedId, totalNum);
				}
				vo.setTotalNum(totalNum);
				vo.setVotedId(votedId);
				voteRecordList.add(vo);
			}
		}
		return voteRecordList;
	}
	
	/** 查询赢家的下的有投资记录的投票人列表 */
	public List<VoteRecordVo> queryVoteRecordListByVotedId(String winSideId) {
		List<VoteRecordVo> resultList = new ArrayList<VoteRecordVo>();
		/** 活动开始时间 */
		String beginDateStr = QiXiActivitiCache.getQiXiBeginDate();
		/** 活动结束时间 */
		String endDateStr = QiXiActivitiCache.getQiXiEndDate();
		StringBuffer sql = new StringBuffer("select v.voterId,l.loanSign_id,l.id,l.tenderMoney,s.remonth ");
		sql.append("from voterecord v, loanrecord l,loansign s ")
		.append("where v.voterId=l.userbasicinfo_id and l.isSucceed=1 and l.loanSign_id=s.id and v.votedId=? ")
		.append(" and l.tenderTime>=? and l.tenderTime<=? and l.tenderMoney>0 and l.subType=1");
		List list = dao.findBySql(sql.toString(), winSideId, beginDateStr, endDateStr);
		if(list != null && list.size() > 0) {
			for (Object obj : list) {
				Object[] arr = (Object[]) obj;
				Long voterId = StatisticsUtil.getLongFromBigInteger(arr[0]);
				if(isQualificationVerification(voterId, winSideId)) {
					VoteRecordVo vo = new VoteRecordVo();
					vo.setVoterId(voterId);
					vo.setLoanSignId(StatisticsUtil.getLongFromBigInteger(arr[1]));
					vo.setLoanRecordId(StatisticsUtil.getLongFromBigInteger(arr[2]));
					vo.setTenderMoney(StatisticsUtil.getDoubleFromBigdecimal((BigDecimal)arr[3]));
					vo.setRemonth(StatisticsUtil.getIntegerFromObject(arr[4]));
					resultList.add(vo);
				}
			}
		}
		return resultList;
	}
	
	/** 判断投票人是否符合要求 */
	private boolean isQualificationVerification(Long voterId, String votedId) {
		boolean validFlag = false;
		/** 用户在投资多人情况下，以投资单人票数最多为准；
		 *  如果存在投票次数都一样的话，以首投的对象为准 */
		String sql = "select * from voterecord where voterId=? order by totalnum desc,createtime asc";
		List<VoteRecord> list = dao.findBySql(sql, VoteRecord.class, voterId);
		if(list != null && list.size() > 0) {
			VoteRecord vo = list.get(0);
			if(votedId.equals(vo.getVotedId())) {
				validFlag = true;
			}
		}
		return validFlag;
	}
	
	/**  判断是否已经计算过 */
	public boolean isSavedVoteIncome() {
		String sql = "select * from voteincome";
		List<VoteIncome> list = dao.findBySql(sql, VoteIncome.class);
		if(list != null && list.size() > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	/** 计算赢家下投票人的七夕活动加息券收益并保存入数据库 */
	public boolean computeAndSaveInComeOfQixi(List<VoteRecordVo> voteRecordList) {
		boolean updateFlag = false;
		String createTime = DateFormatUtil.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
		List<VoteIncome> voteIncomeList = new ArrayList<VoteIncome>();
		for(VoteRecordVo vo : voteRecordList) {
			Long voterId = vo.getVoterId();
			Long loanId = vo.getLoanSignId();
			Long loanRecordId = vo.getLoanRecordId();
			Double tenderMoney = vo.getTenderMoney();
			BigDecimal yearIncome = new BigDecimal(Arith.mul(tenderMoney, 0.01));
			BigDecimal oneMonthIncome = yearIncome.divide(new BigDecimal(12), 2);
			BigDecimal totalInterest = oneMonthIncome.multiply(new BigDecimal(vo.getRemonth()));
			double incomeMoney = Arith.round(totalInterest, 2).doubleValue();
			int status = 0;
			VoteIncome voteIncome = new VoteIncome();
			voteIncome.setVoterId(voterId);
			voteIncome.setLoanId(loanId);
			voteIncome.setLoanRecordId(loanRecordId);
			voteIncome.setIncomeMoney(incomeMoney);
			voteIncome.setStatus(status);
			voteIncome.setCreateTime(createTime);
			voteIncome.setType(1);
			voteIncomeList.add(voteIncome);
		}
		if(voteIncomeList.size() > 0) {
			dao.saveOrUpdateAll(voteIncomeList);
			updateFlag = true;
		} else {
			LOG.error("没有合适的记录需要保存！");
		}
		return updateFlag;
	}
}