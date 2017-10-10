package com.hc9.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.common.util.StringUtil;
import com.hc9.service.MenuroleService;
import com.hc9.service.RoleService;

/**
* <p>Title:MenuRoleController</p>
* <p>Description: 角色菜单关联控制层</p>
* <p>Company: 前海红筹</p> 
* @author LiNing
* date 2014年1月24日
*/
@Controller
@RequestMapping("/menuRole")
@CheckLogin(value=CheckLogin.ADMIN)
public class MenuRoleController {

    /**
     * 注入角色服务层
     */
    @Resource
    private RoleService roleservice;
    
    /**
     * 注入菜单角色关联服务层
     */
    @Resource
    private MenuroleService menuroleservice;

    /**
     * 更改角色权限
     * 
     * @param roleid
     *            角色编号
     * @param menuids
     *            拥有菜单的编号
     * @return 修改结果，json对象
     */
    @ResponseBody
    @RequestMapping("/upDateByRole")
    public JSONObject updateRoleMenu(String roleid, String menuids) {
        JSONObject json = new JSONObject();
        
        //返回http状态码
        String status="200";
        //提示信息
        String message="权限修改成功";
        
        //正确的角色编号
        long rightroleid = 0;

        if (StringUtil.isNotBlank(roleid) && StringUtil.isNumberString(roleid)
                && menuids != null) {

            rightroleid = Long.parseLong(roleid);

            StringBuffer rightmenuid = new StringBuffer();
            String[] menuid = menuids.split(",");
            // 判断传过来的菜单编号是否存在非数字
            for (String menu : menuid) {

                if (StringUtil.isNotBlank(menu)  && StringUtil.isNumberString(menu)) {
                    rightmenuid.append(menu + ",");
                }
            }

            // 修改角色菜单
            if (rightroleid > 0) {
                menuroleservice.upDateByRole(rightroleid,  rightmenuid.toString());
            }else{
                status="300";
                message="参数有误，修改失败";
            }

        } else {
            status="300";
            message="参数有误，修改失败";
        }
        json.element("statusCode", status);
        json.element("message", message);
        return json;
    }

    
    /**
     * 页面跳转方法
     * @param url 要跳转的页面
     * @param id 附带参数
     * @param request HttpServletRequest
     * @return 传入的页面
     */
    @RequestMapping("/jume")
    public ModelAndView jumePage(String url, String id,  HttpServletRequest request) {
    	
        request.setAttribute("id", id);
        
        if(url.indexOf("role_menu")!=-1){
            request.setAttribute("rolelist", roleservice.queryAll());
        }
        
        return new ModelAndView("WEB-INF/views/admin/" + url);
    }
}
