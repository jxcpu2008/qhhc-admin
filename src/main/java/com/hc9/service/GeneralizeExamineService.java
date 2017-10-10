package com.hc9.service;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.hc9.common.constant.Constant;
import com.hc9.common.redis.HcPeachActivitiCache;
import com.hc9.common.util.DateUtils;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.Adminuser;
import com.hc9.dao.entity.CardImgAudit;
import com.hc9.dao.entity.Generalize;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;

/***
 * 客户关联审核列表
 * @author lkl
 *
 */
@Service
public class GeneralizeExamineService {
	
	@Resource
	private HibernateSupport dao;  
	
	@Resource
	private UserbasicsinfoService userbasicsinfoService;
	
	@Resource
	private CardImgAuditQueryService cardImgAuditQueryService;
	
	@Resource
	private ActivityService activityService;
	
	@Resource
	private HccoindetailService hccoindetailService;
	
	
	/***
	 *  列表查询
	 * @param page
	 * @param loansign
	 * @return
	 */
	public  List generalizeExaminePage(PageModel page, Generalize generalize) {
		List datalist = new ArrayList();
		StringBuffer countsql = new StringBuffer("SELECT count(1)  from generalize g,userbasicsinfo ub , userrelationinfo ul where   g.genuid=ub.id and g.genuid=ul.id  ");
		StringBuffer sqlbuffer = new StringBuffer("select g.id,ub.`name`,ul.phone ,(select `name` from userbasicsinfo uu where g.uid=uu.id) as uuname, (select phone from userrelationinfo uul where g.uid=uul.id) as uphone,g.adddate,g.state");
	    sqlbuffer.append(" , (select a.realname from adminuser a where a.id=g.auditId) as realname,g.auditTime,g.remark from generalize g, userbasicsinfo ub , userrelationinfo ul where    g.genuid=ub.id and g.genuid=ul.id  ");
		if (generalize.getUanme()!= null && generalize.getUanme() != "") {
			String name = "";
			try {
				name = java.net.URLDecoder.decode(generalize.getUanme(),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			sqlbuffer.append(" and (ub.`name`  like '%").append(name).append("%'  or ul.phone like '%").append(generalize.getUanme()).append("%')");
			countsql.append(" and (ub.`name`  like '%").append(name).append("%'  or ul.phone like '%").append(generalize.getUanme()).append("%')");
		}
		
		if (generalize.getState()!=null&&generalize.getState()!=9) {
			if(generalize.getState()==1){
				sqlbuffer.append(" AND g.state in (1,2) ");
				countsql.append(" AND g.state in  (1,2) ");
			}else if(generalize.getState()==2){
				sqlbuffer.append(" AND g.state = 3 ");
				countsql.append(" AND g.state =  3 ");
			}else{
				sqlbuffer.append(" AND g.state = ").append(generalize.getState());
				countsql.append(" AND g.state = ").append(generalize.getState());
			}
		 }
	    sqlbuffer.append(connectionSql(generalize.getAdddate(),generalize.getAuditTime()));
		countsql.append(connectionSql(generalize.getAdddate(),generalize.getAuditTime()));
	    sqlbuffer.append("  ORDER BY g.adddate desc");
		datalist = dao.pageListBySql(page, countsql.toString(),sqlbuffer.toString(), null);
	    return datalist;
	}
	
    public String connectionSql(String beginDate, String endDate) {
        String sql = "";
        if (beginDate != null && !"".equals(beginDate.trim())) {
            sql = sql + " AND DATE_FORMAT(g.adddate, '%Y-%m-%d')>=DATE_FORMAT('" + beginDate
                    + "', '%Y-%m-%d') ";
        }
        if (endDate != null && !"".equals(endDate.trim())) {
            sql = sql + " AND DATE_FORMAT(g.adddate, '%Y-%m-%d')<=DATE_FORMAT('" + endDate
                    + "', '%Y-%m-%d') ";
        }
        return sql;
    }
    
    
   
    /***
     * 导出excel
     * @param generalize
     * @return
     */
	public List queryGeneralizeExamineList(Generalize generalize) {
		List list = new ArrayList();
		StringBuffer sqlbuffer = new StringBuffer("select g.id,ub.`name`,ul.phone ,(select `name` from userbasicsinfo uu where g.uid=uu.id) as uuname, (select phone from userrelationinfo uul where g.uid=uul.id) as uphone,g.adddate,g.state");
	    sqlbuffer.append(", (select a.realname from adminuser a where a.id=g.auditId) as realname,g.auditTime,g.remark from generalize g, userbasicsinfo ub ,userrelationinfo ul where  g.genuid=ub.id and  g.genuid=ul.id  ");
		if (generalize.getUanme()!= null && generalize.getUanme() != "") {
			String name = "";
			try {
				name = java.net.URLDecoder.decode(generalize.getUanme(),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			sqlbuffer.append(" and (ub.`name`  like '%").append(name).append("%'  or ul.phone like '%").append(generalize.getUanme()).append("%')");
		}
		
		if (generalize.getState()!=null&&generalize.getState()!=9) {
			if(generalize.getState()==1){
				sqlbuffer.append(" AND g.state in (1,2) ");
			}else if(generalize.getState()==2){
				sqlbuffer.append(" AND g.state = 3 ");
			}else{
				sqlbuffer.append(" AND g.state = ").append(generalize.getState());
			}
		 }
	    sqlbuffer.append(connectionSql(generalize.getAdddate(),generalize.getAuditTime()));
	    sqlbuffer.append("  ORDER BY g.adddate desc");
		list = dao.findBySql(sqlbuffer.toString());
		return list;
	}
	
	
	/***
	 * 根据id获取Generalize
	 * @param id
	 * @return
	 */
	public Generalize getGeneralizeById(String id) {
		try {
			return dao.get(Generalize.class, Long.valueOf(id));
		} catch (DataAccessException e) {
			return null;
		}
	}
	
	/***
	 * 更新状态审核通过
	 * @param ids
	 * @param state
	 * @param remark
	 * @param request
	 * @return
	 */
	public String updateGeneralizeExamineState(String ids, Integer examine, Integer state,String remark,final HttpServletRequest request) {
		Adminuser loginuser = (Adminuser) request.getSession().getAttribute(Constant.ADMINLOGIN_SUCCESS);
		if (StringUtil.isNotBlank(ids)) {
			      try{
			    	  Generalize generalize=getGeneralizeById(ids);
						if(examine==1){ //审核通过
							String sql="update generalize  set auditId="+loginuser.getId()+",auditTime='"+DateUtils.format("yyyy-MM-dd HH:mm:ss")+"',state=2 where id="+generalize.getId();
							dao.executeSql(sql);
							Userbasicsinfo user=userbasicsinfoService.queryUserById(generalize.getGenuid());
							if(generalize.getByUser().getIsAuthIps()!=null){
									if(generalize.getByUser().getIsAuthIps()==1){
											if(user.getUserType()==1||user.getUserType()==3){
												if(user.getIsAuthIps()==1){
													user.setUserType(6);
													dao.update(user);
											    }
												if(user.getUserType() == 2){
													hccoindetailService.saveHccoindetailNumber(user);
												}
										}
									}
								}
								DateFormat fmt =new SimpleDateFormat("yyyy-MM-dd");   
								Date adddate=fmt.parse(generalize.getAdddate());
								if(HcPeachActivitiCache.validCurrentDate(adddate)==0){  //判断是否在活动期间内
									if(generalize.getByUser().getIsAuthIps()!=null){
										if(generalize.getByUser().getIsAuthIps()==1){
											CardImgAudit cardImgAudit=cardImgAuditQueryService.getCardImgAudit(generalize.getByUser().getId());
											if(cardImgAudit!=null){
												if(cardImgAudit.getCardImgState()==1){ //判断是否身份证审核通过
													   if(user.getIsAuthIps()==1){
															cardImgAuditQueryService.saveRedenvelopedetail(user);
													   }
												 }
											}
										}
									}
						            Date date = fmt.parse(generalize.getByUser().getCreateTime());
									if(HcPeachActivitiCache.validCurrentDate(date)==0){
										activityService.lotteryPeach(user, generalize.getByUser());
										HcPeachActivitiCache.increasePermanentLotteryChance(user.getId(), 1);
									}
							 }
						}else{
							generalize.setAuditId(loginuser.getId());
							generalize.setAuditTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
							generalize.setState(Constant.STATUES_THERE);
							String strRemark=remark;
							if(state==1){
								strRemark="提交信息有误";
							}else if(state==2){
								strRemark="用户拒绝被关联";
							}else if(state==3){
								strRemark="用户对关联人无印象";
							}
							generalize.setRemark(strRemark);
							dao.update(generalize);
						}
			      }catch(Exception e){
			    	  return "2";
			      }
		}
		return "1";
	}

}
