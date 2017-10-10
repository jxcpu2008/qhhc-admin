package com.hc9.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.hc9.common.constant.Constant;
import com.hc9.common.util.GoogleAuthenticator;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.Adminuser;
import com.hc9.dao.entity.Menu;
import com.hc9.dao.entity.Role;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;

/**
 * <p>
 * Title:AdminService
 * </p>
 * <p>
 * Description: 后台管理员控制层
 * </p>
 * <p>
 * Company: 前海红筹
 * </p>
 * 
 */
@Service
public class AdminService {

	/** 注入数据库操作层 */
	@Resource
	private HibernateSupport dao;

	/** 注入登录日志服务层 */
	@Resource
	private AdminLoginLogService logservice;

	/** 注入后台菜单服务层 */
	@Resource
	private MenuService menuservice;

	/**
	 * <p>
	 * Title: getMember
	 * </p>
	 * <p>
	 * Description: 查询所有的客服人员
	 * </p>
	 * 
	 * @return 返回查询结果集list
	 */
	public List getMember() {
		StringBuffer sb = new StringBuffer(
				"SELECT au.id,au.realname from adminuser au INNER JOIN role r on au.role_id=r.id where r.roleName='客服人员'");
		return dao.findBySql(sb.toString(), null);
	}

	/**
	 * <p>
	 * Title: login
	 * </p>
	 * <p>
	 * Description: 查询当前登录人拥有的菜单
	 * </p>
	 * 
	 * @param adminuser
	 *            当前登录人
	 * @param request
	 *            HttpServletRequest
	 * @return 返回查询结果集list
	 */
	public List<Menu> login(Adminuser adminuser, HttpServletRequest request) {

		List<Menu> onemenulist = new ArrayList<Menu>();
		List<Menu> sendmenulist = new ArrayList<Menu>();
		List<Menu> allmenulist = new ArrayList<Menu>();

		// 根据输入的用户名和密码查询用户
		@SuppressWarnings("unchecked")
		List<Adminuser> adminlist = dao.findByExample(adminuser);

		// 如果结果不为空则
		if (null != adminlist && !adminlist.isEmpty()) {

			// 查询当前登录人所能有的菜单
			allmenulist = menuservice.queryByUser(adminlist.get(0));

			StringBuffer parentid = new StringBuffer();

			for (int i = 0; i < allmenulist.size(); i++) {
				if (1 == allmenulist.get(i).getMlevel()) {
					onemenulist.add(allmenulist.get(i));
				} else if (2 == allmenulist.get(i).getMlevel()) {
					sendmenulist.add(allmenulist.get(i));

					parentid.append(allmenulist.get(i).getMenu().getId() + ",");
				}
			}

			// 如果一级菜单为空，或数量不够，则重新查询
			if (StringUtil.isNotBlank(parentid.toString())
					&& onemenulist.isEmpty()
					|| parentid.length() / 2 > onemenulist.size()) {
				onemenulist = menuservice.queryById(parentid.toString()
						.substring(0, parentid.length() - 1));
			}
			// 记录登录日志
			logservice.addlog_TRAN(adminlist.get(0), request);

			// 保存当前登录人菜单
			request.getSession().setAttribute("onemenulist", onemenulist);
			request.getSession().setAttribute("sendmenulist", sendmenulist);
			request.getSession().setAttribute(Constant.ADMINLOGIN_SUCCESS,
					adminlist.get(0));
		}

		return onemenulist;
	}

	/**
	 * <p>
	 * Title: queryByRole
	 * </p>
	 * <p>
	 * Description: 根据角色查询用户
	 * </p>
	 * 
	 * @param roleinfo
	 *            角色编号
	 * @return 返回查询结果集list
	 */
	@SuppressWarnings("unchecked")
	public List<Adminuser> queryByRole(Role roleinfo) {

		if (StringUtil.isNotBlank(roleinfo.getId() + "")) {
			String hql = "from Adminuser where role.id=" + roleinfo.getId();

			return dao.find(hql);
		}

		return null;
	}

