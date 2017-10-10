package com.hc9.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.common.redis.RedisHelper;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.entity.Userrelationinfo;
import com.hc9.dao.impl.HibernateSupport;

/**
 * Userbasicinfo CRUD操作
 * @author frank
 *
 */
@Service
public class UserbasicsinfoService {
	
	/**
	 * 注入HibernateSupport
	 */
	@Resource
	private HibernateSupport dao;
	
	/**
	 * 查询会员个人信息
	 * 
	 * @param id
	 *            会员编号
	 * @return 返回会员信息
	 */
	public Userbasicsinfo queryUserById(Long id) {
		return dao.get(Userbasicsinfo.class, id);
	}

	
	/**
	 * 查询用户id
	 * @param merBillNo
	 * @return
	 */
	public Userbasicsinfo queryUserByMerBillNo(String merBillNo) {
		StringBuffer sb=new StringBuffer("SELECT * FROM userbasicsinfo WHERE userbasicsinfo.pMerBillNo=?");
		String sql=sb.toString();
		
		return dao.findObjectBySql(sql, Userbasicsinfo.class, merBillNo);
	}
	
	
	/***
	 * 根据手机号码查询
	 * @param phone
	 * @return
	 */
	public Userrelationinfo queryUserByPhone(String phone) {
		StringBuffer sb=new StringBuffer("SELECT * FROM userrelationinfo WHERE userrelationinfo.phone=?");
		String sql=sb.toString();
		return dao.findObjectBySql(sql, Userrelationinfo.class, phone);
	}
	
	/**
	 * 查询用户id
	 * @param userid
	 * @return
	 */
	public Userbasicsinfo queryUserById(String userid) {
		String sql="SELECT * FROM userbasicsinfo WHERE id=?";
		return dao.findObjectBySql(sql, Userbasicsinfo.class, userid);
	}
	

	/**
	 * 修改会员信息
	 * 
	 * @param userbasicsinfo
	 *            会员
	 */
	public void update(Userbasicsinfo userbasicsinfo) {
		dao.update(userbasicsinfo);
	}
	

    /**
     * 根据用户名查询用户信息
     * 
     * @param username
     *            用户名
     * @return 用户基本信息
     */
    public List<Userbasicsinfo> queryUserByusername(String username) {
        StringBuffer sb = new StringBuffer(
                "SELECT * from userbasicsinfo where username='").append(
                username).append("'");
        List<Userbasicsinfo> list = dao.findBySql(sb.toString(),
                Userbasicsinfo.class);
        return list;
    }
    
    /**
     * 名查询用户信息
     * @param on 
     * @param isespecialuser 
     * 
     * @param username
     *            用户名
     * @return 用户基本信息
     */
    public List<Userbasicsinfo> queryUsers(String isespecialuser, Integer on) {
        StringBuffer sb = new StringBuffer("SELECT * from userbasicsinfo where username like '%'");
        
        if(null != isespecialuser && !"".equals(isespecialuser)&& !"0".equals(isespecialuser)){
        	sb.append("and isespecialuser=").append(isespecialuser);
        }
        List<Userbasicsinfo> list = dao.findBySql(sb.toString(),Userbasicsinfo.class);
        return list;
    }
    
    /**
     * 修改昵称
     * 
     * @param user
     *            用户基本信息
     * @param nickName
     *            昵称
     */
    public void updateNickName(Userbasicsinfo user, String nickName) {
        user.setNickname(nickName);
        dao.update(user);
    }
    
    /**
     * 修改登录密码
     * 
     * @param user
     *            用户基本信息
     * @param pwd
     *            登录密码
     */
    public void updatePwd(Userbasicsinfo user, String pwd) {
        user.setPassword(pwd);
        dao.update(user);
    }
    
    /**
     * 修改身份证
     * 
     * @param user
     *            会员基本信息
     * @param cardId
     *            图片地址
     */
    public void updateCardImg(Userbasicsinfo user, String cardImg) {
        user.getUserrelationinfo().setCardImg(cardImg);
        dao.update(user);
    }
    
    /**
     * 用户注册,保存
     * 
     * @param user 用户
     */
    public void save(Userbasicsinfo user) {
        dao.save(user);
    }

    public boolean queryUserByCardId(String cardId) {
    	return Integer.valueOf(dao.findObjectBySql("select count(1) from userrelationinfo where cardId=?", cardId).toString()) > 0 ?  true :  false;
    }
    
	public Userbasicsinfo queryUserByStaffNo(String member) {
		String sql="SELECT * FROM userbasicsinfo WHERE staff_no=?";
		
		return dao.findObjectBySql(sql, Userbasicsinfo.class, member);
	}
	public String getcurrentRegUsers(String key) {
		String sql="SELECT count(id) FROM userbasicsinfo";
		String value=String.valueOf(dao.findObjectBySql(sql,null));
		RedisHelper.set(key, value);
		return value;
	}

	/**
	 * 获取在职员工
	 * @return
	 */
	public List<Userbasicsinfo> getStaff() {
		String sql="SELECT * FROM userbasicsinfo WHERE user_type=2 AND department!=15";
		return dao.findBySql(sql,Userbasicsinfo.class);
	}
	/**
	 * 获取会员某一时间段的不同回购期的投资总额
	 * @param uid
	 * @param remonth
	 * @param start
	 * @param end
	 * @return
	 */
	public Double getUserTender(long uid,int remonth,String start,String end){
		String sql="SELECT SUM(lr.tenderMoney) FROM loanrecord lr JOIN loansign l ON lr.loanSign_id=l.id "
				+ "WHERE lr.isSucceed=1 AND lr.userbasicinfo_id=? "
				+ "AND DATE(lr.tenderTime)>=DATE(?) AND DATE(lr.tenderTime)<=DATE(?) AND l.remonth=?";
		Object obj=dao.findObjectBySql(sql,uid,start,end,remonth);
		Double result=0.0;
		if(null!=obj){
			result=Double.parseDouble(obj.toString());
		}
		return result;
	}
	
	public List<Object> getPromotedUserId(long uid){
		String sql="SELECT g.uid FROM generalize g WHERE g.genuid=?";
		return dao.findBySql(sql, uid);
	}
	public Double calHcCoin(double money,int month){
		double coin=money*0.01*month;
		return coin;
	}


	public Integer getPromotedUserCoins(Long id,String start,String end) {
		String sql="SELECT COUNT(1) FROM generalize g JOIN userbasicsinfo u ON g.uid = u.id WHERE genuid=? AND DATE(u.createTime)>=DATE(?) AND DATE(u.createTime)<=DATE(?) and isAuthIps=1";
		Object obj=dao.findObjectBySql(sql,id,start,end);
		int coin=0;
		if(null != obj)		coin=Integer.valueOf(obj.toString())*5;
		
		return coin;
	}
	
	/***
	 * 根据宝付订单号进行查询
	 * @param pMerBillNo
	 * @return
	 */
	public  Userbasicsinfo getUser(String pMerBillNo){
		String sql="select * from userbasicsinfo where pMerBillNo=?";
		List<Userbasicsinfo> userList=dao.findBySql(sql, Userbasicsinfo.class, pMerBillNo);
		return userList.size()>0?userList.get(0):null;
	}
	
	/** 根据手机号码查询用户基本信息 */
	public List<Userbasicsinfo> queryUserbasicsinfoByPhone(String phone) {
		String sql = "select u.* from userbasicsinfo u, userrelationinfo r where (u.userName=? or r.phone=?) and u.id=r.user_id";
		List<Userbasicsinfo> userList = dao.findBySql(sql, Userbasicsinfo.class, phone, phone);
		return userList;
	}
}
