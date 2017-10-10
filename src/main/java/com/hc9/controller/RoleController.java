package com.hc9.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.common.constant.Constant;
import com.hc9.common.util.DateUtils;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.Adminuser;
import com.hc9.dao.entity.Role;
import com.hc9.service.AdminService;
import com.hc9.service.RoleService;

/**
* <p>Title:RoleController</p>
* <p>Description: 角色控制层</p>
* <p>Company: 前海红筹</p> 
* @author LiNing
* date 2014年1月24日
*/
@Controller
@RequestMapping("/role")
@CheckLogin(value=CheckLogin.ADMIN)
public class RoleController {
    /**
     * 引入log4j日志打印类
     */
    private static final Logger LOGGER = Logger.getLogger(RoleController.class);

    /**
     * 注入角色服务层
     */
    @Resource
    private RoleService roleservice;

    /**
     * 注入用户服务层
     */
    @Resource
    private AdminService adminservice;

    /**
     * 查询所有角色
     * @return 查询结果转换成的json对象
     */
    @ResponseBody
    @RequestMapping("/queryall")
    public JSONArray queryall() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("queryall()方法开始"); 
        }

        // 定义返回对象
        JSONArray jsonlist = new JSONArray();
        // 定义单个查询结果对象
        JSONObject json = null;
        // 查询所有角色
        List<Role> rolelist = roleservice.queryAll();
        // 遍历结果集
        for (Role roleinfo : rolelist) {
            json = new JSONObject();

            json.element("id", roleinfo.getId());
            json.element("classes", "file");// 显示文件图标
            json.element("text", roleinfo.getRoleName());// 要显示的文本
            json.element("expanded", true);// 是否展开节点 true:展开；false：收缩
            jsonlist.add(json);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("queryall()方法结束OUTPARAM=" + jsonlist); //$NON-NLS-1$
        }
        return jsonlist;
    }

    /**
     * 页面跳转方法，传入页面名称
     * 
     * @param pageName
     *            要跳转页面的名字
     * @return 要跳转的页面
     */
    @RequestMapping("/transit")
    public ModelAndView transitMethd(HttpServletRequest request,String pageName) {
    	
        ModelAndView returnModelAndView = new ModelAndView("WEB-INF/views/admin/" + pageName);
        if(pageName!=null||pageName.equals("")){
        	request.setAttribute("role", new Role());
        }
        return returnModelAndView;

    }

    /**
     * 编辑角色信息
     * 
     * @param roleinfo
     *            要编辑的角色
     * @param request
     *            HttpServletRequest
     * @return 返回修改结果
     */
    @ResponseBody
    @RequestMapping("/edit")
    public JSONObject eidtrole(Role roleinfo, HttpServletRequest request) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("eidtrole(Role roleinfo=" + roleinfo + ", HttpServletRequest request=" + request + ")方法开始"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
        // 定义返回结果
        JSONObject json = new JSONObject();

        // http状态码
        String statusCode = "200";
        String message = "信息修改成功";
        if(roleinfo.getRoleName()==null||roleinfo.getRoleName().replaceAll(" ", "").equals("")){
        	statusCode="301";
        	message = "内容不能为空";
//            json.element("statusCode", statusCode);
            json.element("message", message);
//            json.element("navTabId", "main26");
//            json.element("callbackType", "closeCurrent");
            return json;
        }
        // 从session中获取当前登录人信息
        Adminuser loginadmin = (Adminuser) request.getSession().getAttribute(
                Constant.ADMINLOGIN_SUCCESS);

        // 如果已经登录
        if (loginadmin == null) {
            statusCode = "301";
            message = "登录已失效，请重新登录";

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("eidtrole(Role, HttpServletRequest)方法结束OUTPARAM=" + json); //$NON-NLS-1$
            }
            return json;
        }
        if (roleinfo.getId() != null) {
            message = "信息修改成功";
            roleinfo.setCreateTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
            roleinfo.setCreateUser(loginadmin.getId() + "");
        } else {
            message = "角色添加成功";
            roleinfo.setCreateTime(DateUtils.format("yyyy-MM-dd"));
            roleinfo.setCreateUser(loginadmin.getId().toString());
        }

        // 修改数据库数据
        roleservice.updateOrSave(roleinfo);

        json.element("statusCode", statusCode);
        json.element("message", message);
        json.element("navTabId", "main26");
        json.element("callbackType", "closeCurrent");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("eidtrole(Role, HttpServletRequest)方法结束OUTPARAM=" + json); //$NON-NLS-1$
        }
        return json;
    }

    /**
     * 检查角色名的唯一性
     * 
     * @param role
     *            修改的时候当前修改角色编号
     * @param ids
     *            角色名
     * @return 返回boolean
     */
    @ResponseBody
    @RequestMapping("/checkonly")
    public String checkOnly(Role role, String ids) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("checkOnly(Role role=" + role + ", String ids=" + ids + ")方法开始"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }

        boolean flag = false;

        try {
            role.setRoleName(new String(role.getRoleName().getBytes(
                    "ISO-8859-1"), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(
                    "checkOnly(Role role=" + role + ", String ids=" + ids + ")", e); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        }
        // 将传入数据作为查询条件
        role = roleservice.findByObj(role);

        if (null == role) {
            flag = true;
        } else if (ids != null && role != null
                && ids.equals(role.getId().toString())) {
            flag = true;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("checkOnly(Role, String)方法结束OUTPARAM=" + flag); //$NON-NLS-1$
        }
        return flag + "";
    }

    /**
     * 删除角色信息
     * 
     * @param ids
     *            要删除角色编号
     * @return 删除结果
     */
    @ResponseBody
    @RequestMapping("/delete")
    public JSONObject deleteRole(String ids) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("deleteRole(String ids=" + ids + ")方法开始"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // 定义返回对象
        JSONObject json = new JSONObject();
        String statusCode = "200";
        String message = "角色删除成功";
        Role roleinfo = null;

        // 判断传入的是否是纯数字编号
        if (StringUtil.isNotBlank(ids) && StringUtil.isNumberString(ids)) {
            roleinfo = new Role();
            roleinfo.setId(Long.parseLong(ids));
            // 根据编号查询
            List<Adminuser> adminlist = adminservice.queryByRole(roleinfo);

            if (null == adminlist || adminlist.isEmpty()) {
                if (roleservice.delete(roleinfo)) {
                    statusCode = "200";
                    message = "角色删除成功";
                } else {
                    statusCode = "300";
                    message = "角色删除失败，请稍后重试";
                }
            } else {
                statusCode = "300";
                message = "该角色下还有用户，不能删除";
            }
        } else {
            json.element("statusCode", "300");
            json.element("message", "参数错误，删除失败");
        }

        json.element("navTabId", "main26");
        json.element("statusCode", statusCode);
        json.element("message", message);
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("deleteRole(String)方法结束OUTPARAM=" + json); //$NON-NLS-1$
        }
        return json;
    }

    /**
     * 根据编号查询单个角色
     * @param ids 要查询角色编号
     * @param request HttpServletRequest
     * @return 修改页面
     */
    @RequestMapping("/querybyid")
    public ModelAndView queryById(String ids, HttpServletRequest request) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("queryById(String ids=" + ids + ", HttpServletRequest request=" + request + ")方法开始"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }

        Role role = null;
        
        //判断传入编号是否是纯数字
        if (StringUtil.isNotBlank(ids) && StringUtil.isNumberString(ids)) {
               
            //根据编号查询
            role = roleservice.findById(ids);
        }
        
        if (null != role && null != role.getRoleName()) {
            request.setAttribute("updateroleinfo", role);
        }

        ModelAndView returnModelAndView = new ModelAndView(
                "WEB-INF/views/admin/edit_role");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("queryById(String, HttpServletRequest)方法结束OUTPARAM=" + returnModelAndView); //$NON-NLS-1$
        }
        return returnModelAndView;
    }

    /**
     * 下拉列表框需要的查询
     * @return 查询结果转换后的json对象
     */
    @ResponseBody
    @RequestMapping("/querycom")
    public JSONArray queryCmobox() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("queryCmobox()方法开始"); //$NON-NLS-1$
        }
        
        //定义返回对象
        JSONArray jsonlist = new JSONArray();
        
        //定义查询结果集中的单个对象
        JSONObject json = null;
        
        //查询所有角色
        List<Role> rolelist = roleservice.queryAll();
        
        //转换json对象
        for (Role role : rolelist) {
            json = new JSONObject();

            json.element("text", role.getRoleName());
            json.element("value", role.getId());
            jsonlist.add(json);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("queryCmobox()方法结束OUTPARAM=" + jsonlist); //$NON-NLS-1$
        }
        return jsonlist;
    }
}
