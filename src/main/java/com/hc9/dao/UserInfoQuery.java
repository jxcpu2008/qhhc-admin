package com.hc9.dao;

import java.text.ParseException;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.common.constant.Constant;
import com.hc9.common.util.DateUtils;
import com.hc9.dao.entity.BorrowersApply;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.entity.Userfundinfo;
import com.hc9.dao.impl.HibernateSupport;

/**
 * 关于userfundinfo 和userbasicinfo 的一些查询
 * 
 * @author Administrator 2014-5-27
 */
@Service
public class UserInfoQuery {

    /** dao */
    @Resource
    private HibernateSupport dao;

    /**
     * 通过基础信息编号找到用户资金信息
     * 
     * @param userbasicId
     *            基础信息编号
     * @return 用户资金信息
     */
    public Userfundinfo getUserFundInfoBybasicId(Long userbasicId) {
        StringBuffer sb = new StringBuffer(
                "SELECT * from userfundinfo where id=")
                .append(userbasicId);
        List<Userfundinfo> userFundList = dao.findBySql(sb.toString(),
                Userfundinfo.class, null);
        return userFundList.size() > 0 ? userFundList.get(0) : null;
    }

    /***
     * 判断传入的用户目前是否是特权会员
     * 
     * @param userbasicsinfo
     *            会员基础信息
     * @return 是否成功
     */
    public boolean isPrivilege(Userbasicsinfo userbasicsinfo) {
        try {
            StringBuffer sb = new StringBuffer(
                    "SELECT MAX(endtime) from vipinfo where user_id=")
                    .append(userbasicsinfo.getId());
            Object obj = dao.findObjectBySql(sb.toString(), null);
            if (obj != null
                    && DateUtils.isBefore(Constant.DEFAULT_TIME_FORMAT,
                            obj.toString())) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            return false;
        }
    }
    /**
     * 
     * @param id
     * @return
     */
    public BorrowersApply getBorrowersApply(Integer typeid){
    	String hql = "from BorrowersApply where type=?";
    	List<BorrowersApply> borr =  dao.find(hql,typeid);
    	return borr.size()>0 ? borr.get(0):null;
    }
    /**
     * 通过id获取BorrowersApply对象
     * @param id
     * @return
     */
    public BorrowersApply getBorrowersApplys(Long id){
    	return dao.get(BorrowersApply.class,id);
    }
    /**
     * 通过borrowers_apply id获取user id
     * @param id borrowers_apply 的id
     * @return user id
     */    
	public Long getUserId(Long id) {
		String sql="select user_id from borrowers_apply where id="+id;
		Object obj=dao.findBySql(sql).get(0);
		return Long.valueOf(obj.toString());
	}

	/** 判断是否是指定期间内注册的用户 */
	public boolean isUserRegistedInTimeArea(Long userId, String beginDate, String endDate){
		  String sql="select * from userbasicsinfo where  id=? " 
				  + "and DATE_FORMAT(createTime, '%Y-%m-%d')>=DATE_FORMAT('" + beginDate + "', '%Y-%m-%d')  " 
				  + "AND DATE_FORMAT(createTime, '%Y-%m-%d')<=DATE_FORMAT('" + endDate + "', '%Y-%m-%d') ";
		  List<Userbasicsinfo> list = dao.findBySql(sql, Userbasicsinfo.class, userId);
		  if(list.size() > 0) {
			  return true;
		  } else {
			  return false;
		  }
	}
}
