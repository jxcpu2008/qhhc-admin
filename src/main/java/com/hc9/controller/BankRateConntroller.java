package com.hc9.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.common.util.ArrayToJson;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.BankRate;
import com.hc9.model.PageModel;
import com.hc9.service.BankRateService;

/**
* <p>Title:BankRateConntroller</p>
* <p>Description:银行利率控制层 </p>
* <p>Company: 前海红筹</p> 
* @author Longyang
* date 2014年2月13日
*/
@Controller
@RequestMapping("/bankrate")
@CheckLogin(value=CheckLogin.ADMIN)
public class BankRateConntroller { 
    
    /** bankRateService*/
    @Resource
    private BankRateService bankRateService;
    
    /**
     * <p>
     * Title: index
     * </p>
     * <p>
     * Description:进入银行利率管理
     * </p>
     * 
     * @return 银行利率管理展示页面
     */
    @RequestMapping(value = { "index", "/" })
    public ModelAndView index() {
        return new ModelAndView("WEB-INF/views/admin/bank_rate_list");
    }
    
    /**
    * <p>Title: queryPage</p>
    * <p>Description:  列表</p>
    * @param page 分页
    * @param request 请求
    * @param limit limit
    * @param start start
    * @return 结果集
    */
    @ResponseBody
    @RequestMapping("/querypage")
    @SuppressWarnings("rawtypes")
    public JSONObject queryPage(PageModel page,HttpServletRequest request, String limit, String start) {

        JSONObject resultjson = new JSONObject();

        JSONArray jsonlist = new JSONArray();
        
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
        List datalist = bankRateService.querymemberPage(page);
        String titles = "id,during,rate,timeupdate";
       
        //将查询结果转换为json结果集
        ArrayToJson.arrayToJson(titles, datalist, jsonlist);
        
        resultjson.element("rows", jsonlist);
        resultjson.element("total", page.getTotalCount());
        return resultjson;
        
    }
    
    /**
     * 编辑或新增
     * @param bankRate 对象
     * @param request 请求
     *     @return 是否成功
     */ 
    @ResponseBody
    @RequestMapping(value={"editbankrate","/"})
    public  boolean editbankrate( @ModelAttribute(value="BankRate")BankRate bankRate,HttpServletRequest request){
           if(StringUtil.isNotBlank(bankRate.getId()+"")&&StringUtil.isNumberString(bankRate.getId()+"")){
               //编辑
             return bankRateService.editBankrate(bankRate);
           }else{
               //add
               return bankRateService.addBankrate(bankRate);
           }
      }
    /**
     * 删除多个
     * @param ids 删除的编号
     * @param request 请求
     * @return 是否成功
    */
    @ResponseBody
    @RequestMapping(value={"delete","/"})
    public  boolean deletebankrate(String ids,HttpServletRequest request){
            return bankRateService.deleteBankrate(ids);
      }
}
