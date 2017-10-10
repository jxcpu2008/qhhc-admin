package com.hc9.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.common.constant.Constant;
import com.hc9.common.util.ArrayToJson;
import com.hc9.common.util.DwzResponseUtil;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.ShopComment;
import com.hc9.model.PageModel;
import com.hc9.service.ShopCommentService;

/***
 * 店铺评论回复与删除
 * @author LKL
 *
 */
@Controller
@RequestMapping("ShopComment")
@CheckLogin(value=CheckLogin.ADMIN)
public class ShopCommentController {
	@Resource
    private ShopCommentService shopCommentService;
	
	/**
	 * 跳转到店铺评论审批
	 * 
	 * @return
	 */
	@RequestMapping("openShopComment")
	public String initshopComment() {
		   return "WEB-INF/views/admin/comment/shopComment";
	}
	
	/***
	 * 跳转到回复评论信息
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryShopCommentInfo")
	public String queryShopCommentInfo(String id, HttpServletRequest request) {
		if (StringUtil.isNotBlank(id) && StringUtil.isNumberString(id)) {
			ShopComment shopComment = shopCommentService.queryShopComment(id);
			request.setAttribute("shopComment", shopComment);
		}
		return "/WEB-INF/views/admin/comment/shopReplyer";
	}
	
	/**
	 * 查询店铺列表
	 * 
	 * @param limit
	 * @param start
	 * @param request
	 * @param page
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("shopCommentList")
	@ResponseBody
	public JSONObject shopCommentList(String limit, String start,
			HttpServletRequest request, PageModel page, ShopComment comment) {
		JSONObject resultjson = new JSONObject();
		// 每页显示条数
		if (StringUtil.isNotBlank(limit) && StringUtil.isNumberString(limit)) {
			page.setNumPerPage(Integer.parseInt(limit) > 0 ? Integer
					.parseInt(limit) : 20);
		} else {
			page.setNumPerPage(20);
		}
		// 计算当前页
		if (StringUtil.isNotBlank(start) && StringUtil.isNumberString(start)) {
			page.setPageNum(Integer.parseInt(start) / page.getNumPerPage() + 1);
		}
		// 分页数据源
		List list = shopCommentService.getShopComment(page, comment);
		JSONArray jsonlist = new JSONArray();
		String titles = "id,cmtContent,cmtIsShow,commentTime,cmtReply,replyTime,cmtUserName,adminUserName,replyerUserName,shopName";
		// 将查询结果转换为json结果集
		ArrayToJson.arrayToJson(titles, list, jsonlist);
		// 将数据源封装成json对象（命名必须row）
		resultjson.element("rows", jsonlist);
		// 总条数(命名必须total)
		resultjson.element("total", page.getTotalCount());
		return resultjson;
	}
	
    /***
     * 店铺评论删除
     * @param request
     * @param state
     * @param ids
     * @param approvalExplain
     */
    @ResponseBody
    @RequestMapping("/delShopComment")
	public String  delShopComment(HttpServletRequest request,String ids){
		return shopCommentService.delShopComment(ids);
	}
    
	
    /***
     * 客服回复评论信息
     * @param request
     * @param state
     * @param ids
     * @param approvalExplain
     */
    @ResponseBody
    @RequestMapping("/uptShopComment")
	public JSONObject  uptShopComment(HttpServletRequest request,ShopComment shopComment, String id){
    	 JSONObject json = new JSONObject();
		if (StringUtil.isNotBlank(id)) {
		      boolean del=shopCommentService.uptShopComment(request, shopComment, id);
		      if(del){
		    	  DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_SUCCESS,
		                 "回复评论成功", "main55", "closeCurrent");
		      }else{
		    	  DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_ERROR,
		                  "回复评论失败", "main55", "closeCurrent");
		      }
		}
		return json;
	}
    
}
