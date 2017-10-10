package com.hc9.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.common.constant.IntegralType;
import com.hc9.common.constant.PayURL;
import com.hc9.common.json.JsonUtil;
import com.hc9.common.log.LOG;
import com.hc9.common.util.Arith;
import com.hc9.common.util.DateUtils;
import com.hc9.common.util.ParameterIps;
import com.hc9.common.util.ParseXML;
import com.hc9.dao.entity.Accountinfo;
import com.hc9.dao.entity.Accounttype;
import com.hc9.dao.entity.Autointegral;
import com.hc9.dao.entity.Automatic;
import com.hc9.dao.entity.Loandynamic;
import com.hc9.dao.entity.Loanrecord;
import com.hc9.dao.entity.Loansign;
import com.hc9.dao.entity.Loansignbasics;
import com.hc9.dao.entity.Loansignflow;
import com.hc9.dao.entity.Paylog;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.BidAssignment;
import com.hc9.model.BidInfo;
import com.ips.security.utility.IpsCrypto;

import freemarker.template.TemplateException;

/**
 * 标的购买服务层
 * @author frank 
 * 2015-1-2
 */
@Service
public class PlankService {
	
	@Resource
	private HibernateSupport dao;
	
	@Resource
	private LoanSignQuery loanSignQuery;

	@Resource
	private UserInfoServices userInfoServices;
	
	@Resource
	private IntegralSevice integralSevice;
	

	@Resource
	private PayLogService payLogService;
	
	@Resource
	private LoanrecordService loanrecordService;
	

	/**
	 * 
	 * @param loanrecord
	 * @param accountinfo
	 * @param money
	 */
	public void update(Loanrecord loanrecord,Accountinfo accountinfo,Userbasicsinfo userbasicsinfo,Loansign loan){
		dao.saveOrUpdate(loanrecord);
		dao.save(accountinfo);
		dao.save(loan);
	    dao.saveOrUpdate(userbasicsinfo);
	}
	
	
	public void updatePlank(Loanrecord loanrecord,Accountinfo accountinfo,Userbasicsinfo userbasicsinfo,Loansign loan){
		dao.update(loanrecord);
		dao.save(accountinfo);
		dao.update(loan);
	    dao.update(userbasicsinfo);
	}
	
	
	public void saveAccount(Accountinfo ac){
		dao.save(ac);
	}
	
	
	/***
	 * 保存放款时新增一条项目动态
	 * @param loandynamic
	 */
	public void saveLoandynamic(Loandynamic loandynamic){
		LOG.error("支付记录日志：" + JsonUtil.toJsonStr(loandynamic));
		dao.save(loandynamic);
	}
	
	
	/**
	 * 
	 * @param loanrecord
	 * @param accountinfo
	 * @param money
	 */
	public void update(Loanrecord loanrecord,Accountinfo accountinfo,Double money,Loansignflow loansignflow,Loanrecord loanrecordLoan,Double moneyLoan,Loansignbasics loansignbasics){
		if(loanrecord.getTenderMoney()!=null){
			dao.save(loanrecord);
		}
		dao.save(accountinfo);
		dao.update(loanrecordLoan);
		dao.update(loansignflow);
		dao.save(loansignbasics);
		
		String sql = "UPDATE userfundinfo SET userfundinfo.cashBalance=? where id =?";
		if(loanrecord.getTenderMoney()!=null){
			dao.executeSql(sql,money,loanrecord.getUserbasicsinfo().getId());
		}else{
			dao.executeSql(sql,money,loansignflow.getUserAuth());
		}
		dao.executeSql(sql,moneyLoan,loanrecordLoan.getUserbasicsinfo().getId()); //原购买人
	}
	/**
	 * 获取类型
	 * @param id 类型编号
	 * @return
	 */
	public Accounttype accounttype(Long id){
		return dao.get(Accounttype.class, id);
	}
	
