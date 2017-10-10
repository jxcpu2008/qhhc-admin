package com.hc9.service;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.common.constant.Constant;
import com.hc9.dao.entity.Banktype;
import com.hc9.dao.entity.UserBank;
import com.hc9.dao.entity.Validcodeinfo;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;

/**
 * 用户银行卡账号业务处理
 * 
 * @author RanQiBing 2014-02-12
 *
 */
@Service
public class UserBankService {

	@Resource
	private HibernateSupport dao;

	/**
	 * 根据用户编号查询银行卡信息
	 * 
	 * @param userId
	 *            用户编号
	 * @return
	 */
	public UserBank getUserBank(Long userId) {
		String hql = "from UserBank b where b.userbasicsinfo.id=?";
		List<UserBank> list = dao.find(hql, userId);
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	/***
	 * 根据银行类型
	 * 
	 * @param validateCode
	 * @return
	 */
	public List<Banktype> getBankType() {
		String hql = "from Banktype";
		List<Banktype> list = dao.find(hql.toString());
		return list;
	}

	/**
	 * 根据id查询
	 * 
	 * @param id
	 * @return
	 */
	public UserBank getUserBankById(Long id) {
		String hql = "from UserBank b where b.id=?";
		List<UserBank> list = dao.find(hql, id);
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 添加用户银行卡信息
	 * 
	 * @param userbank
	 *            银行卡信息
	 */
	public void save(UserBank userbank) {
		dao.save(userbank);
	}

	/***
	 * 修改银行卡
	 * 
	 * @param userbank
	 */
	public void update(UserBank userbank) {
		dao.update(userbank);
	}

	/***
	 * 保存银行卡
	 * 
	 * @param userbank
	 * @return
	 */
	public String saveUserBankSeria(UserBank userbank) {
		Serializable seria = dao.save(userbank);
		return seria.toString();
	}

	/**
	 * 根据当前登录用户查询用户发送的验证码
	 * 
	 * @param id
	 *            用户编号
	 * @return 用户所发送的验证码信息
	 */
	public Validcodeinfo codeUserId(Long id) {
		String hql = "from Validcodeinfo v where v.userbasicsinfo.id=?";
		List<Validcodeinfo> list = dao.find(hql, id);
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	
	
	/***
	 * 查询是否存在银行卡
	 * @param bankNo
	 * @return
	 */
	public boolean getUserBank(String bankNo){
		String sql="select * from userbank where state=1 and bank_no like '"+bankNo.trim()+"%'";
		List<UserBank> userBankList = dao.findBySql(sql, UserBank.class);
		return userBankList.size()>0?false:true;
	}
	
	/***
	 * 根据用户Id查询
	 * @param userId
	 * @return
	 */
	public boolean getUserBankId(Long userId){
		String sql="select * from userbank where user_id=?";
		List<UserBank> userBankList = dao.findBySql(sql, UserBank.class,userId);
		return userBankList.size()>0?true:false;
	}

	/***
	 * 删除
	 * 
	 * @param userbank
	 */
	public void delete(UserBank userbank) {
		dao.delete(userbank);
	}

	public PageModel getUserBankList(PageModel page, String userId) {

		StringBuffer sql = new StringBuffer(
				"SELECT * from userbank where user_id=" + userId
						+ " and state=1");

		String sqlcount = "select count(*) from userbank where user_id="
				+ userId + " and state=1";
		page.setTotalCount(dao.queryNumberSql(sqlcount.toString()).intValue());
		sql.append("  LIMIT ")
				.append((page.getPageNum() - Constant.STATUES_ONE)
						* page.getNumPerPage()).append(",")
				.append(page.getNumPerPage());
		List<UserBank> list = dao.findBySql(sql.toString());
		page.setList(list);
		return page;
	}
}
