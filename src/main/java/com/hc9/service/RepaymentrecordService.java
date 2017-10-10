package com.hc9.service;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.hc9.common.constant.Constant;
import com.hc9.common.util.Arith;
import com.hc9.commons.log.LOG;
import com.hc9.dao.HibernateSupportTemplate;
import com.hc9.dao.entity.Repaymentrecord;
import com.hc9.dao.entity.Repaymentrecordparticulars;
import com.hc9.form.RemindRepaymentListForm;
import com.hc9.model.PageModel;
import com.hc9.service.sms.ym.BaseSmsService;

/**
 * 还款记录service
 * 
 * @author Administrator
 * 
 */
@Service
public class RepaymentrecordService {
	/** dao */
	@Resource
	private HibernateSupportTemplate dao;

	/** loanSignQuery */
	@Resource
	private LoanSignQuery loanSignQuery;

	@Resource
	BaseSmsService baseSmsService;

	/**
	 * <p>
	 * Title: getrepaymentRecordCount
	 * </p>
	 * <p>
	 * Description: 借款标为id的还款记录的条数
	 * </p>
	 * 
	 * @param loansignId
	 *            借款标号
	 * @return 结果
	 */
	public int getrepaymentRecordCount(int loansignId) {
		StringBuffer sb = new StringBuffer(
				"SELECT COUNT(1) from repaymentrecord where loanSign_id=");
		return loanSignQuery.queryCount(sb.append(loansignId).toString());
	}

	/***
	 * 根据repaymentrecord的ID查询repaymentrecordparticulars
	 * 
	 * @param loanrecordId
	 * @return
	 */
	public int getRepaymentrecordparticulars(int repaymentrecordId) {
		StringBuffer sb = new StringBuffer(
				"SELECT COUNT(1) from repaymentrecordparticulars where repaymentrecordId=");
		return loanSignQuery
				.queryCount(sb.append(repaymentrecordId).toString());
	}

	/**
	 * 更改还款记录对象
	 * 
	 * @param repaymentrecord
	 *            还款记录对象
	 */
	public void update(Repaymentrecord repaymentrecord) {
		dao.update(repaymentrecord);
	}

	/**
	 * 通过借款标标号查询到该借款标的还款记录（适用于所有的借款标）
	 * 
	 * @param loanSignId
	 *            借款标id
	 * @param start
	 *            start
	 * @param limit
	 *            limit
	 * @return 集合
	 */
	public List<Repaymentrecord> queryRepaymentrecordList(int start, int limit,
			int loanSignId) {
		List list = new ArrayList();
		StringBuffer sb = new StringBuffer(
				"SELECT id, periods, preRepayDate, (money + preRepayMoney),  (middleMoney+middlePreRepayMoney), (afterMoney+afterPreRepayMoney),repayState ,repayTime, ");
		sb.append("  IFNULL((realMoney), 0.00), IFNULL(( middleRealMoney ), 0.00 ), IFNULL((afterRealMoney ), 0.00 ),IFNULL((companyPreFee ), 0.00 ),IFNULL((companyRealFee ), 0.00 ),");
		sb.append("  (money + preRepayMoney+middleMoney+middlePreRepayMoney+afterMoney+afterPreRepayMoney),(IFNULL((realMoney),00)+IFNULL(( middleRealMoney ),0.00)+IFNULL((afterRealMoney ), 0.00 ))");
		sb.append("  FROM repaymentrecord WHERE loanSign_id = ").append(
				loanSignId);
		sb.append(" LIMIT ").append(start).append(" , ").append(limit);
		list = dao.findBySql(sb.toString());
		return list;
	}

	/***
	 * 根据repaymentrecordId查询Repaymentrecordparticulars
	 * 
	 * @param start
	 * @param limit
	 * @param repaymentrecordId
	 * @return
	 */
	public List<Repaymentrecordparticulars> queryRecordparticularsList(
			int start, int limit, int repaymentrecordId) {
		List list = new ArrayList();
		StringBuffer sb = new StringBuffer(
				"SELECT id, (select userName from userbasicsinfo where id=userId) as userName,(money + preRepayMoney),  (middleMoney+middlePreRepayMoney), (afterMoney+afterPreRepayMoney),repState , ");
		sb.append(" (money+realMoney),(middleMoney+middleRealMoney),(afterMoney+afterRealMoney)  ");
		sb.append(
				"  FROM repaymentrecordparticulars WHERE repaymentrecordId = ")
				.append(repaymentrecordId);
		sb.append(" LIMIT ").append(start).append(" , ").append(limit);
		list = dao.findBySql(sb.toString());
		return list;
	}

