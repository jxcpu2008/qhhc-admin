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
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.model.RechargeModel;
import com.hc9.service.BaseLoansignService;
import com.hc9.service.LoanSignOfSecService;
import com.hc9.service.LoanSignQuery;

/**
 * 秒标Controller
 * 
 * @author frank
 * 
 */
@Controller
@RequestMapping("/loanSignOfSec")
@CheckLogin(value=CheckLogin.ADMIN)
public class LoanSignOfSecController {

    /** dao */
    @Resource
    private HibernateSupportTemplate dao;

    /** baseLoansignService */
    @Resource
    private BaseLoansignService baseLoansignService;

    /** loanSignOfSecService */
    @Resource
    private LoanSignOfSecService loanSignOfSecService;

    /** rechargeModel */
    @Resource
    private RechargeModel rechargeModel;
    
    @Resource
    private UserInfoQuery userInfoQuery;

    /** loanSignQuery */
    @Autowired
    private LoanSignQuery loanSignQuery;

    /**
     * 初始化
     * 
     * @return 展示的页面
     */
    @RequestMapping(value = { "index", "/" })
    public ModelAndView index() {
        return new ModelAndView("WEB-INF/views/admin/loansign/loansignSec");
    }

    /**
     * <p>
     * Title: LoanSignOfSecPage
     * </p>
     * <p>
     * Description: 列表展示
     * </p>
     * 
     * @param start
     *            start
     * @param limit
     *            limit
     * @param loansignbasics
     *            借款标基础信息
     * @param request
     *            请求
     * @return 页面url
     */
    @ResponseBody
    @RequestMapping("list")
    public JSONObject loanSignOfSecPage(
            @RequestParam(value = "start", defaultValue = "0", required = false) int start,
            @RequestParam(value = "limit", defaultValue = "10", required = false) int limit,
            Loansignbasics loansignbasics,String loanType, HttpServletRequest request) {

        JSONObject resultjson = new JSONObject();
        // 得到总条数
        int count = loanSignOfSecService.getLoansignSecCount(loansignbasics,loanType);

        // 分页数据源
        List list = loanSignOfSecService.loanSignSecPage(start, limit,
                loansignbasics,loanType);
        JSONArray jsonlist = loanSignOfSecService.queryJSONByList(list);

        // 将数据源分钟成json对象（命名必须row）
        resultjson.element("rows", jsonlist);
        // 总条数(命名必须total)
        resultjson.element("total", count);

        // resultjson数据格式如：
        return resultjson;
    }

    /**
    * <p>Title: saveORupdateSecLoansign</p>
    * <p>Description:  保存or修改秒标</p>
    * @param loanSign 借款标
    * @param loansignbasics 借款标基础信息
    * @param request 请求
    * @return 集合
    */
    @ResponseBody
    @RequestMapping(value = { "saveORupdateSecLoansign", "/" })
    public
    JSONObject saveORupdateSecLoansign(Loansign loanSign,
            Loansignbasics loansignbasics, HttpServletRequest request) {
        JSONObject json = new JSONObject();
        // 2.检查有没有设置费用比例
        Costratio costratio = loanSignQuery.queryCostratio();

        json.element("callbackType", "closeCurrent");
        json.element("statusCode", "300");
        Userbasicsinfo user=loanSign.getUserbasicsinfo();
        boolean vip=userInfoQuery.isPrivilege(user);
//        try {
     /*       // 判断本期借款金额是否正确
            if (loanSign.getIssueLoan() % loanSign.getLoanUnit() != 0) {
                json.element("message", "请输入正确的借款金额！");
                return json;
            }
            loanSign.setRate(loanSign.getRate() / 100);
            loanSign.setLoanType(3);// 秒标
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
                if (costratio == null) {
                    json.element("message", "请设置费用比例后在进行发标操作！");
                    return json;
                }

                // 3.添加
                //设定购买份额
                loanSign.setCounterparts(1);
                loanSign.setVipCounterparts(1);
                //TODO 秒标设定到期一次还本息
                loanSign.setRefundWay(3);
                
                boolean bool = baseLoansignService.save(loanSign,
                        loansignbasics, costratio,vip);
                if (bool == false) {
                    json.element("callbackType", "closeCurrent");
                    return json;
                }

            }*/
            // dwz返回json对象
            json.element("statusCode", "200");
            json.element("message", "更新成功");
            json.element("navTabId", "main310");
            json.element("callbackType", "closeCurrent");
            return json;
//        } catch (ParseException e) {
//            json.element("callbackType", "closeCurrent");
//            e.printStackTrace();
//            return json;
//        }
    }

