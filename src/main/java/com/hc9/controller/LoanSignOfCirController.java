package com.hc9.controller;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.common.constant.Constant;
import com.hc9.common.util.Arith;
import com.hc9.common.util.DateUtils;
import com.hc9.dao.HibernateSupportTemplate;
import com.hc9.dao.entity.Costratio;
import com.hc9.dao.entity.Loansign;
import com.hc9.dao.entity.Loansignbasics;
import com.hc9.dao.entity.Repaymentrecord;
import com.hc9.model.RechargeModel;
import com.hc9.service.BaseLoansignService;
import com.hc9.service.LoanSignOfCirService;
import com.hc9.service.LoanSignQuery;
import com.hc9.service.RepaymentrecordService;

/**
 * <p>
 * Title:LoanSignOfCirController
 * </p>
 * <p>
 * Description: 债权转让
 * </p>
 * <p>
 * Company: 前海红筹
 * </p>
 * 
 * @author frank
 *         <p>
 *         date 2014年1月26日
 *         </p>
 */
@Controller
@RequestMapping("/loanSignOfCir")
@CheckLogin(value=CheckLogin.ADMIN)
public class LoanSignOfCirController {

    /** 用于调用常用方法的dao */
    @Resource
    HibernateSupportTemplate dao;

    /** baseLoansignService 借款标公用方法的service*/
    @Resource
    private BaseLoansignService baseLoansignService;
    /** 还款记录的service */
    @Resource
    private RepaymentrecordService repaymentrecordService;

    
    /** loanSignOfCirService 流转标的service*/
    @Resource
    private LoanSignOfCirService loanSignOfCirService;

    /** loanSignQuery 标的查询公用方法类*/
    @Autowired
    private LoanSignQuery loanSignQuery;

    /** rechargeModel 导出所引用的*/
    @Resource
    private RechargeModel rechargeModel;
    

    /**
    * <p>Title: index</p>
    * <p>Description: 路径跳转</p>
    * @return 流转标页面
    */
    @RequestMapping(value = {"index","/" })
    public ModelAndView index() {
        return new ModelAndView("WEB-INF/views/admin/loansign/loansignCir");
    }

    /**
     * 
    * <p>Title: loanSignCirList</p>
    * <p>Description: 借款标列表</p>
    * @param start 开始
    * @param limit 结束
    * @param loansignbasics 高级搜索查询条件
    * @param request 请求
    * @return 数据对象
     */
    @ResponseBody
    @RequestMapping(value = { "list", "/" })
    public JSONObject loanSignCirList(
            @RequestParam(value = "start", defaultValue = "0", required = false) int start,
            @RequestParam(value = "limit", defaultValue = "10", required = false) int limit,
            Loansignbasics loansignbasics,String loanType, HttpServletRequest request) {

        JSONObject resultjson = new JSONObject();
        // 得到总条数
        int count = loanSignOfCirService.getLoansignCirCount(loansignbasics,loanType);

        // 分页数据源
        List list = loanSignOfCirService.loanSignCirPage(start, limit,
                loansignbasics,loanType);
        JSONArray jsonlist = loanSignOfCirService.queryJSONByList(list);

        // 将数据源分钟成json对象（命名必须row）
        resultjson.element("rows", jsonlist);
        // 总条数(命名必须total)
        resultjson.element("total", count);

        // resultjson数据格式如：
        return resultjson;
    }
    
    

