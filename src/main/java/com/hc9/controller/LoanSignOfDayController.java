package com.hc9.controller;

import java.math.BigDecimal;
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
import com.hc9.common.util.Arith;
import com.hc9.dao.HibernateSupportTemplate;
import com.hc9.dao.UserInfoQuery;
import com.hc9.dao.entity.Costratio;
import com.hc9.dao.entity.Loansign;
import com.hc9.dao.entity.Loansignbasics;
import com.hc9.dao.entity.Repaymentrecord;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.model.RechargeModel;
import com.hc9.service.BaseLoansignService;
import com.hc9.service.LoanSignFund;
import com.hc9.service.LoanSignQuery;

/**
 * 天标Controller
 * 
 * @author frank
 * 发标传一个vip进去2014-8-27
 */
@Controller
@RequestMapping("/loanSignOfDay")
@CheckLogin(value=CheckLogin.ADMIN)
public class LoanSignOfDayController {

    /** dao */
    @Resource
    private HibernateSupportTemplate dao;

    /** baseLoansignService*/
    @Resource
    private BaseLoansignService baseLoansignService;

    /** rechargeModel*/
    @Resource
    private RechargeModel rechargeModel;

    /** loanSignFund*/
    @Autowired
    private LoanSignFund loanSignFund;

    /** loanSignQuery*/
    @Autowired
    private LoanSignQuery loanSignQuery;

    /** loanSignOfDayService*/
    @Resource
    private LoanSignOfDayService loanSignOfDayService;
    
    @Resource
    private UserInfoQuery userInfoQuery;

    /**
     * 初始化
     * 
     * @return 页面
     */
    @RequestMapping(value = { "index", "/" })
    public ModelAndView index() {
        return new ModelAndView("WEB-INF/views/admin/loansign/loansignDay");
    }

    /**
    * <p>Title: loanSignOfDayPage</p>
    * <p>Description: 列表展示</p>
    * @param start start
    * @param limit limit
    * @param loansignbasics 借款标基础信息
    * @param request 请求
    * @return 集合
    */
    @ResponseBody
    @RequestMapping(value = { "list", "/" })
    public 
    JSONObject loanSignOfDayPage(
            @RequestParam(value = "start", defaultValue = "0", required = false) int start,
            @RequestParam(value = "limit", defaultValue = "10", required = false) int limit,
            Loansignbasics loansignbasics,String loanType,HttpServletRequest request) {

        JSONObject resultjson = new JSONObject();
        // 得到总条数
        int count = loanSignOfDayService.getLoansignDayCount(loansignbasics,loanType);

        // 分页数据源
        List list = loanSignOfDayService.loanSignDayPage(start, limit,
                loansignbasics,loanType);
        JSONArray jsonlist = loanSignOfDayService.queryJSONByList(list);

        // 将数据源分钟成json对象（命名必须row）
        resultjson.element("rows", jsonlist);
        // 总条数(命名必须total)
        resultjson.element("total", count);

        // resultjson数据格式如：
        return resultjson;
    }

