package com.hc9.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.common.constant.Constant;
import com.hc9.common.util.ArrayToJson;
import com.hc9.common.util.DwzResponseUtil;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.CardImgAudit;
import com.hc9.model.PageModel;
import com.hc9.model.RechargeModel;
import com.hc9.service.CardImgAuditQueryService;


/***
 * 实名验证审核
 * @author lkl
 *
 */
@Controller
@CheckLogin(value=CheckLogin.ADMIN)
@RequestMapping("/cardImgAudit")
public class CardImgAuditController {
	
	@Resource
	private CardImgAuditQueryService cardImgAuditQueryService;
	
	@Resource
	private RechargeModel rechargeModel;
	
	private static Map<Integer,String> OPER;
	
	private static Map<Integer,String> ISAUTHIPSOPER;

	
	static{
		OPER=new HashMap<>();
		OPER.put(0, "待审核");
		OPER.put(1, "审核通过");
		OPER.put(2, "审核不通过");
	} 
	
	static{
		ISAUTHIPSOPER=new HashMap<>();
		ISAUTHIPSOPER.put(0, "待确认"); 
		ISAUTHIPSOPER.put(1, "已授权");
		ISAUTHIPSOPER.put(2, "授权失败");
	} 
	
	
	@RequestMapping(value = { "indexQuery", "/" })
	public ModelAndView indexQuery() {
		ModelAndView returnModelAndView = new ModelAndView("WEB-INF/views/admin/quickquery/cradImgAudit");
		return returnModelAndView;
	}
	
	/***
	 * 列表查询
	 * @param limit
	 * @param start
	 * @param userrelationinfo
	 * @param request
	 * @param page
	 * @return
	 */
	@ResponseBody
	@RequestMapping("cardImgAuditQuery")
	public JSONObject cardImgAuditQuery(String limit, String start,CardImgAudit cardImgAudit, HttpServletRequest request, PageModel page) {

		JSONObject resultjson = new JSONObject();
		JSONArray jsonlist = new JSONArray();

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
		List datalist = cardImgAuditQueryService.queryCardImgAuditPage(page, cardImgAudit);
		String titles = "id,name,phone,isAuthIps,cardId,cardImgTime,cardImgState,cardImgRemark,cardImgAuditTime,realname,genName";
		// 将查询结果转换为json结果集
		ArrayToJson.arrayToJson(titles, datalist, jsonlist);

		// 将数据源封装成json对象（命名必须row）
		resultjson.element("rows", jsonlist);
		// 总条数(命名必须total)
		resultjson.element("total", page.getTotalCount());

		return resultjson;
	}
	
	/***
	 * 导出数据
	 * @param outLoanrecordExcel
	 * @param request
	 * @param response
	 */
	@RequestMapping("outPutCardImgAuditExcel")
	public void outPutCardImgAuditExcel(CardImgAudit cardImgAudit, HttpServletRequest request,HttpServletResponse response) {
		// 标题
		String[] header = new String[] { "申请人姓名", "申请人手机号", "宝付授权状态", "身份证号码", "申请时间","状态","备注", "操作时间","操作人","推荐人"};
		
		// 获取数据源
		List list = cardImgAuditQueryService.queryCardImgAuditList(cardImgAudit);

		List<Map<String, String>> content = new ArrayList<Map<String, String>>();
		Map<String, String> map = null;
		for (Object obj : list) {
			Object[] str = (Object[]) obj;
			map = new HashMap<String, String>();
			map.put("申请人姓名", str[1] + "");
			map.put("申请人手机号", str[2] + "");
			map.put("宝付授权状态",ISAUTHIPSOPER.get(str[3]) + "");
			map.put("身份证号码", str[4] + "");
			map.put("申请时间", str[5] + "");
			map.put("状态", OPER.get(str[6]));
			map.put("备注", str[7] + "");
			map.put("操作时间", str[8] + "");
			map.put("操作人", str[9] + "");
			map.put("推荐人", str[10] + "");
			content.add(map);
		}
		// 下载excel
		rechargeModel.downloadExcel("实名验证审核列表", null, header, content, response,
				request);
	}
	
	/***
	 * 获取图片路径
	 * @param request
	 * @param id
	 * @return
	 */
	@RequestMapping("getCardImg")
	public String getCardImg(HttpServletRequest request, String id) {
		CardImgAudit cardImgAudit =cardImgAuditQueryService.getCardImgAudit(id);
		request.setAttribute("cardImg", cardImgAudit.getCardImg());
		return "WEB-INF/views/admin/column/loansignInfo";
	}
	
	/***
	 * 申请审核
	 * @param ids
	 * @param examine  1-审核成功  2-审核失败
	 * @param remark
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/auditCardImgState")
	public String auditCardImgState(String ids,Integer  examine, String remark,HttpServletRequest request) {
		String num = cardImgAuditQueryService.updateCardImgState(ids, examine, remark, request);
		return num;
	}
	
	/***
	 * 审核不通过
	 * @param id
	 * @param examine
	 * @param remark
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/auditCardImgStateNotPass")
	public JSONObject auditCardImgStateNotPass(String id,Integer  examine, String remark,HttpServletRequest request) {
		JSONObject json = new JSONObject();
		String num = cardImgAuditQueryService.updateCardImgState(id, examine,  remark, request);
		if(num.equals("1")){
			DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_SUCCESS,"审核成功", "main124", "closeCurrent");
		}else{
			DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_ERROR,"审核失败", "main124", "closeCurrent");
		}
		return json;
	}
	
	/***
	 * 跳转审核不通过页面
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryCardImgState")
	public String queryCardImgState(String id, HttpServletRequest request) {
		request.setAttribute("id", id);
		return "/WEB-INF/views/admin/quickquery/updateCardImgAudit";
	}
	
	/***
	 * 查看图片
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping("/selCardImgState")
	public String selCardImgState(String id, HttpServletRequest request) {
		CardImgAudit audit=cardImgAuditQueryService.getCardImgAudit(id);
		request.setAttribute("cardImg", audit.getCardImg());
		return "/WEB-INF/views/admin/quickquery/selCardImgAudit";
	}
	
	/***
	 * 一键更改现金状态
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updateActivityMonkey")
	public String updateActivityMonkey(HttpServletRequest request) {
		return cardImgAuditQueryService.updateActivityMonkey(request);
	}

}