    /**
    * <p>Title: saveOrUpdateLoansignSic</p>
    * <p>Description: 保存or修改流转标</p>
    * @param loanSign 流转标信息
    * @param loansignbasics 流转标基础信息
    * @param request 请求
    * @return 数据集合对象
    */
    @ResponseBody
    @RequestMapping(value = { "saveORupdateLoansignSic", "/" })
    public JSONObject saveOrUpdateLoansignSic(Loansign loanSign,
            Loansignbasics loansignbasics, HttpServletRequest request) {
	      JSONObject json = new JSONObject();
	      StringBuffer sb=new StringBuffer("");
	     // 2.检查有没有设置费用比例
	      Costratio costratio = loanSignQuery.queryCostratio();
	      boolean bool = true;
            // insert
            // 1.检查授信额度
           /* if (!baseLoansignService.checkCredit(loanSign)) {
                bool=false;
                sb=sb.append("保存失败,借款金额超过借款人的可用授信额度！");
            }*/
            if (bool&&costratio == null) {
                bool=false;
                sb=sb.append("请设置费用比例后在进行发标操作！");
            }
            // 5.添加
            if(bool){
             //    bool = baseLoansignService.saveAssignment(loanSign, loansignbasics, costratio);
            }
	    if (bool) {
	        // dwz返回json对象
	        json.element("statusCode", "200");
	        json.element("message", "更新成功");
	        json.element("navTabId", "main58");
	        json.element("callbackType", "closeCurrent");
	        return json;
	    }else{
	        json.element("callbackType", "closeCurrent");
	        json.element("statusCode", "300");
	        json.element("message", sb.toString());
	        return json;
	    }
	  
    }

  
    /**
    * <p>Title: cirOfPublish</p>
    * <p>Description:  流转标发布</p>
    * @param loanSignId  借款编号
    * @return  1.成功 2.报错 3.不是未发布的标
    */
    @ResponseBody
    @RequestMapping("/cirOfPublish")
    public int cirOfPublish(String loanSignId) {
        // 1、检查是否可以发布
        Loansign loansign = loanSignQuery.getLoansignById(loanSignId);
        if (loansign.getState() != 1) {
            return 3;
        }
        // 2.发布
       /* boolean bool = baseLoansignService.publishCir(loansign);
        if (bool == false) {
            return 2;
        }*/
        return 1;
    }

    /**
    * <p>Title: credit</p>
    * <p>Description: 流转标放款 </p>
    * @param loanSignId 1.成功 2放款出现异常 3.放款失败,只有回款中的标可以放款,请尝试刷新页面 4.放款失败,该流转款已经放款,请尝试刷新页面
    * @return 数字状态
    */
    @ResponseBody
    @RequestMapping("/credit")
    public int credit(String loanSignId) {
        // 1、检查是否可以放款
        Loansign loansign = loanSignQuery.getLoansignById(loanSignId);
        if (loansign.getState() != 3) {// 是否是回款中
            return 3;
        }
        // 是否已经放过款了
        Loansignbasics loansignbasics = loanSignQuery
                .getLoansignbasicsById(loanSignId);
        /*if (null != loansignbasics.getCreditTime()
                && !"".equals(loansignbasics.getCreditTime())) {
            return 4;
        }*/
        // 3.放款
        boolean bool = baseLoansignService.credit(loansign);
        if (bool == false) {
            return 2;
        }
        return 1;
    }

    /**
    * <p>Title: onTimeRepay</p>
    * <p>Description: 按时还款</p>
    * @param repaymentRecordId  还款编号
    * @param repayTime 还款时间
    * @return  数字状态
    *  1.成功 2.失败 3还款失败,只能针对未还款记录还款,请尝试刷新页面 4还款失败,只能针对已放款的标进行还款,请尝试刷新页面！
     * 5.还款失败,按时还款的时间不能大于预计还款时间！ 6.还款失败,只能按期数顺序依次还款！
    * @throws Exception  异常
    */ 
    @ResponseBody
    @RequestMapping("/onTimeRepay")
    public int onTimeRepay(String repaymentRecordId, String repayTime)
            throws Exception {
        // 1.判断是否可以还款
        Repaymentrecord repaymentrecord = dao.get(Repaymentrecord.class,
                Long.valueOf(repaymentRecordId));
        if (repaymentrecord.getRepayState() != 1) {// 1.未还款
            return 3;
        }
        Loansignbasics loansignbasics = loanSignQuery
                .getLoansignbasicsById(repaymentrecord.getLoansign().getId()
                        .toString());
        // 没有放款的流转标不能还款
       /* if (loansignbasics.getCreditTime() == null) {
            return 4;
        }*/
        // 按时还款的时间不能大于预计还款时间
        if (DateUtils.isAfter("yyyy-MM-dd", repayTime, "yyyy-MM-dd",
                repaymentrecord.getPreRepayDate())) {
            return 5;
        }

        // 判断是否按期数还款
        if (loanSignQuery.checkRepayOrder(repaymentrecord)) {
            return 6;

        }
        // 还款
        boolean bool = baseLoansignService.onTimeRepay(repaymentrecord,
                repayTime);
        if (bool == false) {
            return 2;
        }
        // **更新认购信息表下期该收的金额***************

        return 1;
    }

