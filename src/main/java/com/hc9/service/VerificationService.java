package com.hc9.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.dao.entity.Securityproblem;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.entity.Verifyproblem;
import com.hc9.dao.impl.HibernateSupport;

/**
 * 安全中心验证
 * 
 * @author frank
 * 
 */
@Service
public class VerificationService {

	/**
	 * 数据库接口
	 */
	@Resource
	private HibernateSupport commonDao;

	@Resource
	private BorrowService borrowService;

	/**
	 * 检查会员的各种验证状态
	 * 
	 * @param id
	 *            用户编号
	 * @return 2、实名认证, 3、手机验证 ,4、宝付帐户
	 */
	public Integer basicInfoVerification(Userbasicsinfo user) {

		// 判断手机是否通过验证
		if (user.getUserrelationinfo().getPhonePass() == 0) {
			return 2;
		}

		if (user.getCardStatus() != 2) {
			return 3; // 实名认证
		}

		// 判断宝付帐号
		String pMerBillno = user.getpMerBillNo();
		if (pMerBillno == null || pMerBillno.equals("")) {
			return 4;
		}

		return 100;
	}

	/**
	 * 5、项目众持融资人申请,
	 * 
	 * @param user
	 * @return
	 */
	public Integer loaneeInfoVerification(Userbasicsinfo user) {
		String pMerBillno = user.getpMerBillNo();
		if (pMerBillno == null || pMerBillno.equals("")) {
			return 4;
		}
		if (user.getIscrowdhold() == null || user.getIscrowdhold() != 1) {
			return 5;
		}
		// 是否具备项目发布资格

		return 100;
	}

	/**
	 * 6、店铺众筹融资人审核
	 * 
	 * @param user
	 * @return
	 */
	public Integer shopVerification(Userbasicsinfo user) {
		String pMerBillno = user.getpMerBillNo();
		if (pMerBillno == null || pMerBillno.equals("")) {
			return 4;
		}
		if (user.getIscrowdfundingperson() == null
				|| user.getIscrowdfundingperson() != 1) {
			return 6;
		}
		return 100;
	}

	/**
	 * 邮箱通过验证
	 * 
	 * @param user
	 *            用户基本信息
	 */
	public void emailSafe(Userbasicsinfo user) {
		user.getUserrelationinfo().setEmailisPass(1);
		commonDao.update(user);
	}

	/**
	 * 实名验证
	 * 
	 * @param user
	 *            用户基本信息
	 * @param cardId
	 *            身份证号码
	 * @param name
	 *            真实姓名
	 * @param nickName
	 *            昵称
	 */
	public void realNameSafe(Userbasicsinfo user, String cardId, String name,
			String nickName) {
		user.getUserrelationinfo().setCardId(cardId);
		user.setName(name);
		user.setNickname(nickName);
		commonDao.update(user);
	}

	/**
	 * 安全问题验证
	 * 
	 * @param user
	 *            用户信息
	 * @param pwd
	 *            交易密码
	 * @param id1
	 *            安全问题编号1
	 * @param id2
	 *            安全问题编号2
	 * @param answer1
	 *            安全问题答案1
	 * @param answer2
	 *            安全问题答案2
	 */
	public void addOrUpSecuritySafe(Userbasicsinfo user, String id1,
			String id2, String answer1, String answer2) {
		List<Securityproblem> spList = querySecuProByUser(user);
		if (spList == null || spList.size() <= 0) {
			// 添加安全问题1
			Securityproblem problem = new Securityproblem();
			problem.setAnswer(answer1);
			problem.setUserbasicsinfo(user);
			problem.setVerifyproblem(new Verifyproblem(Long.valueOf(id1)));
			commonDao.save(problem);

			// 添加安全问题2
			problem = new Securityproblem();
			problem.setAnswer(answer2);
			problem.setUserbasicsinfo(user);
			problem.setVerifyproblem(new Verifyproblem(Long.valueOf(id2)));
			commonDao.save(problem);
		} else {
			// 修改安全问题1
			Securityproblem problem1 = spList.get(0);
			problem1.setAnswer(answer1);
			problem1.setUserbasicsinfo(user);
			problem1.setVerifyproblem(new Verifyproblem(Long.valueOf(id1)));
			commonDao.update(problem1);

			// 修改安全问题2
			Securityproblem problem2 = spList.get(1);
			problem2.setAnswer(answer2);
			problem2.setUserbasicsinfo(user);
			problem2.setVerifyproblem(new Verifyproblem(Long.valueOf(id2)));
			commonDao.update(problem2);
		}
	}

	public List<Securityproblem> querySecuProByUser(Userbasicsinfo user) {
		String sql = "select * from securityproblem where user_id = ? order by id";
		return commonDao.findBySql(sql, Securityproblem.class, user.getId());
	}
}