	/**
	 * 查询表的记录
	 * 
	 * @param start
	 * @param limit
	 * @param loanSignId
	 * @return
	 */
	public List<Repaymentrecord> queryRepaymentrecord(String loanSignId) {
		List list = new ArrayList();
		StringBuffer sb = new StringBuffer(
				"SELECT id, periods, preRepayDate, (money + preRepayMoney),  (middleMoney+middlePreRepayMoney), (afterMoney+afterPreRepayMoney),repayState ,repayTime , ");
		sb.append(" (money+realMoney),(middleMoney+middleRealMoney),(afterMoney+afterRealMoney)");
		sb.append("  FROM repaymentrecord WHERE loanSign_id = ").append(
				loanSignId);
		list = dao.findBySql(sb.toString());
		return list;
	}

	/**
	 * <p>
	 * Title: getJSONArrayByList
	 * </p>
	 * <p>
	 * Description: 还款记录 转JSONArray
	 * </p>
	 * 
	 * @param list
	 *            集合
	 * @return JSONArray
	 */
	public JSONArray getJSONArrayByList(List list) {
		JSONObject json = null;
		JSONArray jsonlist = new JSONArray();
		// 给每条数据添加标题
		for (Object obj : list) {
			json = new JSONObject();
			Object[] str = (Object[]) obj;
			json.element("id", str[0]);
			json.element("periods", str[1]);
			json.element("preRepayDate", str[2]);
			json.element("money",
					Arith.round(new BigDecimal(str[3].toString()), 2) + "元");
			json.element("middleMoney",
					Arith.round(new BigDecimal(str[4].toString()), 2) + "元");
			json.element("afterMoney",
					Arith.round(new BigDecimal(str[5].toString()), 2) + "元");
			json.element("repayState", str[6]);
			json.element("repayTime", str[7]);
			json.element("companyPreFee",
					Arith.round(new BigDecimal(str[11].toString()), 2) + "元");
			json.element("sumMoney",
					Arith.round(new BigDecimal(str[13].toString()), 2) + "元");
			if ("1".equals(str[6].toString()) || "3".equals(str[6].toString())) {
				json.element("realMoney", "");
				json.element("middleRealMoney", "");
				json.element("afterRealMoney", "");
				json.element("companyRealFee", "");
				json.element("sumRealMoney", "");
			} else {
				json.element("realMoney",
						Arith.round(new BigDecimal(str[8].toString()), 2) + "元");
				json.element("middleRealMoney",
						Arith.round(new BigDecimal(str[9].toString()), 2) + "元");
				json.element("afterRealMoney",
						Arith.round(new BigDecimal(str[10].toString()), 2)
								+ "元");
				json.element("companyRealFee",
						Arith.round(new BigDecimal(str[12].toString()), 2)
								+ "元");
				json.element("sumRealMoney", Arith.round(new BigDecimal(str[14].toString()), 2)
						+ "元");
			}
			jsonlist.add(json);
		}
		return jsonlist;
	}

	public JSONArray getJSONArrayByRecordList(List list) {
		JSONObject json = null;
		JSONArray jsonlist = new JSONArray();
		// 给每条数据添加标题
		for (Object obj : list) {
			json = new JSONObject();
			Object[] str = (Object[]) obj;
			json.element("id", str[0]);
			json.element("userName", str[1]);
			json.element(
					"money",
					str[2] == "" || str[2] == null ? "" : Arith.round(
							new BigDecimal(str[2].toString()), 2) + "元");
			json.element("middleMoney", str[3] == "" || str[3] == null ? ""
					: Arith.round(new BigDecimal(str[3].toString()), 2) + "元");
			json.element("afterMoney", str[4] == "" || str[4] == null ? ""
					: Arith.round(new BigDecimal(str[4].toString()), 2) + "元");
			json.element("repState", str[5]);
			jsonlist.add(json);
		}
		return jsonlist;
	}