    /**
    * <p>Title: exceedTimeRepay</p>
    * <p>Description: 逾期还款</p>
    * @param repaymentRecordId 还款记录
    * @return  int
    * 1成功 2.还款出现异常 3还款失败,只能针对未还款记录还款,请尝试刷新页面 4.还款失败,只能针对已放款的标进行还款,请尝试刷新页面！
    * 5.还款失败,只能按期数顺序依次还款！
    */
   /* @ResponseBody
    @RequestMapping("/exceedTimeRepay")
    public int exceedTimeRepay(String repaymentRecordId) {
        // 1.判断是否可以还款
        Repaymentrecord repaymentrecord = dao.get(Repaymentrecord.class,
                Long.valueOf(repaymentRecordId));
        if (repaymentrecord.getRepayState() != 1) {// 1.未还款
            return 3;
        }
        Loansignbasics loansignbasics = loanSignQuery
                .getLoansignbasicsById(repaymentrecord.getLoansign().getId()
                        .toString());
        // 没有放款的流转标不能还款
        if (loansignbasics.getCreditTime() == null) {
            return 4;
        }

        // 判断是否按期数还款
        if (loanSignQuery.checkRepayOrder(repaymentrecord)) {
            return 5;

        }
        // 逾期还款
        boolean bool = baseLoansignService.exceedTimeRepay(repaymentrecord);
        if (bool == false) {
            return 2;
        }
        // **更新认购信息表下期该收的金额***************---------

        return 1;
    }*/

    /**
    * <p>Title: exceedTimeRepayed</p>
    * <p>Description: 逾期已还款</p>
    * @param repaymentRecordId  逾期已还款的编号
    * @param repayTime 还款时间
    * @return  1.成功 2还款失败,只有平台垫付的标可以还款,请尝试刷新页面！ 3.还款失败,实际还款时间必须大于预计还款时间！
    * @throws ParseException 异常
    */
    @ResponseBody
    @RequestMapping("/exceedTimeRepayed")
    public int exceedTimeRepayed(String repaymentRecordId, String repayTime)
            throws ParseException {
        JSONObject json = new JSONObject();
        // 1.判断是否可以还款
        Repaymentrecord repaymentrecord = dao.get(Repaymentrecord.class,
                Long.valueOf(repaymentRecordId));
        if (repaymentrecord.getRepayState() != 3) {// 3.逾期未还款
            return 2;
        }
        if (DateUtils.isAfter("yyyy-MM-dd", repaymentrecord.getPreRepayDate(),
                "yyyy-MM-dd", repayTime)) {
            return 3;
        }

        // 写入实际还款时间和状态=4,并计算出实际的还款金额=本该还的利息和逾期的手续费
        repaymentrecord.setRepayState(4);
        repaymentrecord.setRepayTime(repayTime);
        BigDecimal money = new BigDecimal(repaymentrecord.getMoney())
                .add(new BigDecimal(repaymentrecord.getPreRepayMoney()));

        repaymentrecord.setRealMoney(money.multiply(
                new BigDecimal(Constant.OVERDUE_INTEREST).add(new BigDecimal(
                        repaymentrecord.getPreRepayMoney()))).doubleValue());
        repaymentrecordService.update(repaymentrecord);
        return 1;
    }

