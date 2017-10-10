package com.hc9.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.hc9.common.util.StringUtil;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;

@Service
public class HccoindetailQueryService {
	
	@Resource
	private HibernateSupport dao;
	
	@Resource
	private LoanSignQuery loanSignQuery;
	
	/***
	 * 查询红筹币统计sql
	 * @param page
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public List getUserHccoindetailList(PageModel page,String beginDate, String endDate,String name){
		 List datalist = new ArrayList();
		 StringBuffer countsql = new StringBuffer("SELECT count(1) from userbasicsinfo u where u.user_type=2  and u.department!=15 ");
			StringBuffer  sqlbuffer=new StringBuffer("select u.id,u.`name`,(select count(g.id) from generalize g where g.genuid=u.id ");
			if (StringUtil.isNotBlank(beginDate)) {
				sqlbuffer.append(" and g.adddate >= '")
						.append(beginDate).append(" 00:00:00'");
			}
			if (StringUtil.isNotBlank(endDate)) {
				sqlbuffer.append(" and g.adddate <= '")
						.append(endDate).append(" 23:59:59'");
			}
		 sqlbuffer.append("  ) as countgen ,(select COUNT(us.id) from userbasicsinfo us where us.id in (select g.uid from generalize g where g.genuid=u.id) and us.isAuthIps=1 ");
			if (StringUtil.isNotBlank(beginDate)) {
				sqlbuffer.append(" and us.authIpsTime >= '")
						.append(beginDate).append(" 00:00:00'");
			}
			if (StringUtil.isNotBlank(endDate)) {
				sqlbuffer.append(" and us.authIpsTime <= '")
						.append(endDate).append(" 23:59:59'");
			}
		 sqlbuffer.append( " ) as isAuthIpsCount, IFNULL((select SUM(l.tenderMoney) from loanrecord l where l.userbasicinfo_id IN (select g.uid from generalize g where g.genuid=u.id) and l.isSucceed=1 ");
			if (StringUtil.isNotBlank(beginDate)) {
				sqlbuffer.append(" and l.tenderTime >= '")
						.append(beginDate).append(" 00:00:00'");
			}
			if (StringUtil.isNotBlank(endDate)) {
				sqlbuffer.append(" and l.tenderTime <= '")
						.append(endDate).append(" 23:59:59'");
			}
		 sqlbuffer.append( " ),0) as sumtendermoney,IFNULL((select SUM(h.number) from hccoindetail h where h.userId =u.id and h.sourceType=3 ");
				if (StringUtil.isNotBlank(beginDate)) {
					sqlbuffer.append(" and h.receiveTime >= '")
							.append(beginDate).append(" 00:00:00'");
				}
				if (StringUtil.isNotBlank(endDate)) {
					sqlbuffer.append(" and h.receiveTime <= '")
							.append(endDate).append(" 23:59:59'");
				}
		 sqlbuffer.append( " ),0) as sumnumber,IFNULL((select SUM(h.number) from hccoindetail h where h.sourceType=4 and h.userId=u.id ");
			 	if (StringUtil.isNotBlank(beginDate)) {
					sqlbuffer.append(" and h.receiveTime >= '")
							.append(beginDate).append(" 00:00:00'");
				}
				if (StringUtil.isNotBlank(endDate)) {
					sqlbuffer.append(" and h.receiveTime <= '")
							.append(endDate).append(" 23:59:59'");
				}
		 sqlbuffer.append( " ),0) as sumwkt,IFNULL((select SUM(h.number) from hccoindetail h where h.sourceType=5 and h.userId=u.id ");
				 if (StringUtil.isNotBlank(beginDate)) {
						sqlbuffer.append(" and h.receiveTime >= '")
								.append(beginDate).append(" 00:00:00'");
					}
					if (StringUtil.isNotBlank(endDate)) {
						sqlbuffer.append(" and h.receiveTime <= '")
								.append(endDate).append(" 23:59:59'");
					}
		 sqlbuffer.append( " ),0) as sumqqj,IFNULL((select SUM(h.number) from hccoindetail h where h.sourceType=6 and h.userId=u.id ");
			    if (StringUtil.isNotBlank(beginDate)) {
					sqlbuffer.append(" and h.receiveTime >= '")
							.append(beginDate).append(" 00:00:00'");
				}
				if (StringUtil.isNotBlank(endDate)) {
					sqlbuffer.append(" and h.receiveTime <= '")
							.append(endDate).append(" 23:59:59'");
				}
		 sqlbuffer.append( " ),0) as sumtsg ,IFNULL((select SUM(h.number) from hccoindetail h where h.sourceType=7 and h.userId=u.id ");
		          if (StringUtil.isNotBlank(beginDate)) {
						sqlbuffer.append(" and h.receiveTime >= '")
								.append(beginDate).append(" 00:00:00'");
					}
					if (StringUtil.isNotBlank(endDate)) {
						sqlbuffer.append(" and h.receiveTime <= '")
								.append(endDate).append(" 23:59:59'");
					}
		 sqlbuffer.append( " ),0) as sumhdjl,IFNULL((select SUM(h.number) from hccoindetail h where h.sourceType=8 and h.userId=u.id ");
		         if (StringUtil.isNotBlank(beginDate)) {
						sqlbuffer.append(" and h.receiveTime >= '")
								.append(beginDate).append(" 00:00:00'");
					}
					if (StringUtil.isNotBlank(endDate)) {
						sqlbuffer.append(" and h.receiveTime <= '")
								.append(endDate).append(" 23:59:59'");
					}
		 sqlbuffer.append( " ),0) as sumpmh ,IFNULL((select SUM(h.number) from hccoindetail h where h.sourceType=9  and h.userId=u.id ");
		         if (StringUtil.isNotBlank(beginDate)) {
						sqlbuffer.append(" and h.receiveTime >= '")
								.append(beginDate).append(" 00:00:00'");
					}
					if (StringUtil.isNotBlank(endDate)) {
						sqlbuffer.append(" and h.receiveTime <= '")
								.append(endDate).append(" 23:59:59'");
					}
		 sqlbuffer.append( " ),0) as sumqt,IFNULL((select SUM(h.number) from hccoindetail h where h.userId=u.id ");
		           if (StringUtil.isNotBlank(beginDate)) {
						sqlbuffer.append(" and h.receiveTime >= '")
								.append(beginDate).append(" 00:00:00'");
					}
					if (StringUtil.isNotBlank(endDate)) {
						sqlbuffer.append(" and h.receiveTime <= '")
								.append(endDate).append(" 23:59:59'");
					}
		 sqlbuffer.append( " ),0) as sumzs from userbasicsinfo u where u.user_type=2  and u.department!=15 ");
		 if(name!=null&&name!=""){
				String loanname = "";
				try {
					loanname = java.net.URLDecoder.decode(name,"UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				sqlbuffer.append(" and u.name like '%").append(loanname).append("%'");
				countsql.append(" and  u.name like '%").append(loanname).append("%'");
		 }
		 sqlbuffer.append( "  order by sumzs desc");
		 datalist = dao.pageListBySql(page, countsql.toString(),sqlbuffer.toString(), null);
         return datalist;
	 }
	 
	 /***
	  * 统计个人红筹币
	  * @param userId
	  * @return
	  */
	public int getHccoindetailCount(int userId) {
		StringBuffer sb = new StringBuffer(
				"SELECT COUNT(1) from hccoindetail h where  h.userId=");
		return loanSignQuery.queryCount(sb.append(userId).toString());
	}
	
