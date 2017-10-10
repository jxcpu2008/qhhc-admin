package com.hc9.model;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.hc9.common.util.ParameterIps;
import com.hc9.dao.entity.Loansign;
import com.hc9.dao.entity.Shop;

 
/**
 * 投标
 * 投资人手续费用与是否vip相关，用属性注入
 * @author frank 2014-08-18
 *
 */

public class BidInfo {
	
	/***
	 * 商户号
	 */
	private String merchant_id;
	
	/***
	 * 请求类型：1-投标 2-满标 3-流标 4-还标
	 */
	private String action_type;
	
	/***
	 * 订单号(唯一不允许重复) 允许字符串
	 */
	private String order_id;
	
	/***
	 * 标Id  纯数字
	 */
	private String cus_id;
	
	/***
	 * 标名称
	 */
	private String cus_name;
	
	/***
	 * 借款人user_id用户编号
	 */
	private String brw_id;
	
	/***
	 * 请求时间
	 * 例如 1405668253874    （当前时间转换毫秒）
	 */
	private String req_time;
	
	/***
	 *  <actions>
	 *    <action>
	 *     <user_id>20140374</user_id>
	 *     <amount>50.00</amount>
	 *    </action>
	 *  </actions>
	 */
	public  List<Action> actions=new ArrayList<Action>();
	
	/***
	 * 用户编号(唯一)
	 */
	private String user_id;
	
	/**
	 * 用户姓名
	 */
	private String user_name;
	
	/***
	 * 金额
	 */
	private String amount;
	/***
	 * 手续费(涉及到满标、还款接口)
	 */
	private String fee;
	
	
	public BidInfo(){
		
	}
	
	
   /***
    * 投标店铺报文
    * @param shop  店铺信息
    * @param userbasicsinfo   用户信息
    * @param fee    手续费
    * @param actionList  投资人信息  
    */
	public BidInfo(Shop shop,String orderNum,Double fee,String actionType,List<Action> actionList){
	     DecimalFormat df=new DecimalFormat("0.00");
		 this.merchant_id=ParameterIps.getCert();
		 this.action_type=actionType; 
		 this.order_id=orderNum;
		 this.cus_id=shop.getCus_id();
		 this.cus_name=shop.getShopName();
		 this.brw_id=shop.getUserbasicsinfo().getpMerBillNo();
		 this.req_time=String.valueOf(new Date().getTime());
		 this.fee=df.format(fee);
		 this.actions=actionList;
	}
	
	/***
	 * 投标项目报文
	 * @param loansign  
	 * @param orderNum  订单号
	 * @param fee   手续费
	 * @param actionType    //请求类型，投标为1，满标为2，流标为3，还标为4
	 * @param actionList   购买人信息
	 */
	public BidInfo(Loansign loansign,String orderNum,Double fee,String actionType,List<Action> actionList){
	     DecimalFormat df=new DecimalFormat("0.00");
		 this.merchant_id=ParameterIps.getCert();
		 this.action_type=actionType; 
		 this.order_id=orderNum;
		 this.cus_id=loansign.getCus_id();
		 this.cus_name=loansign.getName();
		 this.brw_id=loansign.getUserbasicsinfo().getpMerBillNo();
		 this.req_time=String.valueOf(new Date().getTime());
		 this.fee=df.format(fee);
		 this.actions=actionList;
	}
	
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getMerchant_id() {
		return merchant_id;
	}
	public void setMerchant_id(String merchant_id) {
		this.merchant_id = merchant_id;
	}
	
	/**
	 * 请求类型，投标为1，满标为2，流标为3，还标为4
	 * @return
	 */
	public String getAction_type() {
		return action_type;
	}
	public void setAction_type(String action_type) {
		this.action_type = action_type;
	}
	
	/**
	 * 订单号 （唯一不允许重复）允许字符
	 * @return
	 */
	public String getOrder_id() {
		return order_id;
	}
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}
	
	
	/**
	 * 标id
	 * @return
	 */
	public String getCus_id() {
		return cus_id;
	}
	public void setCus_id(String i) {
		this.cus_id = i;
	}
	
	
	/**
	 * 标名称
	 * @return
	 */
	public String getCus_name() {
		return cus_name;
	}
	public void setCus_name(String cus_name) {
		this.cus_name = cus_name;
	}
	
	/**
	 * 借款人user_id用户编号(唯一)
	 * @return
	 */
	public String getBrw_id() {
		return brw_id;
	}
	public void setBrw_id(String brw_id) {
		this.brw_id = brw_id;
	}
	
	/**
	 * 请求时间
	 * @return
	 */
	public String getReq_time() {
		return req_time;
	}
	public void setReq_time(String req_time) {
		this.req_time = req_time;
	}
	
	/**
	 * 用户编号(唯一)
	 * @return
	 */
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String string) {
		this.user_id = string;
	}
	
	/**
	 * 金额，单位：元
	 * @return
	 */
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	
	/**
	 * 手续费(涉及到满标、还款接口)
	 * @return
	 */
	public String getFee() {
		return fee;
	}
	public void setFee(String fee) {
		this.fee = fee;
	}


	public List<Action> getActions() {
		return actions;
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
	}


}
