package com.hc9.model;

import com.hc9.common.constant.Constant;

/**
 * 提现
 * @author RanQiBing 2014-01-03
 *
 */
public class WithdrawalInfo {
		/**商户号 */
		private String merchant_id; 
		
	    /**充值订单号  必填  商户系统唯一 */
		private String order_id; 
		
		/**用户编号，系统唯一*/
		private String user_id; 
		
		/**充值金额 单位 元*/
		private Double amount;  
		
		/**充值手续费,单位：元*/
		private Double fee;   
		
		/**费用承担方(宝付收取的费用) */
		private String fee_taken_on; 
		
		/**状态返回地址1  必填  浏览器返回地址 */
		private String page_url = Constant.WITHDRAWAL;
		private String return_url = Constant.WITHDRAWASYNCHRONOUS;

		/**bank_no*/
		private String bank_no;
		
		
		
		
		public String getPage_url() {
			return page_url;
		}


		public void setPage_url(String page_url) {
			this.page_url = page_url;
		}


		public String getBank_no() {
			return bank_no;
		}


		public void setBank_no(String bank_no) {
			this.bank_no = bank_no;
		}


		public String getMerchant_id() {
			return merchant_id;
		}


		public void setMerchant_id(String merchant_id) {
			this.merchant_id = merchant_id;
		}


		public String getOrder_id() {
			return order_id;
		}


		public void setOrder_id(String order_id) {
			this.order_id = order_id;
		}


		public String getUser_id() {
			return user_id;
		}


		public void setUser_id(String user_id) {
			this.user_id = user_id;
		}


		public Double getAmount() {
			return amount;
		}


		public void setAmount(Double amount) {
			this.amount = amount;
		}


		public Double getFee() {
			return fee;
		}


		public void setFee(Double fee) {
			this.fee = fee;
		}


		public String getFee_taken_on() {
			return fee_taken_on;
		}


		public void setFee_taken_on(String fee_taken_on) {
			this.fee_taken_on = fee_taken_on;
		}


		public String getReturn_url() {
			return return_url;
		}


		public void setReturn_url(String return_url) {
			this.return_url = return_url;
		} 
		
		
}