	/**
	 * 加密投标信息
	 * @param bid
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 */
    public Map<String, String> encryption(BidInfo bid)
            throws IOException, TemplateException {
        // 将充值信息转换成xml文件
      //  String bidxml = ParseXML.bidXml(bid);
        // 加密后的信息
     //   Map<String, String> map = regSubCall(bidxml);
        // 将访问地址放在map里
   //     map.put("url", PayURL.BIDTESTURL);
        return new HashMap();
    }
    
	public Map<String,String> regSubCall(String bidxml){
	
		//生成xml文件字符串
		//String  = ParseXML.registration(entity);
		//将生成的xml文件进行3des加密
		String desede = IpsCrypto.triDesEncrypt(bidxml,ParameterIps.getDes_algorithm(),ParameterIps.getDesedevector());
		//将加密后的字符串不换行
		desede = desede.replaceAll("\r\n","");
		//将“ 平台 ”账号 、用户注册信息、证书拼接成一个字符串
		StringBuffer argSign = new StringBuffer(ParameterIps.getCert()).append(desede).append(ParameterIps.getMd5ccertificate());
		//将argSign进行MD5加密
		String md5 = IpsCrypto.md5Sign(argSign.toString());
		//将参数装进map里面
		Map<String,String> map = new HashMap<String, String>();
		map.put("pMerCode",ParameterIps.getCert());
		map.put("p3DesXmlPara", desede);
		map.put("pSign",md5);
		return map;
	}

	/**
	 * 债权转让投标信息
	 * @param assignment
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 */
    public Map<String, String> encryptionAssignment(BidAssignment assignment)
            throws IOException, TemplateException {
        // 将充值信息转换成xml文件
        String bidxml = ParseXML.bidAssignmentXml(assignment);
        // 加密后的信息
        Map<String, String> map = regSubCall(bidxml);
        // 将访问地址放在map里
        map.put("url", PayURL.BIDASSIGNMENTTESTURL);
        return map;
    }
    
    
    /***
     * 保存自动投标规则数据
     * @param automatic
     */
    public void saveAutomatic(Automatic automatic){
    	dao.save(automatic);
    }
    
    
    /***
     * 保存自动投标规则数据
     * @param automatic
     */
    public void saveLoanrecord(Loanrecord lo){
    	dao.save(lo);
    }
    
    
    /***
     * 根据pP2PBillNo查询automatic是否存在
     * @param pP2PBillNo
     * @return
     */
    public String getAutomaticId(String pP2PBillNo){
    	if(!pP2PBillNo.equals("")&&pP2PBillNo!=null){
    		String sql="select id from automatic where pP2PBillNo='"+pP2PBillNo+"'";
        	Object id=this.dao.findBySql(sql);
        	return id.toString().substring(1, id.toString().length()-1);
    	}else{
    		return null;
    	}
    }
    
