package com.hc9.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.common.constant.Constant;
import com.hc9.common.util.Arith;
import com.hc9.common.util.DateUtils;
import com.hc9.dao.entity.Hccoindetail;
import com.hc9.dao.entity.Loanrecord;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.impl.HibernateSupport;

@Service
public class HccoindetailService {
	
	@Resource
	private HibernateSupport dao;
	
	@Resource
	private RedEnvelopeDetailService redEnvelopeDetailService;
	
	/***
	 * 保存红筹币
	 * 红筹币=投资额*投资天数/360*13%
	 * @param loanrecord
	 * @param hccoinMoney
	 * @param user
	 */
	public void saveHccoindetail(Loanrecord loanrecord,Double hccoinMoney,Userbasicsinfo user){
			Integer day=30;
			 if(loanrecord.getLoansign().getType()==2){ //项目
		    	if(loanrecord.getLoansign().getRefunway()==1){
		    		day=day*loanrecord.getLoansign().getRemonth();
		    	}else if(loanrecord.getLoansign().getRefunway()==2){
		    		day=day*loanrecord.getLoansign().getRemonth()*3;
		    	}
		    }else if(loanrecord.getLoansign().getType()==3){ //天标
		    	day=loanrecord.getLoansign().getRemonth();
		    }
			Long number=Math.round(Arith.mul(Arith.div(Arith.mul(loanrecord.getTenderMoney(), day),360),hccoinMoney));
			Hccoindetail hccoindetail=new Hccoindetail();
			hccoindetail.setUserbasicsinfo(user);
			hccoindetail.setSourceType(Constant.STATUES_THERE);
			hccoindetail.setRemark("推荐投资");
			hccoindetail.setNumber(number);
			hccoindetail.setReceiveTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
			hccoindetail.setLoanrecordId(loanrecord.getId());
			dao.save(hccoindetail);
	}
	
	/***
	 * lkl-20150811
	 * 员工推荐注册所得
	 * @param user
	 * @param number
	 */
	public void saveHccoindetailNumber(Userbasicsinfo user){
		Hccoindetail hccoindetail=new Hccoindetail();
		hccoindetail.setUserbasicsinfo(user);
		hccoindetail.setSourceType(Constant.STATUES_ONE);
		hccoindetail.setRemark("推荐注册");
		hccoindetail.setNumber(Long.valueOf(5));
		hccoindetail.setReceiveTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
		dao.save(hccoindetail);
	}
	
	/***
	 * 根据输入信息添加
	 * @param user
	 * @param number
	 * @param remark
	 */
	public void saveHccoindetailQT(Userbasicsinfo user,Integer jjstate,Integer state,Long number,String remark){
		Hccoindetail hccoindetail=new Hccoindetail();
		hccoindetail.setUserbasicsinfo(user);
		hccoindetail.setSourceType(state);
		 if(state==4){
			hccoindetail.setRemark("微课堂");
		}else if(state==5){
			hccoindetail.setRemark("全勤奖");
		}else if(state==6){
			hccoindetail.setRemark("图书馆");
		}else if(state==7){
			hccoindetail.setRemark("活动奖励");
		}else if(state==8){
			hccoindetail.setRemark("拍卖会");
		}else if(state==9){
			hccoindetail.setRemark(remark);
		}
		 if(jjstate==2){
			 hccoindetail.setNumber(Long.valueOf("-"+number));
		 }else{
			 hccoindetail.setNumber(number);
		 }
		hccoindetail.setReceiveTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
		dao.save(hccoindetail);
	}
}
