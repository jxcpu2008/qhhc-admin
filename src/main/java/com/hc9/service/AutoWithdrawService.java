package com.hc9.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Service;

import com.hc9.common.constant.Constant;
import com.hc9.common.constant.PayURL;
import com.hc9.common.util.Arith;
import com.hc9.common.util.CommonUtil;
import com.hc9.common.util.DateUtil;
import com.hc9.common.util.DateUtils;
import com.hc9.common.util.ParameterIps;
import com.hc9.common.util.ParseXML;
import com.hc9.commons.log.LOG;
import com.hc9.dao.entity.Accountinfo;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.entity.Withdraw;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.P2pQuery;
import com.hc9.model.crs;

@Service
public class AutoWithdrawService {
	
	@Resource
	private HibernateSupport dao;
	
	@Resource
	private BaoFuService baoFuService;

	@Resource
	private PayLogService payLogService;
	
	@Resource
	private UserbasicsinfoService userbasicsinfoService;
	
	@Resource
	private WithdrawServices withdrawServices;
	
	@Resource
	private PlankService plankService;
	
	List<NameValuePair> nvps;
	
	
	
	/***
	 * 根据时间-1查询提现记录
	 * @return
	 */
	public List<Withdraw> getWithdrawList(){
		String time=DateUtil.getSpecifiedDateAfter(DateUtil.format("yyyy-MM-dd"), -1);
		String sql="select * from withdraw where state not in(-1,1) and  DATE_FORMAT(time, '%Y-%m-%d')<=DATE_FORMAT(?, '%Y-%m-%d')  order by id  limit 0,20 ";
		List<Withdraw> withdrawList = dao.findBySql(sql, Withdraw.class, time);
		return withdrawList;
	}
	
	public Withdraw selWithdraw(String strNum) {
		String sql = "select * from withdraw where strNum=?";
		Withdraw withdraw = dao.findObjectBySql(sql, Withdraw.class, strNum);
		return withdraw;
	}
	
	/***
	 * 查询提现记录
	 */
	public void autoWithdrawQuery(String orderNumber){
 		P2pQuery p2pQuery = new P2pQuery(orderNumber, 6);
		Withdraw withdraw=new Withdraw();
		nvps = new ArrayList<NameValuePair>();
		try {
			String withdrawXml = ParseXML.p2pQueryXml(p2pQuery);
			System.out.println("查询提现报文="+withdrawXml);
			nvps.add(new BasicNameValuePair("requestParams", withdrawXml));
			nvps.add(new BasicNameValuePair("sign", CommonUtil.MD5(withdrawXml+ "~|~" + ParameterIps.getmerchantKey())));
			String result = CommonUtil.excuteRequest(PayURL.P2PQUERYTESTURL,nvps);
			System.out.println("提现业务查询=" + result);
			Document doc = DocumentHelper.parseText(result);
			Element rootElt = doc.getRootElement(); // 获取根节点
			System.out.println("根节点：" + rootElt.getName()); // 拿到根节点的名称
			// 拿到crs节点下的子节点code值
			String code = rootElt.elementTextTrim("code");
			String msg = rootElt.elementTextTrim("msg");
			String sign = rootElt.elementTextTrim("sign");
			// 获取子节点crs下的子节点result
			Iterator iteratorResult = rootElt.elementIterator("result");
			boolean Judge = false; // 判断是否有值
			String state = "-1";
			// 遍历result节点下的Response节点
			while (iteratorResult.hasNext()) {
				Element itemEle = (Element) iteratorResult.next();
				Iterator iteratorOrder = itemEle.elementIterator("order"); // 获取子节点result下的子节点order
				while (iteratorOrder.hasNext()) {
					Element elementOrder = (Element) iteratorOrder.next();
					String order_id = elementOrder.elementTextTrim("order_id");
					state = elementOrder.elementTextTrim("state");
					String succ_amount = elementOrder.elementTextTrim("succ_amount");
					String succ_time = elementOrder.elementTextTrim("succ_time");
					String fee = elementOrder.elementTextTrim("fee");
					String baofoo_fee = elementOrder.elementTextTrim("baofoo_fee");
					String fee_taken_on = elementOrder.elementTextTrim("fee_taken_on");
				    withdraw=selWithdraw(order_id);
				    Double addFee=withdraw.getMer_fee();
					String Md5sign = CommonUtil.MD5(code + "~|~" + msg + "~|~"+ ParameterIps.getDes_algorithm());
					if (sign.equals(Md5sign)) {
						if (code.equals("CSD000")) {
							if (state.equals("1")) { // 转账成功
								withdraw.setState(Constant.STATUES_ONE);
								withdraw.setFee(2.00);
								withdraw.setMer_fee(Arith.sub(withdraw.getMer_fee(), 2));
								if(succ_amount==null){
									succ_amount="0";
								}
								withdraw.setWithdrawAmount(Double.valueOf(succ_amount) != null ? null : 0.00);
								withdraw.setApplytime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
								withdraw.setCode(code);
								addFee=Arith.add(withdraw.getMer_fee(), withdraw.getFee());
								//withdraw.setFee_taken_on(Integer.valueOf(fee_taken_on));
							} else if (state.equals("-1")) { // -1转账失败
								withdraw.setState(-1);
							} else if (state.equals("5")) { // 转账处理中
								withdraw.setState(Constant.STATUES_FIVE);
							} else if (state.equals("0")) { // 初始化
								withdraw.setState(Constant.STATUES_ZERO);
							}
							if (state.equals("1")) {
								Userbasicsinfo obj = userbasicsinfoService.queryUserById(String.valueOf(withdraw.getUserbasicsinfo().getId()));
								// 余额查询
								crs cr = baoFuService.getCasbalance(obj.getpMerBillNo());
								obj.getUserfundinfo().setCashBalance(cr.getBalance());
								obj.getUserfundinfo().setOperationMoney(cr.getBalance());
								// 流水账
								Accountinfo account = new Accountinfo();
								account.setExpenditure(Double.valueOf(succ_amount));
								account.setExplan("提现");
								account.setIncome(0.00);
								account.setIpsNumber(order_id);
								account.setTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
								account.setUserbasicsinfo(obj);
								account.setAccounttype(plankService.accounttype(7L));
								account.setMoney(cr.getBalance());
								account.setFee(Arith.add(withdraw.getMer_fee(), withdraw.getFee()));
								plankService.saveAccount(account);// 保存流水账余额
								userbasicsinfoService.update(obj);
								Judge = true;
							}
							// 更新支付报文信息
							payLogService.updatePayLog(order_id,Integer.valueOf(state),withdraw.getWithdrawAmount(),addFee);

							withdrawServices.uptWithdraw(withdraw);
							LOG.error("宝付支付提现查询处理成功");
						} else {
							LOG.error("宝付提现查询处理失败--" + result+ "----->订单号----->" + withdraw.getStrNum());
						}
					} else {
						LOG.error("非宝付提现查询返回数据--" + result + "----->订单号----->"+ withdraw.getStrNum());
					}
				}
			}
			if (!Judge && state.equals("-1")) {
				if (withdraw.getState() != -1) {
					withdraw.setFee(0.00);
					withdraw.setState(-1);
					withdrawServices.uptWithdraw(withdraw);
					// 更新支付报文信息
					payLogService.updatePayLog(withdraw.getStrNum(),Integer.valueOf(state),withdraw.getWithdrawAmount(), withdraw.getFee());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("宝付提现查询失败----->订单号----->" + withdraw.getStrNum());
		}
	}

}
