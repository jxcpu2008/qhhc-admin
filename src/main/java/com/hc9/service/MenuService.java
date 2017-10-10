package com.hc9.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.Adminuser;
import com.hc9.dao.entity.Menu;
import com.hc9.dao.entity.Menurole;
import com.hc9.dao.impl.HibernateSupport;

/**
 * <p>
 * Title:MenuService</p>
 * <p>
 * Description: 菜单服务层</p>
 * <p>
 * Company: 前海红筹</p>
 * 
 */
@Service
public class MenuService {
	
	private static final Logger logger = Logger.getLogger(MenuService.class);

    /** 注入数据库服务层*/
    @Resource
    private HibernateSupport dao;

    /**
    * <p>Title: queryByUser</p>
    * <p>Description: 查询当前登录用户的菜单权限</p>
    * @param loginuser 当前登录的用户
    * @return 返回查询结果集list
    */
    @SuppressWarnings("unchecked")
    public List<Menu> queryByUser(Adminuser loginuser) {
        List<Menu> allmenulist = new ArrayList<Menu>();

        String rolemenu = "from Menurole where role.id= ?";

        List<Menurole> objlist = dao
                .find(rolemenu, loginuser.getRole().getId());

        StringBuffer ids = new StringBuffer("");

        for (int i = 0; i < objlist.size(); i++) {
            ids.append(objlist.get(i).getMenu().getId() + ",");
        }
        
        if(ids.length()>2){
            String menuhql = "from Menu where id in ("
                    + ids.toString().substring(0, ids.length() - 1) + ")";
            allmenulist = dao.find(menuhql);
        }

        return allmenulist;
    } 

    /**
    * <p>Title: queryByRole</p>
    * <p>Description: 根据角色查询菜单</p>
    * @param roleid 角色编号
    * @param request HttpServletRequest
    * @return 查询结果集list
    */
    public List<Menu> queryByRole(long roleid, HttpServletRequest request) {

        String rolemenu = "from Menurole where role.id= "+roleid;

        @SuppressWarnings("unchecked")
        List<Menurole> objlist = dao.find(rolemenu);

        StringBuffer ids = new StringBuffer("");

        for (Menurole menu:objlist) {
            ids.append(menu.getMenu().getId() + ",");
        }

        logger.debug("rolemenustr = " + ids.toString());
        
        request.setAttribute("rolemenustr", ids.toString());

        if (StringUtil.isNotBlank(ids.toString())) {
            String menuhql = "from Menu where id in ("
                    + ids.toString().substring(0, ids.length() - 1) + ")";

            return queryByHql(menuhql);
        } else {
            return new ArrayList<Menu>();
        }

    }

    /**
    * <p>Title: queryAll</p>
    * <p>Description: 查询所有菜单</p>
    * @return 返回查询结果集 list
    */
    public List<Menu> queryAll() {

        String hql = "from Menu";

        return queryByHql(hql);
    }

    /**
    * <p>Title: queryByLevel</p>
    * <p>Description:根据菜单等级查询（如：1级菜单，2级菜单） </p>
    * @param level 传入菜单等级（eg：1,2...）
    * @return 返回查询结果集
    */
    public List<Menu> queryByLevel(String level) {

        String hql = "from Menu where mlevel=" + level;

        return queryByHql(hql);
    }

    /**
    * <p>Title: queryByFather</p>
    * <p>Description: 根据父级菜单查询子菜单</p>
    * @param fatherid 父级菜单编号
    * @return 返回查询结果集 list
    */
    public List<Menu> queryByFather(String fatherid) {

        String hql = "from Menu where menu.id in(" + fatherid + ")";

        return queryByHql(hql);

    }

    /**
    * <p>Title: queryById</p>
    * <p>Description: 根据编号查询菜单</p>
    * @param menuid  菜单编号
    * @return 返回查询结果集list
    */
    public List<Menu> queryById(String menuid) {
        String hql = "from Menu where id in (" + menuid + ")";

        return queryByHql(hql);
    }

    /**
    * <p>Title: queryByHql</p>
    * <p>Description: 根据传入hql查询</p>
    * @param hql 要执行的hql
    * @return 查询结果集list
    */
    public List<Menu> queryByHql(String hql) {

        @SuppressWarnings("unchecked")
        List<Menu> menulist = dao.find(hql);

        return menulist;
    }
}