	/**
	 * 查询催收列表
	 * 
	 * @param page
	 * @param loansignbasics
	 * @param day
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List remindRepaymentPage(PageModel page,
			RemindRepaymentListForm remindRepayForm) {
		List list = new ArrayList();
		StringBuffer sb = new StringBuffer(
				" SELECT  repaymentrecord.id,  loansignbasics.loanNumber, loansign.`name`, ");
		sb.append(" CASE   WHEN loansign.type = 2 THEN 	'普通众持'  WHEN loansign.type = 3 THEN  '短期众持'   ELSE  	''   END,   '众持', ");
		sb.append(" IFNULL(userbasicsinfo.`name`,''),  loansign.issueLoan,");
		sb.append(" loansign.remonth,   loansign.prio_rate * 100, ");
		sb.append(" CASE   WHEN loansign.refunway = 1 THEN  	'按月付息到期还本'   WHEN loansign.refunway = 2 THEN  	'按季付息到期还本'   ELSE  	'天标还款'   END, ");
		sb.append(" loansign.publish_time,");
		sb.append(" CASE  WHEN loansign.`status` = 6 THEN  	'已放款'   ELSE  	'未放款'  END,");
		sb.append(" loansign.credit_time,   repaymentrecord.periods,    repaymentrecord.preRepayDate,");
		sb.append(" (  	repaymentrecord.money + repaymentrecord.preRepayMoney   ), ");
		sb.append(" CASE   WHEN repaymentrecord.repayState = 1 THEN  	'未还款'   WHEN repaymentrecord.repayState = 2 THEN  	'按时还款'   WHEN repaymentrecord.repayState = 3 THEN  	'逾期未还款' ");
		sb.append(" WHEN repaymentrecord.repayState = 4 THEN  	'逾期已还款'   WHEN repaymentrecord.repayState = 5 THEN  	'提前还款'   ELSE  	''   END,");
		sb.append(" repayTime,   repayState,   money + realMoney,   remindEmailCount,   remindSMSCount ");
		sb.append(" FROM loansign INNER JOIN loansignbasics ON loansign.id = loansignbasics.id ");
		sb.append(" INNER JOIN userbasicsinfo ON loansign.userbasicinfo_id = userbasicsinfo.id ");
		sb.append(" INNER JOIN repaymentrecord ON loansign.id=repaymentrecord.loanSign_id WHERE  ");
		sb.append(" (loansign.`status` = 6)");
		// StringBuffer sb = new
		// StringBuffer(" SELECT  repaymentrecord.id,  loansignbasics.loanNumber, loansign.`name`, "
		// +
		// "	CASE   WHEN loansign.type = 2 THEN 	'普通标'  WHEN loansign.type = 3 THEN  '天标'   ELSE  	''   END,   '众持', "
		// +
		// " userbasicsinfo. NAME,   loansign.issueLoan, " +
		// " loansign.remonth,   loansign.prio_rate * 100, " +
		// " CASE   WHEN loansign.refunway = 1 THEN  	'按月付息到期还本'   WHEN loansign.refunway = 2 THEN  	'按季付息到期还本'   ELSE  	'天标还款'   END,"
		// +
		// "loansign.publish_time, " +
		// " CASE  WHEN loansign.`status` = 6 THEN  	'已放款'   ELSE  	'未放款'  END, "
		// +
		// "  loansign.credit_time,   repaymentrecord.periods,    repaymentrecord.preRepayDate, "
		// +
		// "  (  	repaymentrecord.money + repaymentrecord.preRepayMoney   ), " +
		// "  CASE   WHEN repaymentrecord.repayState = 1 THEN  	'未还款'   WHEN repaymentrecord.repayState = 2 THEN  	'按时还款'   WHEN repaymentrecord.repayState = 3 THEN  	'逾期未还款' "
		// +
		// " WHEN repaymentrecord.repayState = 4 THEN  	'逾期已还款'   WHEN repaymentrecord.repayState = 5 THEN  	'提前还款'   ELSE  	''   END, "
		// +
		// " repayTime,   repayState,   money + realMoney,   remindEmailCount,   remindSMSCount "
		// +
		// " FROM  loansign " +
		// " INNER JOIN loansignbasics ON loansign.id = loansignbasics.id " +
		// " INNER JOIN userbasicsinfo ON loansign.userbasicinfo_id = userbasicsinfo.id "
		// +
		// " INNER JOIN repaymentrecord ON loansign.id = repaymentrecord.loanSign_id "
		// +
		// " WHERE (loansign.`status` = 6)" );
		String queryCondition = this.getQueryConditions(remindRepayForm);
		sb.append(queryCondition);
		sb.append("   ORDER BY  loansign.`status`  asc,loansign.id DESC ");

		StringBuffer sbl = new StringBuffer(
				"SELECT count(loansign.id) FROM loansign INNER JOIN loansignbasics ON loansign.id = loansignbasics.id ");
		sbl.append(" INNER JOIN userbasicsinfo ON loansign.userbasicinfo_id = userbasicsinfo.id  ");
		sbl.append(" INNER JOIN repaymentrecord ON loansign.id=repaymentrecord.loanSign_id WHERE  ");
		sbl.append(" (loansign.`status` = 6) ");
		sbl.append(queryCondition);
		LOG.info(sbl.toString());
		if (page != null) {// page不为空即是查询
			list = dao.pageListBySql(page, sbl.toString(), sb.toString(), null);
		} else {// page为空是下载中的查询
			list = dao.findBySql(sb.toString(), new Object[] {});
		}
		return list;
	}

	/**
	 * <p>
	 * Title: GetQueryConditions
	 * </p>
	 * <p>
	 * Description: 组装的查询条件
	 * </p>
	 * 
	 * @param loansignbasics
	 *            借款标基础信息
	 * @return sql语句
	 */
	public String getQueryConditions(RemindRepaymentListForm remindRepayForm) {
		StringBuffer sb = new StringBuffer();
		if (null != remindRepayForm.getLoanTitle()
				&& !"".equals(remindRepayForm.getLoanTitle())) {
			sb.append(" and loansign.name like '%")
					.append(remindRepayForm.getLoanTitle()).append("%'");
		}
		if (null != remindRepayForm.getLoanNumber()
				&& !"".equals(remindRepayForm.getLoanNumber())) {
			sb.append(" and loansignbasics.loanNumber like '%")
					.append(remindRepayForm.getLoanNumber()).append("%'");
		}
		if (null != remindRepayForm.getName()
				&& !"".equals(remindRepayForm.getName())) {
			sb.append(" and userbasicsinfo.name like '%")
					.append(remindRepayForm.getName()).append("%'");
		}
		if (null != remindRepayForm.getLoanType()
				&& !"".equals(remindRepayForm.getLoanType())) {
			sb.append(" and loansign.type =").append(
					remindRepayForm.getLoanType());
		}
		// if (null != remindRepayForm.getLoanProductType()
		// && !"".equals(remindRepayForm.getLoanProductType())) {
		// sb.append(" and loansign.loansignType_id =").append(
		// remindRepayForm.getLoanProductType());
		// }
		if (null != remindRepayForm.getPublishTimeStart()
				&& !"".equals(remindRepayForm.getPublishTimeStart())) {
			sb.append(" and loansign.publish_time>='")
					.append(remindRepayForm.getPublishTimeStart()).append("'");
		}
		if (null != remindRepayForm.getPublishTimeEnd()
				&& !"".equals(remindRepayForm.getPublishTimeEnd())) {
			sb.append(" and loansign.publish_time<='")
					.append(remindRepayForm.getPublishTimeEnd()).append("'");
		}
		if (null != remindRepayForm.getPreRepayDateStart()
				&& !"".equals(remindRepayForm.getPreRepayDateStart())) {
			sb.append(" and repaymentrecord.preRepayDate>='")
					.append(remindRepayForm.getPreRepayDateStart()).append("'");
		}
		if (null != remindRepayForm.getPreRepayDateEnd()
				&& !"".equals(remindRepayForm.getPreRepayDateEnd())) {
			sb.append(" and repaymentrecord.preRepayDate<='")
					.append(remindRepayForm.getPreRepayDateEnd()).append("'");
		}
		if (null != remindRepayForm.getFactRepayDateStart()
				&& !"".equals(remindRepayForm.getFactRepayDateStart())) {
			sb.append(" and repaymentrecord.repayTime>='")
					.append(remindRepayForm.getFactRepayDateStart())
					.append("'");
		}
		if (null != remindRepayForm.getFactRepayDateEnd()
				&& !"".equals(remindRepayForm.getFactRepayDateEnd())) {
			sb.append(" and repaymentrecord.repayTime<='")
					.append(remindRepayForm.getFactRepayDateEnd()).append("'");
		}
		if (null != remindRepayForm.getRepayState()
				&& !"".equals(remindRepayForm.getRepayState())) {
			sb.append(" and repaymentrecord.repayState=").append(
					remindRepayForm.getRepayState());
		}
		if (null != remindRepayForm.getRemindEmailCount()
				&& !"".equals(remindRepayForm.getRemindEmailCount())) {
			sb.append(" and repaymentrecord.remindEmailCount =").append(
					remindRepayForm.getRemindEmailCount());
		}
		if (null != remindRepayForm.getRemindSMSCount()
				&& !"".equals(remindRepayForm.getRemindSMSCount())) {
			sb.append(" and repaymentrecord.remindSMSCount=").append(
					remindRepayForm.getRemindSMSCount());
		}
		return sb.toString();
	}