   /* *//**
     * 导出债权转让标
     * @param id
     * @param request
     * @param response
     *//*
    @RequestMapping("outPutLoanrecordExcel")
	public void outPutLoanrecordExcel(int id,HttpServletRequest request, HttpServletResponse response) {

		// 标题
		String[] header = new String[] { "项目名称", "项目所属行业", "用户名","购买金额", "购买时间", "购买状态","特权会员","投资类型","管理费" };
		// 行宽度
		Integer[] column = new Integer[] { 12, 10, 12, 12, 8, 8, 8, 10 };
		
		// 获取数据源
		List list = loanrecordService.queryLoanrecordList(0, 0, id, 2);

		List<Map<String, String>> content = new ArrayList<Map<String, String>>();
		Map<String, String> map= null;
		for (Object obj : list) {
			Object[] str = (Object[]) obj;
			map = new HashMap<String, String>();
			map.put("项目名称", str[0]+"");
			map.put("用户名", str[1]+"");
			map.put("购买金额",
	                    Arith.round(new BigDecimal(str[2].toString()), 2) + "元");
			map.put("购买时间", str[3]+"");
			map.put("购买状态", str[4]+"");
			map.put("特权会员", str[5]+"");
			map.put("投资类型", str[6]+"");
			map.put("管理费", str[7]+"");
			content.add(map);
			
		}
		// 下载excel
		rechargeModel.downloadExcel("出借记录", null, header, content, response,request);
	}
    */
    /**
    * <p>Title: outPutLoanSignOfDayExcel</p>
    * <p>Description: 导出流转标借款列表</p>
    * @param request request
    * @param response response
    * 
    */
    @RequestMapping("outPutExcel")
    public void outPutLoanSignOfDayExcel(HttpServletRequest request,HttpServletResponse response) {
        // 标题
    	 String[] header = new String[] {"序号", "借款标号", "标题", "借款人", "最小出借单位","借款金额", "还款期限", "借款标类型", "借款管理费", "年化利率", "平台奖励","成功借出份数", "剩余份数", "还款方式", "借款标状态", "是否放款", "放款时间", "发布时间","是否显示", "推荐到首页"};
        // 行宽度
    	 Integer[] column = new Integer[] { 8, 10, 11, 12,12, 10, 10, 12, 10,
                 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10 };
        // 获取数据源
        List list = loanSignOfCirService.outPutList();

        List<Map<String, String>> content = new ArrayList<Map<String, String>>();
        Map<String, String> map = null;

        for (Object obj : list) {
            Object[] str = (Object[]) obj;
            map = new HashMap<String, String>();
            map.put("序号", str[0] + "");
            map.put("借款标号", str[1] + "");
            map.put("标题", str[2] + "");
            map.put("借款人", str[3] + "");
            map.put("最小出借单位", str[5] + "");
            map.put("借款金额", Arith.round(new BigDecimal(str[6].toString()), 2)
                    + "元");
            map.put("还款期限", str[7] + "个月");
            map.put("借款标类型", str[8] + "");
            map.put("借款管理费", str[9] + "");
            map.put("年化利率", Arith.round(new BigDecimal(str[11].toString()), 2)
                    + "%");
            map.put("平台奖励", Arith.round(new BigDecimal(str[12].toString()), 2)
                    + "%");
            map.put("成功借出份数", Integer.parseInt(str[13].toString()) + "");
            map.put("剩余份数", Double.valueOf(str[14].toString()) > 0 ? str[14]
                    + "" : "满标");
            map.put("还款方式", str[15] + "");
            map.put("借款标状态", str[16] + "");
            map.put("是否放款", str[17] + "");
            map.put("发布时间", null != str[10] ? str[10].toString() : "");
            map.put("放款时间", null != str[18] ? str[18].toString() : "");
            map.put("是否显示", str[19] + "");
            map.put("推荐到首页", str[20] + "");
            content.add(map);
        }
        // 下载excel
        rechargeModel.downloadExcel("流转标", column, header, content, response);

    }

    
    
    
    /**
    * <p>Title: queryDetails</p>
    * <p>Description: 查询详情</p>
    * @param id 标编号
    * @param operNumber 操作号 1 新增 2 编辑 3 查看详情
    * @param request   request
    * @return 查询详情的页面
    */
   /* @RequestMapping("/queryDetails")
    public ModelAndView queryDetails(
            @RequestParam(value = "id", defaultValue = "", required = false) String id,
            @RequestParam(value = "userName", defaultValue = "", required = false) String userName,
            int operNumber, HttpServletRequest request) {

        if (null != id && !id.trim().equals("") && operNumber != 1) {
            // 通过id查询到信息
            Loansign loansign = loanSignQuery.getLoansignById(id);
            loansign.setRate(Arith.round(
                    new BigDecimal(loansign.getRate()).multiply(new BigDecimal(
                            100)), 2).doubleValue());

            Loansignbasics loansignbasics = loanSignQuery
                    .getLoansignbasicsById(id);
            String userId=loanSignQuery.getID(userName);
            Userbasicsinfo userbasicsinfo=loanSignQuery.getUserbasicsinfo(userId);
            if(operNumber==3){
            	request.setAttribute("name", loansign.getUserbasicsinfo().getName());
                request.setAttribute("tenderMoney", loansign.getIssueLoan());
            }else{
            	String tenderMoney=loanSignQuery.getTenderMoney(id,userId.toString());
            	request.setAttribute("name", userbasicsinfo.getName());
                request.setAttribute("tenderMoney", tenderMoney.substring(1, tenderMoney.length()-1));
            }
            request.setAttribute("loansign", loansign);
            request.setAttribute("loansignbasics", loansignbasics);
        } else {
            request.setAttribute("loansign", new Loansign());
            request.setAttribute("loansignbasics", new Loansignbasics());
            request.setAttribute("loansignflow", new Loansignflow());
        }
        request.setAttribute("operNumber", operNumber);
        return new ModelAndView("WEB-INF/views/admin/loansign/editloansignCir");
    }*/
    
    
    
    
    
}
