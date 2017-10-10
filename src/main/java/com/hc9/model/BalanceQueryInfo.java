package com.hc9.model;

/**
 * 用户账户余额查询
 * 
 * @author RanQiBing 2014-01-03
 * 
 */
public class BalanceQueryInfo {

	  /***
	   * 商户号
	   */
      private String user_id;
      
      /***
       * 用户编号(唯一)
       */
      private String merchant_id;
      
      
      public BalanceQueryInfo(){}
      
      
      public BalanceQueryInfo(String userId,String merchantId){
    	  this.user_id=userId;
    	  this.merchant_id=merchantId;
      }


	public String getUser_id() {
		return user_id;
	}


	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}


	public String getMerchant_id() {
		return merchant_id;
	}

	public void setMerchant_id(String merchant_id) {
		this.merchant_id = merchant_id;
	}
      
    
  

   
}
