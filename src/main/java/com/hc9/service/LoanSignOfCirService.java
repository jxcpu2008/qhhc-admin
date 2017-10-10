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
import com.hc9.dao.entity.Loansign;
import com.hc9.dao.entity.Loansignbasics;

/**
 * <p>
 * Title:LoanSignOfCirService
 * </p>
 * <p>
 * Description: 流转标服务层
 * </p>
 * <p>
 * Company: 前海红筹
 * </p>
 * 
 * @author frank
 *         <p>
 *         date 2014年2月14日
 *         </p>
 */
@Service
public class LoanSignOfCirService {
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
     * 保存查询流转标的条件
     */
    private String loansignCIRConditions;

    /**
     * 流转条数
     * 
     * @param loansignbasics 查询条件
     * @return 条数
     */
    public int getLoansignCirCount(Loansignbasics loansignbasics,String loanType) {
        StringBuffer sb = new StringBuffer(
                "SELECT COUNT(1) FROM loansign INNER JOIN loansignbasics ON loansign.id = loansignbasics.id ");
        sb.append(" INNER JOIN userbasicsinfo ON loansign.userbasicinfo_id = userbasicsinfo.id WHERE loansign.loanType = 6");
  //      sb.append(baseLoansignService.getQueryConditions(loansignbasics,loanType));
        return loanSignQuery.queryCount(sb.toString());
    }

    /**
     * 流转标列表
     * 
     * @param start start
     * @param limit limit
     * @param loansignbasics 借款标查询信息
     * @return list 集合
     */
    public List loanSignCirPage(int start, int limit,
            Loansignbasics loansignbasics,String loanType) {
        List list = new ArrayList();
        StringBuffer sb = new StringBuffer(
				"SELECT loansign.id, loansignbasics.loanNumber, loansignbasics.loanTitle, ");
		sb.append(" userbasicsinfo.NAME,userbasicsinfo.userName, loansign.loanUnit, loansign.issueLoan, loansign.`month`, ");
		sb.append("loansign_type.typename,");
		sb.append(" loansignbasics.mgtMoney, loansign.publishTime, loansign.rate * 100, loansignbasics.reward * 100 ,");
		sb.append(" round ( IFNULL(( SELECT SUM(tenderMoney) / loansign.loanUnit FROM loanrecord WHERE isSucceed = 1 AND loanrecord.loan_id = loansign.id ), 0 )), ");
		sb.append(" round ((SELECT ( loansign.issueLoan - IFNULL(SUM(tenderMoney), 0)) / loansign.loanUnit FROM loanrecord WHERE isSucceed = 1 AND loanrecord.loan_id = loansign.id)),");
		sb.append(" CASE WHEN loansign.refundWay = 1 THEN '按月等额本息' WHEN loansign.refundWay = 2 THEN '按月付息到期还本' ELSE '到期一次性还本息' END,");
		sb.append(" CASE WHEN loansign.loanstate = 1 THEN '未发布' WHEN loansign.loanstate = 2 THEN '进行中' WHEN loansign.loanstate = 3 THEN '回款中' ELSE '已完成' END, ");
		sb.append(" CASE WHEN loansign.loanstate = 3 OR loansign.loanstate = 4 THEN '已放款' ELSE '未放款' END, ");
		sb.append(" loansignbasics.creditTime, CASE WHEN loansign.isShow = 1 THEN '显示' ELSE '不显示' END, CASE WHEN loansign.isRecommand = 1 THEN '推荐' ELSE '不推荐' END");
		sb.append(" FROM loansign INNER JOIN loansignbasics ON loansign.id = loansignbasics.id ");
		sb.append(" INNER JOIN userbasicsinfo ON loansign.userbasicinfo_id = userbasicsinfo.id INNER JOIN loansign_type ON loansign.loansignType_id=loansign_type.id WHERE loansign.loanType = 6");
	//	sb.append(baseLoansignService.getQueryConditions(loansignbasics,loanType));
		sb.append("   ORDER BY loansign.loanstate asc ,loansign.id DESC ");
		loansignCIRConditions = sb.toString();
	    sb.append(" LIMIT ").append(start).append(" , ").append(limit);
        list = dao.findBySql(sb.toString());
        return list;
    }

