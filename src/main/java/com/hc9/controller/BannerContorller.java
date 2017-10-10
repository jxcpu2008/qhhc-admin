package com.hc9.controller;

import java.io.PrintWriter;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.dao.entity.Banner;
import com.hc9.model.PageModel;
import com.hc9.service.AppCacheService;
import com.hc9.service.BannerService;

/**
 * banner控制层
 * 
 * 
 */
@Controller
@RequestMapping(value = "/banner")
@CheckLogin(value = CheckLogin.ADMIN)
public class BannerContorller {
	
	private static final Logger logger = Logger.getLogger(BannerContorller.class);

    @Resource
    private BannerService bannerservice;

    @Resource
    private ServletContext application;

    @Resource
    AppCacheService appCacheService;
    
    /**
     * banner图片分页展示信息
     * 
     * @param request
     *            请求对象
     * @param page
     *            分页对象
     * @return jsp路径
     */
    @RequestMapping(value ="/bannerpage")
    public ModelAndView bannerPage(
            @ModelAttribute(value = "PageModel") PageModel page,
            HttpServletRequest request) {
        // 取到banner图片信息条数
        Object count = bannerservice.getCount();
        page.setTotalCount(Integer.parseInt(count.toString()));
        // 分页查询banner图片信息
        List list = bannerservice.bannerPage(page);
        request.setAttribute("page", page);
        request.setAttribute("list", list);
        bannerservice.query();

        return new ModelAndView("/WEB-INF/views/admin/banner/bannerlist");
    }

    /**
     * 打开新增（编辑）banner图片页面
     * 
     * @param id
     *            图片编号
     * @param request
     *            请求对象
     * @return jsp路径
     */
    @RequestMapping(value = "banneropen")
    public ModelAndView openbanner(
            @RequestParam(value = "id", defaultValue = "", required = false) String id,
            HttpServletRequest request) {
        if (!"".equals(id.trim())) {
            // 查询单条banner图片信息
            Banner banner = bannerservice.getOnly(id);
            request.setAttribute("banner", banner);
        }
        return new ModelAndView("WEB-INF/views/admin/banner/updatebanner");
    }

    /**
     * 新增（编辑）banner图片
     * 
     * @param banner banner图片对象
     * @param request request
     * @param response response
     */
    @RequestMapping(value = "/updatebanner")
    public void openBanner(@ModelAttribute(value = "Banner") Banner banner,
            HttpServletRequest request, HttpServletResponse response) {
        PrintWriter out = null;
        JSONObject json = new JSONObject();
        try {
            out = response.getWriter();
            // 新增（编辑）banner图片信息
            boolean b = bannerservice.saveORupdateBannerToOSS(banner, request);
            if (b) {
                bannerservice.resetAppBanner(application);
                json.element("statusCode", "200");
                json.element("message", "更新成功");
                json.element("navTabId", "main32");
                json.element("callbackType", "closeCurrent");
            } else {
                json.element("statusCode", "300");
                json.element("message", "请上传JPG、PNG、GIF图片类型");
            }
            out.print(json);
        } catch (Exception e) {
        	logger.debug("banner图片设置更新失败", e);
            json.element("message", "更新失败");
            json.element("statusCode", "300");
            json.element("callbackType", "closeCurrent");
            bannerservice.resetAppBanner(application);
            out.print(json);
        }

    }


    /**
     * 
     * 根据id删除banner图片
     * 
     * @param ids 多个图片编号
     * @return dwzjson数据
     */
    @RequestMapping(value = "/deletebanner")
    @ResponseBody
    public JSONObject deletebanner(
            @RequestParam(value = "ids", defaultValue = "", required = false) String ids) {
        JSONObject json = new JSONObject();
        try {
            // 根据id删除banner图片
            bannerservice.deletebanner(ids);
            bannerservice.resetAppBanner(application);
            json.element("statusCode", "200");
            json.element("message", "删除成功");
            json.element("navTabId", "main32");
            return json;
        } catch (Throwable e) {
            json.element("message", "删除失败");
            json.element("statusCode", "300");
            bannerservice.resetAppBanner(application);
            return json;
        }
    }

