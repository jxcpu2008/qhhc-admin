package com.hc9.controller;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.hc9.common.util.Arith;
import com.hc9.dao.HibernateSupportTemplate;
import com.hc9.dao.entity.Loansignbasics;
import com.hc9.service.BaseLoansignService;
import com.hc9.service.LoanSignQuery;

/**
* <p>Title:LoanSignOfDayService</p>
* <p>Description: 天标服务层</p>
* <p>Company: 前海红筹</p>
* @author frank
* <p>date 2014年2月14日</p>
*/
@Service
public class LoanSignOfDayService {
    /** dao */
    @Resource
    private HibernateSupportTemplate dao;

    /** baseLoansignService */
    @Resource
    private BaseLoansignService baseLoansignService;

    /** loanSignQuery */
    @Resource
    private LoanSignQuery loanSignQuery;
    /**
     * 保存查询天标的条件
     */
    private String loansignDayConditions;

    /**
     * 天标条数
     * 
     * @param loansignbasics
     *            借款标基本信息
     * @return 条数
     */
    public int getLoansignDayCount(Loansignbasics loansignbasics,String loanType) {
        StringBuffer sb = new StringBuffer(
                "SELECT COUNT(1) FROM loansign INNER JOIN loansignbasics ON loansign.id = loansignbasics.loansign_id ");
        sb.append(" INNER JOIN userbasicsinfo ON loansign.userbasicinfo_id = userbasicsinfo.id WHERE loansign.loanType = 2");
      //  sb.append(baseLoansignService.getQueryConditions(loansignbasics,loanType));
        return loanSignQuery.queryCount(sb.toString());
    }

    /**
    * <p>Title: loanSignDayPage</p>
    * <p>Description: 天标列表</p>
    * @param start start
    * @param limit limit
    * @param loansignbasics  查询条件对象
    * @return List
    */
    public List loanSignDayPage(int start, int limit,
            Loansignbasics loansignbasics,String loanType) {
        List list = new ArrayList();
        StringBuffer sb = new StringBuffer(
                "SELECT loansign.id, loansignbasics.loanNumber, loansignbasics.loanTitle, userbasicsinfo. NAME,");
        sb.append(" loansign.loanUnit, loansign.issueLoan, loansign.publishTime, loansign.rate * 100,");
        sb.append(" round( IFNULL(( SELECT SUM(tenderMoney) / loansign.loanUnit FROM loanrecord WHERE isSucceed = 1 AND loanrecord.loanSign_id = loansign.id ), 0 )), ");
        sb.append(" round(( SELECT ( loansign.issueLoan - IFNULL(SUM(tenderMoney), 0)) / loansign.loanUnit FROM loanrecord WHERE isSucceed = 1 AND loanrecord.loanSign_id = loansign.id )),");
        sb.append(" CASE WHEN loansign.loanstate = 3 OR loansign.loanstate = 4 THEN '已放款' ELSE '' END, loansignbasics.creditTime, ");
        sb.append(" CASE WHEN loansign.loanstate = 1 THEN '未发布' WHEN loansign.loanstate = 2 THEN '进行中' WHEN loansign.loanstate = 3 THEN '回款中' ELSE '已完成' END,");
        sb.append(" CASE WHEN loansign.isShow = 1 THEN '显示' ELSE '不显示' END, CASE WHEN loansign.isRecommand = 1 THEN '推荐' ELSE '不推荐' END ");
        sb.append(" FROM loansign INNER JOIN loansignbasics ON loansign.id = loansignbasics.id ");
        sb.append(" INNER JOIN userbasicsinfo ON loansign.userbasicinfo_id = userbasicsinfo.id WHERE loansign.loanType = 2");
   //     sb.append(baseLoansignService.getQueryConditions(loansignbasics,loanType));
        sb.append("   ORDER BY loansign.loanstate asc ,loansign.id DESC ");
        loansignDayConditions = sb.toString();

        sb.append(" LIMIT ").append(start).append(" , ").append(limit);
        list = dao.findBySql(sb.toString());
        return list;
    }

    /**
     * 天标列表转为JSONArray
     * 
     * @param list 集合
     * @return JSONArray对象
     */
    public JSONArray queryJSONByList(List list) {
        JSONObject json = null;
        JSONArray jsonlist = new JSONArray();

        // 给每条数据添加标题
        for (Object obj : list) {
            json = new JSONObject();
            Object[] str = (Object[]) obj;
            json.element("id", str[0]);
            json.element("loanNumber", str[1]);
            json.element("loanTitle", str[2]);
            json.element("name", str[3]);
            json.element("loanUnit", str[4]);
            json.element("issueLoan",
                    Arith.round(new BigDecimal(str[5].toString()), 2) + "元");
            json.element("publishTime", str[6]);
            json.element("rate",
                    Arith.round(new BigDecimal(str[7].toString()), 2) + "%");
            json.element("successfulLending", str[8]);
            json.element("remainingCopies",
                    Double.valueOf(str[9].toString()) > 0 ? str[9] : "满标");
//            json.element("loanCategory", str[10]);
            json.element("iscredit", str[10]);
            json.element("creditTime", str[11]);
            json.element("loanstate", str[12]);
            json.element("isShow", str[13]);
            json.element("isRecommand", str[14]);
            jsonlist.add(json);
        }
        return jsonlist;
    }

    /***
     * 要导出的天列表数据
     * 
     * @return List
     */
    public List outPutLoanSignDayList() {
        return dao.findBySql(loansignDayConditions.toString());
    }
}