    /**
    * <p>Title: saveORupdateDayLoansign</p>
    * <p>Description: 保存or修改天标</p>
    * @param loanSign 借款标
    * @param loansignbasics 借款标基础信息
    * @param request 请求
    * @return 集合
    */
    @ResponseBody
    @RequestMapping(value = { "saveORupdateDayLoansign", "/" })
    public JSONObject saveORupdateDayLoansign(Loansign loanSign, Loansignbasics loansignbasics, HttpServletRequest request) {
        JSONObject json = new JSONObject();
        Userbasicsinfo user=loanSign.getUserbasicsinfo();
        boolean isVip=userInfoQuery.isPrivilege(user);
        json.element("callbackType", "closeCurrent");
        json.element("statusCode", "300");
            // 判断本期借款金额是否正确
           /* if (loanSign.getIssueLoan() % loanSign.getLoanUnit() != 0) {
                json.element("message", "请输入能正确的借款金额！");
                return json;
            }*/
//            // 结束时间不能小于当前时间
//            if (DateUtils.isAfter(Constant.DEFAULT_DATE_FORMAT,
//                    loanSign.getEndTime())) {
//                json.element("message", "结束时间应在当前时候之后！");
//                return json;
//            }
         // 2.检查有没有设置费用比例
            Costratio costratio = loanSignQuery.queryCostratio();
       /*     loansignbasics.setReward(loansignbasics.getReward() / 100);
            loanSign.setRate(loanSign.getRate() / 100);
            loanSign.setLoanType(2);// 天标
            if (loanSign.getId() != null) {
                // update
                boolean bool = baseLoansignService.update(loanSign,
                        loansignbasics,costratio);
                if (bool == false) {
                    json.element("callbackType", "closeCurrent");
                    return json;
                }
            } else {
                // insert
                // 1.检查授信额度
                if (!baseLoansignService.checkCredit(loanSign)) {
                    json.element("message", "保存失败,借款金额超过借款人的可用授信额度！");
                    return json;
                }

                if (costratio == null) {
                    json.element("message", "请设置费用比例后在进行发标操作！");
                    return json;
                }

                // 3.添加
                //TODO 到期一次还本息
                loanSign.setRefundWay(3);
                boolean bool = baseLoansignService.save(loanSign,
                        loansignbasics, costratio,isVip);
                if (bool == false) {
                    json.element("callbackType", "closeCurrent");
                    return json;
                }
            }
            json.element("statusCode", "200");
            json.element("message", "更新成功");
            json.element("navTabId", "main40");
            json.element("callbackType", "closeCurrent");*/
            return json;
    }


    /**
    * <p>Title: queryDetails</p>
    * <p>Description: 查询详情</p>
    * @param id 标编号
    * @param operNumber 操作号 1 新增 2 编辑 3 查看详情
    * @param request 请求
    * @return 页面
    */
    @RequestMapping("/queryDetails")
    public ModelAndView queryDetails(
            @RequestParam(value = "id", defaultValue = "", required = false) String id,
            int operNumber, HttpServletRequest request) {

       /* if (null != id && !id.trim().equals("") && operNumber != 1) {
            // 通过id查询到信息
            Loansign loansign = loanSignQuery.getLoansignById(id);
            loansign.setRate(Arith.round(
                    new BigDecimal(loansign.getRate()).multiply(new BigDecimal(
                            100)), 2).doubleValue());

            Loansignbasics loansignbasics = loanSignQuery
                    .getLoansignbasicsById(id);
            loansignbasics.setReward(Arith.round(
                    new BigDecimal(loansignbasics.getReward())
                            .multiply(new BigDecimal(100)), 2).doubleValue());

            request.setAttribute("loansign", loansign);
            request.setAttribute("loansignbasics", loansignbasics);
        } else {
            request.setAttribute("loansign", new Loansign());
            request.setAttribute("loansignbasics", new Loansignbasics());
        }*/
        request.setAttribute("operNumber", operNumber);
        return new ModelAndView("WEB-INF/views/admin/loansign/editloansignDay");
    }

    /**
    * <p>Title: dayTimeRepay</p>
    * <p>Description: 还款(天标就一种还款方式,还款的金额通过实际使用天数进行计算的) </p>
    * @param repaymentRecordId  还款编号
    * @param repayTime 还款时间
    * @return 1.成功 2.还款失败,只能针对未还款记录还款,请尝试刷新页面！ 3.其他
    */
    @ResponseBody
    @RequestMapping("/daytime_repay")
    public 
    int dayTimeRepay(String repaymentRecordId, String repayTime) {
            // 1.判断是否可以还款
            Repaymentrecord repaymentrecord = dao.get(Repaymentrecord.class,
                    Long.valueOf(repaymentRecordId));
            if (repaymentrecord.getRepayState() != 1) {// 1.未还款
                return 2;
            }
            // 还款
            boolean bool = baseLoansignService.dayOnTimeRepay(repaymentrecord,
                    repayTime);
            return bool ? 1 : 3;
    }