    /**
     * 图片顺序上移
     * 
     * @param id 图片顺序号
     * @param request 请求对象
     * @return dwzjson数据
     */
    @RequestMapping(value ="/bannerup")
    @ResponseBody
    public JSONObject bannerup(
            @RequestParam(value = "id", defaultValue = "", required = true) String id,
            HttpServletRequest request) {
        JSONObject json = new JSONObject();
        try {
            List list = bannerservice.queryByNumberUp(id);
            // 如果不是第一条，选择上移
            if (list != null && list.size() > 1) {
                if (list.get(1) != null
                        && !"".equals(list.get(1).toString().trim())) {
                    Integer i = Integer.parseInt(list.get(1) + "");
                    Integer number = ((Banner) list.get(0)).getNumber();
                    // 查询
                    Banner banner = bannerservice.getBannerByNume(number);
                    banner.setNumber(i);

                    // 查询
                    Banner ber = bannerservice.getBannerByNume(i);
                    ber.setNumber(number);

                    // 修改
                    bannerservice.update(banner);
                    // 修改
                    bannerservice.update(ber);
                    
                }
            }
            bannerservice.resetAppBanner(application);
            json.element("statusCode", "200");
            json.element("message", "上移成功");
            json.element("navTabId", "main32");
            return json;
        } catch (Throwable e) {
            json.element("statusCode", "300");
            json.element("message", "上移失败");
            json.element("navTabId", "main32");
            bannerservice.resetAppBanner(application);
            return json;
        }

    }

    /**
     * 图片顺序下移
     * 
     * @param id
     *            图片顺序号
     * @param request
     *            请求对象
     * @return dwz json数据
     */
    @RequestMapping(value = "bannerdown")
    @ResponseBody
    public JSONObject bannerdown(
            @RequestParam(value = "id", defaultValue = "", required = true) String id,
            HttpServletRequest request) {
        JSONObject json = new JSONObject();
        try {
            List list = bannerservice.queryByNumberDown(id);
            // 如果不是第一条，选择上移
            if (list != null && list.size() > 1) {
                if (list.get(1) != null
                        && !"".equals(list.get(1).toString().trim())) {
                    Integer i = Integer.parseInt(list.get(1) + "");
                    Integer number = ((Banner) list.get(0)).getNumber();
                    // 查询
                    Banner banner = bannerservice.getBannerByNume(number);
                    banner.setNumber(i);
                    // 查询
                    Banner ber = bannerservice.getBannerByNume(i);
                    ber.setNumber(number);

                    // 修改
                    bannerservice.update(banner);
                    // 修改
                    bannerservice.update(ber);
                }
            }
            bannerservice.resetAppBanner(application);
            json.element("statusCode", "200");
            json.element("message", "下移成功");
            json.element("navTabId", "main32");
            return json;
        } catch (Throwable e) {
        	bannerservice.resetAppBanner(application);
            json.element("statusCode", "300");
            json.element("message", "下移失败");
            json.element("navTabId", "main32");
            return json;
        }
    }
    /**
     * 复制某条H5的banner为appBanner
     * @param id
     * @param request
     */
    @RequestMapping(value = "duplicateAppBannerFromH5")
    @ResponseBody
    public JSONObject duplicateAppBannerFromH5(@RequestParam(value = "id",  required = true) String id, HttpServletRequest request){
    	JSONObject json = new JSONObject();
    	boolean isH5=bannerservice.isH5Banner(id);
    	if(!isH5){
            json.element("statusCode", "300");
            json.element("message", "请选择H5的banner");
            json.element("navTabId", "main32");
    	}else{
    		try{
    			bannerservice.duplicateH5Banner(id);
    			bannerservice.resetAppBanner(application);
                json.element("statusCode", "200");
                json.element("message", "添加成功");
                json.element("navTabId", "main32");
    		}catch(Throwable e){
    			bannerservice.duplicateH5Banner(id);
                json.element("statusCode", "300");
                json.element("message", "添加失败");
                json.element("navTabId", "main32");
    		}

    	}
    	return json;
    	
    }
}