	/**
	 * 获取联系方式用来催收通知
	 * 
	 * @param repaymentRecordId
	 * @return
	 */
	public Object[] getContact(Long repaymentRecordId) {
		StringBuilder sb = new StringBuilder(
				" SELECT u.name,ur.cardId,ur.phone,c.address,c.companyName,ur.email,");
		sb.append(" lb.loanNumber,ifnull(l.`name`,''),");
		sb.append(" CASE WHEN l.type = 2 THEN '普通众持' WHEN l.type = 3 THEN '短期众持' ELSE '' END,");
		sb.append(" l.issueLoan, l.remonth,");
		sb.append(" CASE WHEN l.refunway = 1 THEN '按月付息到期还本' WHEN l.refunway = 2 THEN '按季付息到期还本' ELSE '天标还款' END,");
		sb.append(" r.periods, preRepayDate,(r.money +r.middleMoney+r.afterMoney+ r.preRepayMoney+middlePreRepayMoney+afterPreRepayMoney),r.remindEmailCount,r.remindSMSCount ");
		sb.append(" FROM borrowersbase b,borrowerscompany c,userbasicsinfo u,userrelationinfo ur,repaymentrecord r,loansign l,loansignBasics lb ");
		sb.append(" WHERE b.userbasicinfo_id=u.id and b.id=c.id AND r.loanSign_id = l.id AND l.id=lb.id AND lb.id=r.loanSign_id AND l.userbasicinfo_id=u.id AND u.id=ur.id AND r.id=?");
		List<Object[]> list = (List<Object[]>) dao.findBySql(sb.toString(),
				repaymentRecordId);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 更新催款通知次数
	 * 
	 * @param fashion
	 * @param repayId
	 */
	public void updateSendTimes(int fashion, Long repayId) {
		int sms = 0;
		int email = 0;
		if (fashion == Constant.STATUES_ZERO) {
			sms++;
		} else {
			email++;
		}
		String sql = "update repaymentrecord set  remindEmailCount=remindEmailCount+?,remindSMSCount=remindSMSCount+? where id=?";
		dao.executeSql(sql, email, sms, repayId);
	}

	/**
	 * excel导出
	 * 
	 * @param title
	 *            标题
	 * @param column
	 *            列宽度（如果为null，默认高度为15）
	 * @param header
	 *            头部
	 * @param content
	 *            内容
	 * @param response
	 *            响应
	 * @return 是否成功
	 */
	public boolean downloadExcel(String title, Integer[] column,
			String[] header, List<Map<String, String>> content,
			HttpServletResponse response) {
		try {
			String filename = title
					+ new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
			filename = new String(filename.getBytes(), "ISO-8859-1");
			response.reset();
			response.setCharacterEncoding("utf-8");
			response.setContentType("application/msexcel");// 设置为下载application/x-download
			response.setHeader("Content-Disposition", "inline;filename=\""
					+ filename + ".xls\"");
			OutputStream os = response.getOutputStream();// 取得输出流
			// 提示下载
			WritableWorkbook wwb = Workbook.createWorkbook(os);
			// 创建excel工作表，指定名字和位置
			WritableSheet sheet = wwb.createSheet(title, 0);

			// 添加标题（行宽）
			for (int i = 0; i < header.length; i++) {
				sheet.addCell(new Label(i, 0, header[i]));
				// 设置excel列宽
				if (column != null) {
					sheet.setColumnView(i, column[i]);
				} else {// 如果没有设置默认为宽度为50
					sheet.setColumnView(i, 15);
				}
			}

			// 添加内容
			for (int i = 0; i < content.size(); i++) {
				for (int j = 0; j < content.get(i).size(); j++) {
					sheet.addCell(new Label(j, i + 1, content.get(i).get(
							header[j])
							+ ""));
				}
			}

			// 写入工作表
			wwb.write();
			wwb.close();
			os.close();
		} catch (IOException | WriteException e) {
			LOG.error(e.getMessage());
		}
		return true;
	}

}
