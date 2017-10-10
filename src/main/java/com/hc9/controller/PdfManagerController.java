package com.hc9.controller;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.common.constant.Constant;
import com.hc9.common.util.ArrayToJson;
import com.hc9.common.util.DwzResponseUtil;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.Uploadfile;
import com.hc9.model.PageModel;
import com.hc9.service.PdfManageService;

@Controller
@RequestMapping(value = { "pdfManage" })
@CheckLogin(value=CheckLogin.ADMIN)
public class PdfManagerController {
	
	@Resource
	PdfManageService pdfService;
	
	/**
	 * @return
	 */
	@RequestMapping("pdfList")
	public ModelAndView openPdfList(){
		return  new ModelAndView("WEB-INF/views/admin/column/pdflist");
	}
	
	@RequestMapping("openDiaLog")
	public ModelAndView openDiaLog(){
		return new ModelAndView("WEB-INF/views/admin/column/pdfDialog");
	}
	
	 /**
	  * 上传PDF文件
	 * @param uploadfile
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = { "uploadPdf", "/" })
	    public void uploadPdf(
	            @ModelAttribute(value = "Uploadfile") Uploadfile uploadfile,
	            HttpServletRequest request,HttpServletResponse response) {
	        String pageid="main"+uploadfile.getId();
	        JSONObject json = new JSONObject();
	        PrintWriter out = null;
	        try {
	            // 新增
	            Integer result = pdfService.uploadPdf(request);
	            response.setContentType("text/html;charset=UTF-8");
	            out=response.getWriter();
	            if (result==3) {
	                DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_SUCCESS, "上传成功", pageid, "closeCurrent");
	            } else if(result==1){
	                DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_ERROR, "请选择图PDF文件", pageid,  "closeCurrent");
	            }else if(result==2){
	                DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_ERROR, "请上传PDF图片类型", pageid,  "closeCurrent");
	            }
	            out.print(json);
	        } catch (Throwable e) {
	            DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_ERROR, "更新失败", pageid, "closeCurrent");
	            out.print(json);
	        }
	    }

	   /**
	    * 查询pdf列表
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	@ResponseBody
	@RequestMapping( "getPdfList")
	public JSONObject  getPdfList(String limit,String start,Uploadfile uploadfile,HttpServletRequest request,PageModel page) throws UnsupportedEncodingException{
		   
		JSONObject resultjson = new JSONObject();
        // 得到总条数
       /* int count = loanSignService.getLoansignCount(loansignbasics,loanType);*/
        
        // 每页显示条数
        if (StringUtil.isNotBlank(limit) && StringUtil.isNumberString(limit)) {
            page.setNumPerPage(Integer.parseInt(limit) > 0 ? Integer
                    .parseInt(limit) : 10);
        } else {
            page.setNumPerPage(10);
        }
 
        // 计算当前页
        if (StringUtil.isNotBlank(start) && StringUtil.isNumberString(start)) {
            page.setPageNum(Integer.parseInt(start) / page.getNumPerPage() + 1);
        }
              
        // 分页数据源
        List list = pdfService.pdfPage(page,uploadfile);
       /* JSONArray jsonlist = loanSignService.queryJSONByList(list);*/
        JSONArray jsonlist = new JSONArray();
        String titles = "id,name,savePath,saveName,addTime";
     // 将查询结果转换为json结果集
        ArrayToJson.arrayToJson(titles, list, jsonlist);

        // 将数据源封装成json对象（命名必须row）
        resultjson.element("rows", jsonlist);
        // 总条数(命名必须total)
        resultjson.element("total", page.getTotalCount());

        return resultjson;
		
	   }
	
	/**
	 * 删除pdf文件
	 * @param id
	 * @param request
	 * @return
	 */
	@ResponseBody
    @RequestMapping(value = { "delPdf", "/" })
    public Boolean delPdf(
            @RequestParam(value = "id", defaultValue = "", required = true) String id,
            HttpServletRequest request) {
        // 删除附件
        return  pdfService.delPdf(id, request);
    }
}
	
