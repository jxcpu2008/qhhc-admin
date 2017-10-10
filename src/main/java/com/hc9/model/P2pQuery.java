package com.hc9.model;

 
/**
 * 业务查询
 * @author frank 2014-08-18
 *
 */
public class P2pQuery {
	/***
	 * 订单ID,多ID之间使用英文逗号间隔,单ID不需要逗号。
	 */
	private String order_id;
	
	/***
	 *   1-投标 2-满标 3-流标  4-还款 5-充值 6-提现 ;
	 */
	private int type;
	
	/***
	 *  start_time 查询开始时间 毫秒数;
	 */
	private String start_time;
	
	/***
	 *  end_time 查询结束时间 毫秒数;
	 */
	private String end_time;
	
	
	/***
	 * 根据订单号与类型查询
	 * @param orderId
	 * @param type
	 */
	public  P2pQuery(String orderId,Integer type){
		this.order_id=orderId;
		this.type=type;
	}
	
	/***
	 * 根据类型与时间查询
	 * @param type
	 * @param startTime
	 * @param endTime
	 */
	public P2pQuery(Integer type,String startTime,String  endTime){
		this.type=type;
		this.start_time=startTime;
		this.end_time=endTime;
	}
	
	/***
	 * 根据订单号与类型、时间查询
	 * @param orderId
	 * @param type
	 * @param startTime
	 * @param endTime
	 */
	public P2pQuery(String orderId,Integer type,String startTime,String  endTime){
		this.order_id=orderId;
		this.type=type;
		this.start_time=startTime;
		this.end_time=endTime;
	}
	
	public P2pQuery(){
		
	}
	
	public String getOrder_id() {
		return order_id;
	}
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getStart_time() {
		return start_time;
	}
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}
	public String getEnd_time() {
		return end_time;
	}
	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}
	

}
