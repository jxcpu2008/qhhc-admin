package com.hc9.service;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.hc9.common.constant.Constant;
import com.hc9.common.redis.HcPeachActivitiCache;
import com.hc9.common.util.DateUtil;
import com.hc9.common.util.DateUtils;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.ActivityMonkey;
import com.hc9.dao.entity.Adminuser;
import com.hc9.dao.entity.CardImgAudit;
import com.hc9.dao.entity.RedEnvelopeDetail;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;


/***
 * 实名验证审核
 * @author lkl
 *
 */
@Service
public class CardImgAuditQueryService {
	@Resource
	private HibernateSupport dao;  
	
	@Resource
	private GeneralizeService generalizeService;
	
	@Resource
	private UserbasicsinfoService userbasicsinfoService;
	
	public  List queryCardImgAuditPage(PageModel page, CardImgAudit cardImgAudit) {
		List datalist = new ArrayList();
		StringBuffer countsql = new StringBuffer("select count(1)   from cardimgaudit c, userbasicsinfo us,userrelationinfo ur  where  c.userId=us.id   and c.userId=ur.id ");
		StringBuffer sqlbuffer = new StringBuffer("select c.id,us.`name`,ur.phone,us.isAuthIps,ur.cardId,c.cardImgTime,c.cardImgState,c.cardImgRemark,c.cardImgAuditTime");
	    sqlbuffer.append(" ,(select a.realname from adminuser a where c.cardImgAudit=a.id) as realname,(select uu.`name` from generalize g,userbasicsinfo uu where g.genuid=uu.id and g.state in (1,2) and g.uid=c.userId) as genName  from cardimgaudit c, userbasicsinfo us,userrelationinfo ur ");
	    sqlbuffer.append(" where  c.userId=us.id  and c.userId=ur.id ");
		if (cardImgAudit.getCardImgRemark()!= null && cardImgAudit.getCardImgRemark() != "") {
			String name = "";
			try {
				name = java.net.URLDecoder.decode(cardImgAudit.getCardImgRemark(),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			sqlbuffer.append(" and (us.`name`  like '%").append(name).append("%'  or ur.phone like '%").append(cardImgAudit.getCardImgRemark()).append("%')");
			countsql.append(" and (us.`name`  like '%").append(name).append("%'  or ur.phone like '%").append(cardImgAudit.getCardImgRemark()).append("%')");
		}
		
		if (cardImgAudit.getCardImgState()!=null&&cardImgAudit.getCardImgState()!=3) {
				sqlbuffer.append(" AND c.cardImgState = ").append(cardImgAudit.getCardImgState());
				countsql.append(" AND c.cardImgState = ").append(cardImgAudit.getCardImgState());
		 }
	    sqlbuffer.append(connectionSql(cardImgAudit.getCardImgTime(),cardImgAudit.getCardImgAuditTime()));
		countsql.append(connectionSql(cardImgAudit.getCardImgTime(),cardImgAudit.getCardImgAuditTime()));
	    sqlbuffer.append("  ORDER BY c.cardImgTime  desc");
		datalist = dao.pageListBySql(page, countsql.toString(),sqlbuffer.toString(), null);
	    return datalist;
	}
	
	
    public String connectionSql(String beginDate, String endDate) {
        String sql = "";
        if (beginDate != null && !"".equals(beginDate.trim())) {
            sql = sql + " AND DATE_FORMAT(c.cardImgTime, '%Y-%m-%d')>=DATE_FORMAT('" + beginDate
                    + "', '%Y-%m-%d') ";
        }
        if (endDate != null && !"".equals(endDate.trim())) {
            sql = sql + " AND DATE_FORMAT(c.cardImgTime, '%Y-%m-%d')<=DATE_FORMAT('" + endDate
                    + "', '%Y-%m-%d') ";
        }
        return sql;
    }
    
    /***
     * 导出excel
     * @param generalize
     * @return
     */
	public List queryCardImgAuditList(CardImgAudit cardImgAudit) {
		List list = new ArrayList();
		StringBuffer sqlbuffer = new StringBuffer("select c.id,us.`name`,ur.phone,us.isAuthIps,ur.cardId,c.cardImgTime,c.cardImgState,c.cardImgRemark,c.cardImgAuditTime ");
	    sqlbuffer.append(" ,(select a.realname from adminuser a where c.cardImgAudit=a.id) as realname,(select uu.`name` from generalize g,userbasicsinfo uu where g.genuid=uu.id and g.state in (1,2) and g.uid=c.userId) as genName  from cardimgaudit c, userbasicsinfo us,userrelationinfo ur  ");
	    sqlbuffer.append(" where  c.userId=us.id   and c.userId=ur.id ");
		if (cardImgAudit.getCardImgRemark()!= null && cardImgAudit.getCardImgRemark() != "") {
			String name = "";
			try {
				name = java.net.URLDecoder.decode(cardImgAudit.getCardImgRemark(),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			sqlbuffer.append(" and (us.`name`  like '%").append(name).append("%'  or ur.phone like '%").append(cardImgAudit.getCardImgRemark()).append("%')");
		}
		
		if (cardImgAudit.getCardImgState()!=null&&cardImgAudit.getCardImgState()!=3) {
				sqlbuffer.append(" AND c.cardImgState = ").append(cardImgAudit.getCardImgState());
		 }
	    sqlbuffer.append(connectionSql(cardImgAudit.getCardImgTime(),cardImgAudit.getCardImgAuditTime()));
	    sqlbuffer.append("  ORDER BY c.cardImgTime  desc");
		list = dao.findBySql(sqlbuffer.toString());
		return list;
	}
    
	
	/**
	 * 查询用户信息表
	 * @param id
	 * @return
	 */
	public CardImgAudit getCardImgAudit(String id){
		String sql="select * from cardimgaudit where id=?";
		return dao.findObjectBySql(sql, CardImgAudit.class, id);
	}
	
	/***
	 * 审核
	 * @param ids
	 * @param examine
	 * @param state
	 * @param remark
	 * @param request
	 * @return
	 */
	public String updateCardImgState(String ids, Integer examine,String remark,final HttpServletRequest request) {
		Adminuser loginuser = (Adminuser) request.getSession().getAttribute(Constant.ADMINLOGIN_SUCCESS);
		if (StringUtil.isNotBlank(ids)) {
			     try{
			    	    String cardImgAuditTime=DateUtils.format("yyyy-MM-dd HH:mm:ss");
			            CardImgAudit cardImgAudit=getCardImgAudit(ids);
						if(examine==1){ //审核通过
							String sql="update  cardimgaudit  set cardImgState=1,cardImgAudit="+loginuser.getId()+", cardImgAuditTime='"+cardImgAuditTime+"' where id="+cardImgAudit.getId();
							dao.executeSql(sql);
							//dao.update(cardImgAudit);
							//在活动范围内
							if(HcPeachActivitiCache.validCurrentDate(new Date())==0){ 
								if(cardImgAudit.getUserbasicsinfo().getIsAuthIps()!=null){
									if(cardImgAudit.getUserbasicsinfo().getIsAuthIps()==1){
										Userbasicsinfo userGen = generalizeService.queryPromoterByPromotedId(cardImgAudit.getUserbasicsinfo().getId());
										//推荐好友注册总数除以9的倍数加20元红包
										if(userGen!=null){
											saveRedenvelopedetail(userGen);
										}
									}
								}
							}
						}else{
							String sql="update  cardimgaudit  set cardImgState=2,cardImgAudit="+loginuser.getId()+", cardImgAuditTime='"+cardImgAuditTime+"', cardImgRemark='"+remark+"'  where id="+cardImgAudit.getId();
							dao.executeSql(sql);
						}
			     }catch(Exception e){
			    	 return "2";
			     }
		}
		return "1";
	}
	
	/***
	 * 推荐好友注册总数除以9的倍数加20元红包
	 * @param userId
	 */
	public  void  saveRedenvelopedetail(Userbasicsinfo userGen){
		String sql="select count(*) from generalize g, userbasicsinfo u ,cardimgaudit c where g.uid=u.id and c.userId=g.uid and c.cardImgState=1 and g.genuid =?  ";
        sql = sql + " AND DATE_FORMAT(u.createTime, '%Y-%m-%d')>=DATE_FORMAT('" + HcPeachActivitiCache.getActiveBeginDate() + "', '%Y-%m-%d') ";
        sql = sql + " AND DATE_FORMAT(u.createTime, '%Y-%m-%d')<=DATE_FORMAT('" + HcPeachActivitiCache.getActiveEndDate() + "', '%Y-%m-%d')  and g.state in (1,2) ";
		Object obj=dao.findObjectBySql(sql,userGen.getId());
		Integer count=1;
		if(null != obj){
			count=Integer.valueOf(obj.toString());
			if(count%9==0){
				  saveRedenvelopedetail(userGen,1);
			}
		}
	}
	
	/***
	 * 新增红包
	 * @param user
	 */
	public void saveRedenvelopedetail(Userbasicsinfo user,Integer num){
		for (int i = 0; i <2*num; i++) {
			String time = DateUtils.format("yyyy-MM-dd");
			RedEnvelopeDetail redEnvelopeDetail = new RedEnvelopeDetail();
			redEnvelopeDetail.setUserbasicsinfo(user);
			redEnvelopeDetail.setMoney(Double.valueOf(10));
			redEnvelopeDetail.setBeginTime(time);
			redEnvelopeDetail.setReceiveTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
			redEnvelopeDetail.setEndTime(DateUtil.getSpecifiedDateAfter(time, 90));
			redEnvelopeDetail.setUseFlag(Constant.STATUES_ZERO);
			redEnvelopeDetail.setSourceType(Constant.STATUES_TWO);
			redEnvelopeDetail.setLowestUseMoney(Double.valueOf(0.00));
			dao.save(redEnvelopeDetail);
		}
	} 
	
	/***
	 *  一键更改现金状态
	 * @param request
	 * @return
	 */
	public  String updateActivityMonkey(final HttpServletRequest request){
		Adminuser loginuser = (Adminuser) request.getSession().getAttribute(Constant.ADMINLOGIN_SUCCESS);
		try{
			String sql="select * from activity_monkey where examineStatus=0 and type=10 ";
			List<ActivityMonkey> listActivityMonkey=dao.findBySql(sql, ActivityMonkey.class);
			for (int i = 0; i < listActivityMonkey.size(); i++) {
				ActivityMonkey activityMonkey=listActivityMonkey.get(i);
				CardImgAudit cardImgAudit=getCardImgAudit(activityMonkey.getByUser().getId());
				Userbasicsinfo user =userbasicsinfoService.queryUserById(activityMonkey.getUserId());
				if(cardImgAudit!=null){
					if(user!=null){
						if(user.getIsAuthIps()!=null){
							if(user.getIsAuthIps()==1){
								DateFormat fmt =new SimpleDateFormat("yyyy-MM-dd");            
					            Date dateUser = fmt.parse(user.getCreateTime());
								if(HcPeachActivitiCache.validCurrentDate(dateUser)==0){
									CardImgAudit cardImgAuditUser=getCardImgAudit(user.getId());
									if(cardImgAuditUser!=null){
										activityMonkey.setExamineStatus(Constant.STATUES_ONE);
										activityMonkey.setExamineTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
										activityMonkey.setExamineAdminId(loginuser.getId());
										dao.update(activityMonkey);
									}
								}else{
										activityMonkey.setExamineStatus(Constant.STATUES_ONE);
										activityMonkey.setExamineTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
										activityMonkey.setExamineAdminId(loginuser.getId());
										dao.update(activityMonkey);
								}
							}
						}
					}
				}
			}
			return "1";
		}catch(Exception e){
			return "2";
		}
	}
	
	
	/***
	 * 根据用户查询身份验证表
	 * @param userId
	 * @return
	 */
	public CardImgAudit  getCardImgAudit(Long userId){
		String sql="select * from cardimgaudit where userId=? and cardImgState=1 ";
		return dao.findObjectBySql(sql, CardImgAudit.class, userId);
	}

}
