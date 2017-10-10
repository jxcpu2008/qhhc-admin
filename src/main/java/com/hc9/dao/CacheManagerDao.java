package com.hc9.dao;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.common.redis.activity.year2016.month05.GenInvestUserVo;
import com.hc9.common.util.StatisticsUtil;
import com.hc9.dao.entity.PrizeDetail;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.LoginRelVo;

/** 系统缓存管理相关dao */
@Service
public class CacheManagerDao {

	/** 注入数据库操作层 */
	@Resource
	private HibernateSupport dao;
	
	/** 查询用户缓存相关信息:用户名，手机，密码、用户显示名称，email状态，实名状态，宝付状态，锁定状态 */
	@SuppressWarnings("rawtypes")
	public List<LoginRelVo> queryUserLoginRelCache() {
		List<LoginRelVo> resultList = new ArrayList<LoginRelVo>();
		String sql = "select u.id,u.userName,r.phone,u.name,u.password,u.isLock,u.cardStatus,r.emailisPass,u.isAuthIps,u.pMerBillNo,u.staff_no " + 
				"from userbasicsinfo u , userrelationinfo r where u.id=r.id";
		List list = dao.findBySql(sql);
		if (list != null && list.size() > 0) {
			for (Object obj : list) {
				Object[] arr = (Object[]) obj;
				LoginRelVo vo = new LoginRelVo();
				vo.setId(StatisticsUtil.getLongFromBigInteger(arr[0]));
				vo.setUserName(StatisticsUtil.getStringFromObject(arr[1]));
				vo.setPhone(StatisticsUtil.getStringFromObject(arr[2]));
				vo.setName(StatisticsUtil.getStringFromObject(arr[3]));
				vo.setPassword(StatisticsUtil.getStringFromObject(arr[4]));
				vo.setIsLock(StatisticsUtil.getIntegerFromObject(arr[5]));
				vo.setCardStatus(StatisticsUtil.getIntegerFromObject(arr[6]));
				vo.setEmailisPass(StatisticsUtil.getIntegerFromObject(arr[7]));
				vo.setIsAuthIps(StatisticsUtil.getIntegerFromObject(arr[8]));
				vo.setpMerBillNo(StatisticsUtil.getStringFromObject(arr[9]));
				vo.setStaffNo(StatisticsUtil.getStringFromObject(arr[10]));
				resultList.add(vo);
			}
		}
		return resultList;
	}
	
	/** 根据登录账号和手机号查询当前注册用户的信息 */
	public LoginRelVo queryLoginRelVoBy(String userName, String phone) {
		String sql = "select u.id,u.userName,r.phone,u.name,u.password,u.isLock,u.cardStatus,r.emailisPass,u.isAuthIps,u.pMerBillNo,u.staff_no " + 
				"from userbasicsinfo u , userrelationinfo r where u.id=r.id and (u.userName=? or r.phone=?)";
		List list = dao.findBySql(sql, userName, phone);
		if (list != null && list.size() > 0) {
			Object obj = list.get(0);
			Object[] arr = (Object[]) obj;
			LoginRelVo vo = new LoginRelVo();
			vo.setId(StatisticsUtil.getLongFromBigInteger(arr[0]));
			vo.setUserName(StatisticsUtil.getStringFromObject(arr[1]));
			vo.setPhone(StatisticsUtil.getStringFromObject(arr[2]));
			vo.setName(StatisticsUtil.getStringFromObject(arr[3]));
			vo.setPassword(StatisticsUtil.getStringFromObject(arr[4]));
			vo.setIsLock(StatisticsUtil.getIntegerFromObject(arr[5]));
			vo.setCardStatus(StatisticsUtil.getIntegerFromObject(arr[6]));
			vo.setEmailisPass(StatisticsUtil.getIntegerFromObject(arr[7]));
			vo.setIsAuthIps(StatisticsUtil.getIntegerFromObject(arr[8]));
			vo.setpMerBillNo(StatisticsUtil.getStringFromObject(arr[9]));
			vo.setStaffNo(StatisticsUtil.getStringFromObject(arr[10]));
			return vo;
		}
		return null;
	}
	