    /**
     * <p>
     * Title: outPutLoanSignOfDayExcel
     * </p>
     * <p>
     * Description: 导出天标借款列表
     * </p>
     * 
     * @param response
     *            响应
     */
    @RequestMapping("/outPutExcel")
    public void outPutLoanSignOfDayExcel(HttpServletResponse response) {

        // 标题
        String[] header = new String[] { "序号", "借款标号", "标题", "借款人", "最小出借单位",
                "借款金额", "天利率", "成功借出份数", "剩余份数", "借款标状态", "发布时间", "借款标类型",
                "是否放款", "放款时间", "是否显示", "推荐到首页" };
        // 行宽度
        Integer[] column = new Integer[] { 8, 10, 11, 12, 12, 10, 10, 12, 10,
                10, 10, 10, 10, 10, 10, 10 };
        // 获取数据源
        List list = loanSignOfDayService.outPutLoanSignDayList();

        List<Map<String, String>> content = new ArrayList<Map<String, String>>();
        Map<String, String> map = null;

        for (Object obj : list) {
            Object[] str = (Object[]) obj;
            map = new HashMap<String, String>();

            map.put("序号", str[0] + "");
            map.put("借款标号", str[1] + "");
            map.put("标题", str[2] + "");
            map.put("借款人", str[3] + "");
            map.put("最小出借单位", str[4] + "");
            map.put("借款金额", Arith.round(new BigDecimal(str[5].toString()), 2)
                    + "元");
            map.put("发布时间", str[6] != null ? str[6] + "" : "");
            map.put("天利率", Arith.round(new BigDecimal(str[7].toString()), 2)
                    + "%");
            map.put("成功借出份数", Integer.parseInt(str[8].toString()) + "");
            map.put("剩余份数", Double.valueOf(str[9].toString()) > 0 ? str[9] + ""
                    : "满标");
            map.put("借款标类型", str[10] + "");
            map.put("是否放款", str[11] + "");
            map.put("放款时间", null != str[12] ? str[12].toString() : "");
            map.put("借款标状态", str[13] + "");
            map.put("是否显示", str[14] + "");
            map.put("推荐到首页", str[15] + "");
            content.add(map);
        }
        // 下载excel
        rechargeModel.downloadExcel("天标", column, header, content, response);

    }

    /**
     * <p>
     * Title: dayTimeRepayCompute
     * </p>
     * <p>
     * Description: 天标实际还款金额计算
     * </p>
     * 
     * @param repaymentrecordId
     *            还款编号
     * @param repayTime
     *            还款时间
     * @return 更新结果
     */
    @ResponseBody
    @RequestMapping("/daytime_repay_compute")
    public Object[] dayTimeRepayCompute(String repaymentrecordId,
            String repayTime) {
        Object[] obj = new Object[6];
            Repaymentrecord repaymentrecord = dao.get(Repaymentrecord.class,
                    Long.valueOf(repaymentrecordId));
            Loansign loanSign = repaymentrecord.getLoansign();

            // 实际使用天数
            int reallyDay = loanSignFund.reallyUseDay(
                    loanSignQuery.getcreditTime(loanSign.getId()), repayTime);
            obj[0] = "0";
            if (reallyDay < 0) {
                obj[1] = "还款时间不能小于放款时间";
                return obj;
            }
            obj[0] = "1";
            // 实际还款金额
          /*  double reallyInterest = loanSignFund.instalmentInterest(
                    new BigDecimal(loanSign.getIssueLoan()),
                    loanSign.getRate(), reallyDay, 2).doubleValue();
            obj[1] = Arith.round(
                    Arith.add(repaymentrecord.getMoney(), reallyInterest), 2);*/
            return obj;
    }

}