    /**
     * 投资项目所得积分
     * @param user  用户
     * @param product 投资项目所得积分
     * @param type  购买类型
     */
    public void saveAutointegralBuyProject(Userbasicsinfo user,double money,Integer type) {

    	switch(type){
    	case 1:
    		integralSevice.invest(user, money, IntegralType.PRIO);
    		break;
    	case 2:
    		integralSevice.invest(user, money, IntegralType.MIDDLE);
    		break;
    	case 3:
    		integralSevice.invest(user, money, IntegralType.AFTER);
    		break;
    	}
    	/*
       	String content="劣后项目投资";
    	if(type==1){
    		content="优先项目投资";
    	}else if(type==2){
    		content="夹层项目投资";
    	}
	   	Autointegral autointegral=new Autointegral();
	   	autointegral.setUserbasicsinfo(user);
	   	autointegral.setRealityintegral(product);
	   	autointegral.setContent(content);
	   	autointegral.setGetintegraltime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));  
	   	dao.save(autointegral);
	   	*/
    }	
    
    
    public void saveAutointegralBuyDayMark(Userbasicsinfo user,Integer product) {
   	
	   	Autointegral autointegral=new Autointegral();
	   	autointegral.setUserbasicsinfo(user);
	   	autointegral.setRealityintegral(product);
	   	autointegral.setContent("天标认购");
	   	autointegral.setGetintegraltime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));  
	   	dao.save(autointegral);
    }
    


	/**
	 * 生成 预购标 信息
	 * @param loanrecord
	 * @param loan
	 */
	public void createReserveInfo(Loanrecord loanrecord, Loansign loan) {
		dao.saveOrUpdate(loanrecord);
		dao.saveOrUpdate(loan);
	}
	
	/***
	 * 根据loanrecordId删除购买标状态=0待确认的loanrecord
	 * @param loanrecordId
	 * @return
	 */
	public  boolean  getLoanInfoRecord(String orderNum){
		boolean state=false;
		//查询购买记录
		 Loanrecord  loanRecord=loanrecordService.getLoanRecordOrderNum(orderNum);
		//获得项目记录
		Loansign loan=loanSignQuery.getLoansignById(loanRecord.getLoansign().getId().toString());
		//查询订单
		Paylog payLog = payLogService.queryPaylogByOrderSn(orderNum);
		if(loanRecord.getIsSucceed()==0){
			 //剩余金额
			 loan.setRestMoney(Arith.add(loan.getRestMoney(),loanRecord.getTenderMoney()));
			 if(loanRecord.getSubType()==1){  //优先
				  if(loanRecord.getIsType()==0){   //默认
						  loan.setPrioRestMoney(Arith.add(loan.getPrioRestMoney(), loanRecord.getTenderMoney()));
					  }else if(loanRecord.getIsType()==2){   //夹层转优先
						  Double moneyNum=Arith.sub(loanRecord.getTenderMoney(),loanRecord.getSubMoney());  //购买金额-差额=夹层剩余金额
						  loan.setMiddle(Arith.add(loan.getMiddle(), loanRecord.getSubMoney()));  //夹层总额+差额
					      loan.setMidRestMoney(Arith.add(loan.getMidRestMoney(), loanRecord.getSubMoney()));  //夹层剩余金额+差额
						  loan.setPriority(Arith.sub(loan.getPriority(),  loanRecord.getSubMoney()));  //优先总额-差额
						  loan.setPrioRestMoney(Arith.add(loan.getPrioRestMoney(), moneyNum));  //优先剩余金额
					  }
				 }else if(loanRecord.getSubType()==2){   //夹层
					 if(loanRecord.getIsType()==0){
						  loan.setMidRestMoney(Arith.add(loan.getMidRestMoney(), loanRecord.getTenderMoney()));
					  }else if(loanRecord.getIsType()==1){ //优先转夹层
						  Double moneyNum=Arith.sub(loanRecord.getTenderMoney(),loanRecord.getSubMoney());  //购买金额-差额=优先剩余金额
						  loan.setPriority(Arith.add(loan.getPriority(),  loanRecord.getSubMoney()));  //优先总金额+差额
						  loan.setPrioRestMoney(Arith.add(loan.getPrioRestMoney(),  loanRecord.getSubMoney()));  //优先剩余总金额+差额
						  loan.setMiddle(Arith.sub(loan.getMiddle(), loanRecord.getSubMoney())); //夹层总额-差额
						  loan.setMidRestMoney(Arith.add(loan.getMidRestMoney(), moneyNum));  //夹层剩余金额
					  }
			 }else  if(loanRecord.getSubType()==3){  //劣后
				 loan.setAfterRestMoney(Arith.add(loan.getAfterRestMoney(),loanRecord.getTenderMoney()));
			 }
			 loanSignQuery.updateLoansign(loan);
			 loanSignQuery.deleteLoanrecord(loanRecord);
			 state=true;
			 System.out.println("record "+loanRecord+" 删除成功~~~");
		}
		 return state;
	}
}