	/***
	 * 查询个人红筹币明细
	 * @param start
	 * @param limit
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List queryHccoindetailList(int start, int limit, int userId,int num) {
		StringBuffer sb = new StringBuffer("select u.`name`,h.receiveTime,h.remark,h.number from hccoindetail h ,userbasicsinfo  u where h.userId=u.id  and h.userId=").append(userId);
		sb.append(" order by h.receiveTime desc  ");
		if(num==1){
			sb.append(" LIMIT ").append(start).append(" , ").append(limit);
		}
		List list = dao.findBySql(sb.toString());
		return list;
	}
	
	
	public JSONArray getJSONArrayByList(List list) {
		JSONObject json = null;
		JSONArray jsonlist = new JSONArray();
		if (list != null) {
			// 给每条数据添加标题
			for (Object obj : list) {
				json = new JSONObject();
				Object[] str = (Object[]) obj;
				json.element("name", str[0]);
				json.element("receiveTime", str[1]);
				json.element("remark", str[2]);
				json.element("number", str[3]);
				jsonlist.add(json);
			}
		}
		return jsonlist;
	}

	/***
	 * 查询所有员工的红筹币信息
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public List<Object> queryUserHccoindetail(String beginDate, String endDate){
		StringBuffer  sqlbuffer=new StringBuffer("select u.id,u.`name`,(select count(g.id) from generalize g where g.genuid=u.id ");
			if (StringUtil.isNotBlank(beginDate)) {
				sqlbuffer.append(" and g.adddate >= '")
						.append(beginDate).append(" 00:00:00'");
			}
			if (StringUtil.isNotBlank(endDate)) {
				sqlbuffer.append(" and g.adddate <= '")
						.append(endDate).append(" 23:59:59'");
			}
	     sqlbuffer.append("  ) as countgen, (select COUNT(us.id) from userbasicsinfo us where us.id in (select g.uid from generalize g where g.genuid=u.id) and us.isAuthIps=1 ");
		 	if (StringUtil.isNotBlank(beginDate)) {
				sqlbuffer.append(" and us.authIpsTime >= '")
						.append(beginDate).append(" 00:00:00'");
			}
			if (StringUtil.isNotBlank(endDate)) {
				sqlbuffer.append(" and us.authIpsTime <= '")
						.append(endDate).append(" 23:59:59'");
			}
	     sqlbuffer.append( " ) as isAuthIpsCount, IFNULL((select SUM(l.tenderMoney) from loanrecord l where l.userbasicinfo_id IN (select g.uid from generalize g where g.genuid=u.id) and l.isSucceed=1 ");
			if (StringUtil.isNotBlank(beginDate)) {
				sqlbuffer.append(" and l.tenderTime >= '")
						.append(beginDate).append(" 00:00:00'");
			}
			if (StringUtil.isNotBlank(endDate)) {
				sqlbuffer.append(" and l.tenderTime <= '")
						.append(endDate).append(" 23:59:59'");
			}
		 sqlbuffer.append( " ),0) as sumtendermoney,IFNULL((select SUM(h.number) from hccoindetail h where h.userId=u.id and h.sourceType=3 ");
				if (StringUtil.isNotBlank(beginDate)) {
					sqlbuffer.append(" and h.receiveTime >= '")
							.append(beginDate).append(" 00:00:00'");
				}
				if (StringUtil.isNotBlank(endDate)) {
					sqlbuffer.append(" and h.receiveTime <= '")
							.append(endDate).append(" 23:59:59'");
				}
		 sqlbuffer.append( " ),0) as sumnumber,IFNULL((select SUM(h.number) from hccoindetail h where h.sourceType=4 and h.userId=u.id ");
			 	if (StringUtil.isNotBlank(beginDate)) {
					sqlbuffer.append(" and h.receiveTime >= '")
							.append(beginDate).append(" 00:00:00'");
				}
				if (StringUtil.isNotBlank(endDate)) {
					sqlbuffer.append(" and h.receiveTime <= '")
							.append(endDate).append(" 23:59:59'");
				}
		 sqlbuffer.append( " ),0) as sumwkt,IFNULL((select SUM(h.number) from hccoindetail h where h.sourceType=5 and h.userId=u.id ");
				 if (StringUtil.isNotBlank(beginDate)) {
						sqlbuffer.append(" and h.receiveTime >= '")
								.append(beginDate).append(" 00:00:00'");
					}
					if (StringUtil.isNotBlank(endDate)) {
						sqlbuffer.append(" and h.receiveTime <= '")
								.append(endDate).append(" 23:59:59'");
					}
		 sqlbuffer.append( " ),0) as sumqqj,IFNULL((select SUM(h.number) from hccoindetail h where h.sourceType=6 and h.userId=u.id ");
			    if (StringUtil.isNotBlank(beginDate)) {
					sqlbuffer.append(" and h.receiveTime >= '")
							.append(beginDate).append(" 00:00:00'");
				}
				if (StringUtil.isNotBlank(endDate)) {
					sqlbuffer.append(" and h.receiveTime <= '")
							.append(endDate).append(" 23:59:59'");
				}
		 sqlbuffer.append( " ),0) as sumtsg ,IFNULL((select SUM(h.number) from hccoindetail h where h.sourceType=7 and h.userId=u.id ");
		          if (StringUtil.isNotBlank(beginDate)) {
						sqlbuffer.append(" and h.receiveTime >= '")
								.append(beginDate).append(" 00:00:00'");
					}
					if (StringUtil.isNotBlank(endDate)) {
						sqlbuffer.append(" and h.receiveTime <= '")
								.append(endDate).append(" 23:59:59'");
					}
		 sqlbuffer.append( " ),0) as sumhdjl,IFNULL((select SUM(h.number) from hccoindetail h where h.sourceType=8 and h.userId=u.id ");
		         if (StringUtil.isNotBlank(beginDate)) {
						sqlbuffer.append(" and h.receiveTime >= '")
								.append(beginDate).append(" 00:00:00'");
					}
					if (StringUtil.isNotBlank(endDate)) {
						sqlbuffer.append(" and h.receiveTime <= '")
								.append(endDate).append(" 23:59:59'");
					}
		 sqlbuffer.append( " ),0) as sumpmh ,IFNULL((select SUM(h.number) from hccoindetail h where h.sourceType=9  and h.userId=u.id ");
		         if (StringUtil.isNotBlank(beginDate)) {
						sqlbuffer.append(" and h.receiveTime >= '")
								.append(beginDate).append(" 00:00:00'");
					}
					if (StringUtil.isNotBlank(endDate)) {
						sqlbuffer.append(" and h.receiveTime <= '")
								.append(endDate).append(" 23:59:59'");
					}
		 sqlbuffer.append( " ),0) as sumqt,IFNULL((select SUM(h.number) from hccoindetail h where h.userId=u.id ");
		           if (StringUtil.isNotBlank(beginDate)) {
						sqlbuffer.append(" and h.receiveTime >= '")
								.append(beginDate).append(" 00:00:00'");
					}
					if (StringUtil.isNotBlank(endDate)) {
						sqlbuffer.append(" and h.receiveTime <= '")
								.append(endDate).append(" 23:59:59'");
					}
		 sqlbuffer.append( " ),0) as sumzs from userbasicsinfo u where u.user_type=2 and u.department!=15 order by sumzs desc");
		List list = dao.findBySql(sqlbuffer.toString());
		return list;
	}
	 
}
