package com.hc9.service;


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

/**
 * 秒标service
 * 
 * @author frank
 * 
 */
@Service
public class LoanSignOfSecService {

    /** dao */
    @Resource
    private HibernateSupportTemplate dao;

    /** baseLoansignService*/
    @Resource
    private BaseLoansignService baseLoansignService;

    /** loanSignQuery*/
    @Resource
    private LoanSignQuery loanSignQuery;
    /**
     * 保存查询秒标的条件
     */
    private String loansignSecConditions;

    /**
     * 秒标条数
     * @param loansignbasics 借款标基础信息
     * @return 条数
     */
    public int getLoansignSecCount(Loansignbasics loansignbasics,String loanType) {
        StringBuffer sb = new StringBuffer(
                "SELECT COUNT(1) FROM loansign INNER JOIN loansignbasics ON loansign.id = loansignbasics.loansign_id WHERE loansign.loanType = 3 ");
     //   sb.append(baseLoansignService.getQueryConditions(loansignbasics,loanType));
        return loanSignQuery.queryCount(sb.toString());
    }

    /**
    * <p>Title: loanSignSecPage</p>
    * <p>Description: 秒标列表</p>
    * @param start start
    * @param limit limit
    * @param loansignbasics 借款标基础信息
    * @return list
    */
    public List loanSignSecPage(int start, int limit,
            Loansignbasics loansignbasics,String loanType) {
        List list = new ArrayList();
        StringBuffer sb = new StringBuffer(
                "SELECT loansign.id,loansignbasics.loanNumber,loansignbasics.loanTitle,loansign.loanUnit,loansign.issueLoan,loansign.publishTime,loansign.rate*100,");
        sb.append(" round( IFNULL(( SELECT SUM(tenderMoney) / loansign.loanUnit FROM loanrecord WHERE isSucceed = 1 AND loanrecord.loanSign_id = loansign.id ), 0 )), ");
        sb.append(" round(( SELECT ( loansign.issueLoan - IFNULL(SUM(tenderMoney), 0)) / loansign.loanUnit FROM loanrecord WHERE isSucceed = 1 AND loanrecord.loanSign_id = loansign.id )), ");
        sb.append(" CASE WHEN loansign.loanstate = 1 THEN '未发布' WHEN loansign.loanstate = 2 THEN '进行中' WHEN loansign.loanstate = 3 THEN '回款中' ELSE '已完成' END, ");
        sb.append(" CASE WHEN loansign.loanstate = 3 OR loansign.loanstate = 4 THEN '已放款' ELSE '' END, ");
        sb.append(" CASE WHEN loansign.isShow = 1 THEN '显示' ELSE '不显示' END, ");
        sb.append(" CASE WHEN loansign.isRecommand = 1 THEN '推荐' ELSE '不推荐' END  ");
        sb.append(" FROM loansign INNER JOIN loansignbasics ON loansign.id = loansignbasics.id WHERE loansign.loanType = 3 ");
  //      sb.append(baseLoansignService.getQueryConditions(loansignbasics,loanType));
        sb.append("  ORDER BY loansign.loanstate asc ,loansign.id DESC ");
        loansignSecConditions = sb.toString();

        sb.append(" LIMIT ").append(start).append(" , ").append(limit);
        list = dao.findBySql(sb.toString());
        return list;
    }

    /**
     * 秒标列表转为JSONArray
     * 
     * @param list list
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
            json.element("loanUnit", str[3]);
            json.element("issueLoan",
                    Arith.round(new BigDecimal(str[4].toString()), 2) + "元");
            json.element("publishTime", str[5]);
            json.element("rate",
                    Arith.round(new BigDecimal(str[6].toString()), 2) + "%");
            json.element("successfulLending", str[7]);
            json.element("remainingCopies",
                    Double.valueOf(str[8].toString()) > 0 ? str[8] : "满标");
            json.element("loanstate", str[9]);
            json.element("iscredit", str[10]);
            json.element("isShow", str[11]);
//            json.element("loanCategory", str[12]);
            json.element("isRecommand", str[12]);
            jsonlist.add(json);
        }
        return jsonlist;
    }

    /***
     * 要导出的天列表数据
     * 
     * @return list
     */
    public List outPutLoanSignSecList() {
        return dao.findBySql(loansignSecConditions.toString());
    }
}