	/**
	 * <p>
	 * Title: queryPage
	 * </p>
	 * <p>
	 * Description: 分页查询用户信息
	 * </p>
	 * 
	 * @param page
	 *            分页参数
	 * @param user
	 *            查询条件
	 * @return 返回查询结果集
	 */
	@SuppressWarnings("unchecked")
	public List<Object> queryPage(PageModel page, String userName,
			Adminuser user) {

		List<Object> adminlist = new ArrayList<Object>();

		String sqlcount = "select count(1) FROM adminuser ,role WHERE adminuser.role_id=role.id";
		String sql = "SELECT adminuser.id,adminuser.username,adminuser.realname,adminuser.phone,adminuser.sex,adminuser.email,adminuser.status,adminuser.secret_key,role.roleName "
				+ " FROM adminuser ,role WHERE adminuser.role_id=role.id ";

		if (StringUtil.isNotBlank(userName)) {
			sql += "  and adminuser.username LIKE '%" + userName + "%'";
			sqlcount += "  and adminuser.username LIKE '%" + userName + "%'";
		}
		adminlist = (List<Object>) dao.pageListBySql(page, sqlcount, sql, null);

		return adminlist;

	}

	/**
	 * <p>
	 * Title: saveOrUpdate
	 * </p>
	 * <p>
	 * Description: 保存或修改用户信息
	 * </p>
	 * 
	 * @param adminuser
	 *            要保存或修改的用户
	 */
	public void saveOrUpdate(Adminuser adminuser) {

		dao.saveOrUpdate(adminuser);
	}

	/**
	 * <p>
	 * Title: delete
	 * </p>
	 * <p>
	 * Description: 删除用户
	 * </p>
	 * 
	 * @param adminuser
	 *            要删除的用户
	 */
	public void delete(Adminuser adminuser) {

		dao.delete(adminuser);

	}

	/**
	 * <p>
	 * Title: deletes
	 * </p>
	 * <p>
	 * Description: 批量删除用户
	 * </p>
	 * 
	 * @param ids
	 *            要删除用户的编号
	 */
	public void deletes(String ids) {

		if (StringUtil.isNotBlank(ids)) {
			// 根据“，”拆分字符串
			String[] newids = ids.split(",");

			// 确认删除的编号
			String delstr = "";

			for (String idstr : newids) {

				// 将不是空格和非数字的字符拼接
				if (StringUtil.isNotBlank(idstr)
						&& StringUtil.isNumberString(idstr)) {
					delstr += idstr + ",";
				}
			}

			// 如果确认删除的字符串不为空
			if (delstr.length() > 0) {

				// 批量删除
				String hql = "from Adminuser where id in ("
						+ delstr.substring(0, delstr.length() - 1) + ")";
				dao.deleteAll(dao.find(hql));

			}

		}

	}

	/**
	 * <p>
	 * Title: queryById
	 * </p>
	 * <p>
	 * Description: 根据编号查询用户
	 * </p>
	 * 
	 * @param adminuser
	 *            要查询用户的编号（对象）
	 * @return 查询结果，存在就返回Adminuser，不存在返回null
	 */
	public Adminuser queryById(Adminuser adminuser) {

		return dao.get(Adminuser.class, adminuser.getId());
	}

	public Adminuser queryByAdminId(Long id) {

		return dao.get(Adminuser.class, id);
	}
	/**
	 * 获取谷歌安全key
	 * @param username
	 * @return
	 */
	public String getSecretKey(String username,String host) {
		String key = GoogleAuthenticator.genSecretKey(username, host);
		return key;
	}
	
	/**
	 * 校验谷歌SecretKey
	 * @param codes
	 * @param secretKey
	 * @return
	 */
	public boolean checkSecretKey(String codes,String secretKey){
		return GoogleAuthenticator.authcode(codes, secretKey);
	}

}
