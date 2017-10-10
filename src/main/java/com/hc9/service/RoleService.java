package com.hc9.service;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.hc9.dao.entity.Role;
import com.hc9.dao.impl.HibernateSupport;

/**
 * <p>
 * Title:RoleService</p>
 * <p>
 * Description: </p>
 * <p>
 * Company: 前海红筹</p>

 */
@Service
public class RoleService {

    /** 注入log4j打印类 */
    private static final Logger LOGGER = Logger.getLogger(RoleService.class);

    /** 注入数据库操作层 */
    @Resource
    private HibernateSupport dao;

    /** 注入菜单角色服务层 */
    @Resource
    private MenuroleService menuRoleService;

    /**
     * <p>
     * Title: queryAll</p>
     * <p>
     * Description: 查询所有角色</p>
     * 
     * @return 返回查询结果集list
     */
    public List<Role> queryAll() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("queryAll() - start");
        }

        String hql = "from Role";

        @SuppressWarnings("unchecked")
        List<Role> rolelist = dao.find(hql);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("queryAll() - end");
        }
        return rolelist;
    }

    /**
    * <p>Title: updateOrSave</p>
    * <p>Description: 修改或新增角色</p>
    * @param roleinfo 要修改对象
    */
    public void updateOrSave(Role roleinfo) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("updateOrSave(Role) - start");
        }

        dao.saveOrUpdate(roleinfo);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("updateOrSave(Role) - end");
        }
    }

    /**
    * <p>Title: findByObj</p>
    * <p>Description: 根据对象查询</p>
    * @param role 查询条件
    * @return 返回查询结果
    */
    public Role findByObj(Role role) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("findByObj(Role) - start");
        }

        @SuppressWarnings("unchecked")
        List<Role> rolelist = dao.findByExample(role);

        if (null == rolelist || rolelist.isEmpty()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("findByObj(Role) - end");
            }
            return null;
        } else {
            Role returnRole = rolelist.get(0);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("findByObj(Role) - end");
            }
            return returnRole;
        }
    }

    /**
    * <p>Title: findById</p>
    * <p>Description: 根据角色编号查询 </p>
    * @param ids 角色编号
    * @return 返回查询结果
    */
    public Role findById(String ids) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("findById(String) - start");
        }

        Role returnRole = dao.get(Role.class, Long.parseLong(ids));
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("findById(String) - end");
        }
        return returnRole;
    }

    /**
    * <p>Title: delete</p>
    * <p>Description: 删除角色</p>
    * @param role 要删除的角色
    * @return 删除结果 boolean
    */
    public boolean delete(Role role) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("delete(Role) - start");
        }

        boolean flag = false;

        if (menuRoleService.deleteByRole(role)) {
            dao.delete(role);
            flag = true;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("delete(Role) - end");
        }
        return flag;
    }

}