    /**
    * <p>Title: onTimeRepay</p>
    * <p>Description:  还款(秒标就一种还款预计的金额和实际的金额一样的) </p>
    * @param loanSignId 标编号
    * @return 1 成功 2 还款出现错误 3.只能针对回款中的秒标还款
    */
    @ResponseBody
    @RequestMapping("/onTimeRepay")
    public 
    int onTimeRepay(String loanSignId) {
        JSONObject json = new JSONObject();
            // 1.判断是否该秒标是否是回款中
            Loansign loansign = dao.get(Loansign.class,
                    Long.valueOf(loanSignId));
            //TODO 此处判断要测试
            if (loansign.getStatus() != 3) {// 3.回款中的标才能还款
                return 3;
            }
            // 还款
            boolean bool = baseLoansignService.secOnTimeRepay(loansign);
            if (bool == false) {
                return 2;
            }
            return 1;
    }

    /**
     * 导出秒标借款列表
     * @param response response
     * @param request 请求
     */
    @RequestMapping("outPutExcel")
    public void outPutLoanSignOfSecExcel(HttpServletRequest request,
            HttpServletResponse response) {
        // 标题
        String[] header = new String[] { "序号", "借款标号", "标题", "最小出借单位", "借款金额",
                "发布时间", "利率", "成功借出份数", "剩余份数", "状态", "是否放款", "是否显示", "借款标类型",
                "推荐到首页" };
        // 行宽度
        Integer[] column = new Integer[] { 8, 10, 11, 12, 12, 10, 10, 12, 10,
                10, 10, 10, 10, 10 };
        // 获取数据源
        List list = loanSignOfSecService.outPutLoanSignSecList();

        List<Map<String, String>> content = new ArrayList<Map<String, String>>();
        Map<String, String> map = null;

        for (Object obj : list) {
            Object[] str = (Object[]) obj;
            map = new HashMap<String, String>();

            map.put("序号", str[0] + "");
            map.put("借款标号", str[1] + "");
            map.put("标题", str[2] + "");
            map.put("最小出借单位", str[3] + "");
            map.put("借款金额", Arith.round(new BigDecimal(str[4].toString()), 2)
                    + "元");
            map.put("发布时间", str[5] != null ? str[5] + "" : "");
            map.put("利率", Arith.round(new BigDecimal(str[6].toString()), 2)
                    + "%");
            map.put("成功借出份数", Integer.parseInt(str[7].toString()) + "");
            map.put("剩余份数", Double.valueOf(str[8].toString()) > 0 ? str[9] + ""
                    : "满标");
            map.put("状态", str[9] + "");
            map.put("是否放款", str[10] + "");
            map.put("是否显示", str[11] + "");
            map.put("借款标类型", str[12] + "");
            map.put("推荐到首页", str[13] + "");
            content.add(map);
        }
        // 下载excel
        rechargeModel.downloadExcel("秒标", column, header, content, response);
    }

    /**
     * 查询详情
     * 
     * @param id
     *            标编号
     * @param operNumber
     *            操作号 1 新增 2 编辑 3 查看详情
     *               @param request  请求
     * @return   页面
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
            request.setAttribute("loansign", loansign);
            request.setAttribute("loansignbasics", loansignbasics);
        } else {
            request.setAttribute("loansign", new Loansign());
            request.setAttribute("loansignbasics", new Loansignbasics());
        }*/
        request.setAttribute("operNumber", operNumber);
        return new ModelAndView("WEB-INF/views/admin/loansign/editloansignSec");
    }
}