	/** 满标放款根据项目id更新用户的回款记录 */
	public List<Long> queryUserIdListByLoanSignId(Long loanSignId) {
		List<Long> userIdList = new ArrayList<Long>();
		String sql = "select distinct r.userbasicinfo_id from loansign l , loanrecord r where l.id=r.loanSign_id and l.id=?";
		List list = dao.findBySql(sql, loanSignId);
		if(list != null && list.size() > 0) {
			for(Object obj : list) {
				BigInteger bigObj = (BigInteger)obj;
				long userId = bigObj.longValue();
				userIdList.add(userId);
			}
		}
		return userIdList;
	}
	
	/** 查询所有已经发布的标id */
	public List<Long> queryAllLoanIdList() {
		List<Long> loanIdList = new ArrayList<Long>();
		String sql = "select id from loansign where status !=0 order by id desc";

		List list = dao.findBySql(sql);
		if(list != null && list.size() > 0) {
			for(Object obj : list) {
				BigInteger bigObj = (BigInteger)obj;
				long loanId = bigObj.longValue();
				loanIdList.add(loanId);
			}
		}
		return loanIdList;
	}
	
	/** 根据用户id查询用户奖品信息列表 */
	public List<PrizeDetail> queryPrizeDetailListByUserIds(String[] ids) {
		 List<PrizeDetail> list = new ArrayList<PrizeDetail>();
		 String sql = "select * from prizedetail where prizeType=17 and userId in(";
		 for(int i = 0; i < ids.length; i++) {
			 sql += ids[i];
			 if(i < ids.length - 1) {
				 sql += ",";
			 }
		 }
		 sql += ")";
		 list = dao.findBySql(sql, PrizeDetail.class);
		 return list;
	}
	
	/** 根据用户id删除对应的聚橙网门票奖品信息 */
	public void deleteJuChengTicketForGenUser(String[] ids) {
		String sql = "delete from prizedetail where prizeType=17 and userId in(";
		 for(int i = 0; i < ids.length; i++) {
			 sql += ids[i];
			 if(i < ids.length - 1) {
				 sql += ",";
			 }
		 }
		 sql += ")";
		 dao.executeSql(sql);
		 
		 sql = "delete from activity_monkey where type=16 and userId in(";
		 for(int i = 0; i < ids.length; i++) {
			 sql += ids[i];
			 if(i < ids.length - 1) {
				 sql += ",";
			 }
		 }
		 sql += ")";
		 dao.executeSql(sql);
	}
	
	/** 查询符合推荐投资送聚橙网门票用户数据列表 */
	public List<GenInvestUserVo> queryGenInvestUserList() {
		String benginTime = "2016-05-24 00:00:00";
		String endTime = "2016-06-13 23:59:59";
		String sql = "select l.userbasicinfo_id,l.loanSign_id,l.id,l.tenderMoney,g.genuid "
				+ "from loanrecord l, userbasicsinfo u, generalize g " 
				+ "where u.user_type in (1,6) and u.createTime>=? and u.createTime<=? and u.isAuthIps=1 " 
				+ "and l.tenderTime>=? and l.tenderTime<=? " 
				+ "and l.id not in (select loanrecordid from prizedetail where prizetype=17) "
				+ "and u.id=l.userbasicinfo_id and l.tenderMoney>=30000 and l.isSucceed=1 and l.subType in(1,2) "
				+ "and l.userbasicinfo_id =g.uid and g.genuid in(select id from userbasicsinfo where user_type in (1,2,6)) "
				+ "and l.userbasicinfo_id not in (select b.id from channelspreaddetail c,userbasicsinfo b " 
				+ "where spreadid='NAIozGva' and b.userName=c.regUserName and b.isAuthIps=1) "
				+ "order by l.id asc";
		List list = dao.findBySql(sql, benginTime, endTime, benginTime, endTime);
		List<GenInvestUserVo> userList = new ArrayList<GenInvestUserVo>();
		if(list != null && list.size() > 0) {
			for (Object obj : list) {
				Object[] arr = (Object[]) obj;
				
				GenInvestUserVo vo = new GenInvestUserVo();
				vo.setUserId(StatisticsUtil.getLongFromBigInteger(arr[0]));
				vo.setLoanSignId(StatisticsUtil.getLongFromBigInteger(arr[1]));
				vo.setLoanRecordId(StatisticsUtil.getLongFromBigInteger(arr[2]));
				vo.setInvestMoney(StatisticsUtil.getDoubleFromBigdecimal((BigDecimal) arr[3]));
				vo.setGenUserId(StatisticsUtil.getLongFromBigInteger(arr[4]));
				
				userList.add(vo);
			}
		}
		return userList;
	}
}