    /**
    * <p>Title: getJSONArrayByList</p>
    * <p>Description: 流转标列表转为JSONArray</p>
    * @param list 集合
    * @return 集合
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
            json.element("userName", str[4]);
            json.element("loanUnit", Arith.round(new BigDecimal(str[5].toString()), 2) + "元");
            json.element("issueLoan", Arith.round(new BigDecimal(str[6].toString()), 2) + "元");
            json.element("month", str[7]+ "个月");
            json.element("loancategory",str[8]);
            json.element("mgtMoney", str[9]);
            json.element("publishTime",str[10]);
            json.element("rate", Arith.round(new BigDecimal(str[11].toString()), 2) + "%");
            json.element("reward",
                    Arith.round(new BigDecimal(str[12].toString()), 2) + "%");
            json.element("successfulLending", str[13]);
            json.element("remainingCopies", str[14]);
            json.element("refundWay", str[15]);
            json.element("loanstate", str[16]);
            json.element("iscredit", str[17]);
            json.element("creditTime", str[18]);
            json.element("isShow", str[19]);
            json.element("isRecommand", str[20]);
            jsonlist.add(json);
        }
        return jsonlist;
    }

    /***
     * 要导出的流转标列表数据
     * @return list
     */
    public List outPutList() {
        return dao.findBySql(loansignCIRConditions.toString());
    }

    /**
    * <p>Title: publish</p>
    * <p>Description: 流转标发布</p>
    * @param loansign 流转标
    * @return 是否成功
    */
 /*   public boolean publish(Loansign loansign) {
        try {
            // 1.生成还款记录
            for (int i = 0; i < loansign.getMonth(); i++) {
                Repaymentrecord record = new Repaymentrecord();
                record.setPeriods(i + 1);// 1期=1个月
                record.setLoansign(loansign);
                record.setRepayState(1);
                record.setPreRepayDate(DateUtils.add(
                        Constant.DEFAULT_DATE_FORMAT, Calendar.DATE,
                        (i + 1) * 30));// 30天一期
                record.setPreRepayMoney(0.00);// 预计还款金额为0
                record.setMoney(0.00);
                dao.save(record);
            }

            // 2.改状态
            loansign.setLoanstate(2);// 2进行中
            loansign.setPublishTime(DateUtils
                    .format(Constant.DEFAULT_TIME_FORMAT));// 发布时间
            dao.update(loansign);

            // 3.发送消息(通过用户设定的邮件反馈消息来进行发送消息)--

            return true;
        } catch (ParseException e) {
            // log.error("BaseLoansignService-借款标发布出错！");
            return false;
        }
    }*/
    
    /**
     * <p>Title: publishCir</p>
     * <p>Description: 流转标发布</p>
     * @param loansign 流转标
     * @return 是否成功
     */
     public boolean publishCir(Loansign loansign) {
       /*  try {
             // 1.生成还款记录
             for (int i = 0; i < loansign.getMonth(); i++) {
                 Repaymentrecord record = new Repaymentrecord();
                 record.setPeriods(i + 1);// 1期=1个月
                 record.setLoansign(loansign);
                 record.setRepayState(1);
                 record.setPreRepayDate(DateUtils.add(
                         Constant.DEFAULT_DATE_FORMAT, Calendar.DATE,
                         (i + 1) * 30));// 30天一期
                 record.setPreRepayMoney(0.00);// 预计还款金额为0
                 record.setMoney(0.00);
                 dao.save(record);
             }

             // 2.改状态
             loansign.setLoanstate(2);// 2进行中
             loansign.setPublishTime(DateUtils
                     .format(Constant.DEFAULT_TIME_FORMAT));// 发布时间
             dao.update(loansign);
             
            StringBuffer sb=new StringBuffer("update loansignflow set flowstate=2");
 	        sb.append(" where loanSign_id=").append(loansign.getId());
 	        dao.executeSql(sb.toString());

             return true;
         } catch (ParseException e) {
             // log.error("BaseLoansignService-借款标发布出错！");
             return false;
         }*/
    	 
    	 return false;
     }

}
