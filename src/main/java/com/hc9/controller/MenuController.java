package com.hc9.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.Menu;
import com.hc9.service.MenuService;
import com.hc9.service.MenuroleService;

/**
* <p>Title:MenuController</p>
* <p>Description:菜单查询控制器 </p>
* <p>Company: 前海红筹</p> 
* @author LiNing
* date 2014年1月24日
*/
@Controller
@RequestMapping("/menu")
@CheckLogin(value=CheckLogin.ADMIN)
public class MenuController {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(MenuController.class);

    /**
     * 导入菜单服务层
     */
    @Resource
    private MenuService menusercie;
    
    @Resource
    private MenuroleService menuRoleService;

    /**
     * 原始菜单查询（由于是上个项目遗留，后期可能作废）
     * @param model 数据对象
     * @return 返回后台主页面
     */
    @RequestMapping(value = { "to_main", "/" })
    @Deprecated
    public ModelAndView menuTree(Model model) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("menuTree(Model) - 菜单初始化了......");
        }
        return new ModelAndView("WEB-INF/views/admin/public/left");
    }

    /**
     * 点击角色查询菜单树
     * @param request HttpServletRequest
     * @return 角色菜单显示页
     * @param id 要查询菜单的角色主键
     */
    @RequestMapping("/queryall")
    public ModelAndView queryByRole(String id, HttpServletRequest request) {

        if (StringUtil.isNotBlank(id) && StringUtil.isNumberString(id)) {
            request.setAttribute("roleid", id);

            Map<Menu, List<Menu>> menumap = new LinkedHashMap<Menu, List<Menu>>();

            // 查询一级菜单
            List<Menu> onemenulist = menusercie.queryByLevel("1");

            int menusize = 0;

            if (null != onemenulist && !onemenulist.isEmpty()) {
                menusize = onemenulist.size();
            }

            // 查询一级菜单下面的二级的二级菜单
            for (int i = 0; i < menusize; i++) {

                menumap.put(onemenulist.get(i), menusercie
                        .queryByFather(onemenulist.get(i).getId() + ""));

            }

            // 功能点
            List<Menu> threemenulist = menusercie.queryByLevel("3");

            // 保存功能点
            request.setAttribute("threemenulist", threemenulist);

            // 保存一二级菜单
            request.setAttribute("menulist", menumap);

            return new ModelAndView("WEB-INF/views/admin/role_menu_tree");
        }

        return null;
    }

    /**
     * 根据角色编号查询所拥有的菜单并转换成json对象
     * @param id 要查询菜单的角色
     * @param request HttpServletRequest
     * @return 查询结果转换成json对象
     */
    @ResponseBody
    @RequestMapping("/newqueryall")
    public JSONArray newQueryAll(String id, HttpServletRequest request) {

        JSONArray jsonlist = new JSONArray();

        if (StringUtil.isNotBlank(id) && StringUtil.isNumberString(id)) {

            menusercie.queryByRole(Long.parseLong(id), request);

            // 调用将菜单转换成json的方法
            jsonlist = this.queryByLevelTwo(null, Long.parseLong(id));

            JSONObject json = new JSONObject();
            json.element("roleid", request.getAttribute("rolemenustr"));
            json.element("id", "r1");
            json.element("text", "不要");
            jsonlist.add(json);
            
            LOGGER.debug("newQueryAll jsonlist = " + jsonlist.toString());
            return jsonlist;
        } else {
            return jsonlist;
        }

    }
    
    /**
     * 将list集合转换成json对象
     * @param menulist 要转换的菜单集合
     * @return 转换后的结果
     */
    public JSONArray joiningTogether(List<Menu> menulist) {
        JSONArray jsonlist = new JSONArray();
        JSONObject json = null;

        int listsize = 0;

        if (null != menulist && !menulist.isEmpty()) {

            listsize = menulist.size();
        }
        for (int i = 0; i < listsize; i++) {
            json = new JSONObject();
            json.element("id", menulist.get(i).getId());// 菜单节点id
            if (null != menulist.get(i).getMenu()) {
                json.element("pid", menulist.get(i).getMenu().getId());// 父节点
                json.element("classes", "file");// 显示文件图标
            } else {
                json.element("classes", "folder");// 显示文件夹图标
            }
            json.element("text", menulist.get(i).getSmenCaption()); // 显示的文字
            json.element("expanded", true);// 是否展开节点 true:展开；false：收缩

            jsonlist.add(json);
        }

        return jsonlist;

    }

    /**
     * 将所有菜单转换成json
     * 
     * @param menu  菜单对象
     * @return json转换结果
     */
    public JSONArray queryByLevelTwo(Menu menu) {
        JSONArray jsonlist = new JSONArray();
        JSONObject json = null;
        List<Menu> menulist = null;

        if (menu == null) {
            menulist = menusercie.queryByLevel("1");
        } else {
            menulist = menusercie.queryByFather(menu.getId() + "");
        }

        for (Menu newmenu : menulist) {
            json = new JSONObject();
            json.element("id", newmenu.getId());// 菜单节点id

            if (null == menu || menu.getMlevel() != 3) {
                json.element("classes", "folder");// 显示文件夹图标
            } else {
                json.element("pid", menu.getId());// 父节点
                json.element("classes", "file");// 显示文件图标
            }
            json.element("text", newmenu.getSmenCaption()); // 显示的文字
            json.element("mevel", newmenu.getMlevel());
            json.element("expanded", true);// 是否展开节点 true:展开；false：收缩
            json.element("children", this.queryByLevelTwo(newmenu)); // 递归调用自身，查询子菜单
            jsonlist.add(json);
        }

        LOGGER.debug("queryByLevelTwo jsonlist = " + jsonlist.toString());
        
        return jsonlist;

    }
    
    public JSONArray queryByLevelTwo(Menu menu, Long roleId) {
        JSONArray jsonlist = new JSONArray();
        JSONObject json = null;
        List<Menu> menulist = null;

        if (menu == null) {
            menulist = menusercie.queryByLevel("1");
        } else {
            menulist = menusercie.queryByFather(menu.getId() + "");
        }

        for (Menu newmenu : menulist) {
        	
//        	Object object = menuRoleService.queryMenuRole(roleId, newmenu.getId());
//        	if (object != null) {
        	
	            json = new JSONObject();
	            json.element("id", newmenu.getId());// 菜单节点id
	
	            if (null == menu || menu.getMlevel() != 3) {
	                json.element("classes", "folder");// 显示文件夹图标
	            } else {
	                json.element("pid", menu.getId());// 父节点
	                json.element("classes", "file");// 显示文件图标
	            }
	            json.element("text", newmenu.getSmenCaption()); // 显示的文字
	            json.element("mevel", newmenu.getMlevel());
	            json.element("expanded", true);// 是否展开节点 true:展开；false：收缩
	            json.element("children", queryByLevelTwo(newmenu, roleId)); // 递归调用自身，查询子菜单
	            
	            jsonlist.add(json);
//        	}
        }

        LOGGER.debug("queryByLevelTwo jsonlist = " + jsonlist.toString());
        
        return jsonlist;
    }
}