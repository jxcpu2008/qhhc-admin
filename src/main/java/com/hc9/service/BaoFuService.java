package com.hc9.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Service;

import com.hc9.common.constant.PayURL;
import com.hc9.common.util.CommonUtil;
import com.hc9.common.util.ParameterIps;
import com.hc9.common.util.ParseXML;
import com.hc9.commons.log.LOG;
import com.hc9.dao.entity.UserBank;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.BalanceQueryInfo;
import com.hc9.model.crs;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import freemarker.template.TemplateException;

@Service
public class BaoFuService {
	@Resource
	private HibernateSupport dao;
	
	@Resource
	private UserbasicsinfoService userbasicsinfoService;
	@Resource
	private FundsAccountService fundsAccountService;
	
	@Resource
	private UserBankService bankService;
	
	private List<NameValuePair> nvps;	
	
	private static Map<String,String> MSG;
	
	
	static{
		MSG=new HashMap<>();
		MSG.put("CSD001", "系统参数传入错误");
		MSG.put("CSD002", "系统参数缺省或请求参数不符合要求");
		MSG.put("CSD003", "验签失败");
		MSG.put("CSD004", "XML参数解析错误");
		MSG.put("CSD005", "宝付账户不存在");
		MSG.put("CSD006", "该商户不支持资金托管");
		MSG.put("CSD007", "该指令已存在");
		MSG.put("CSD008", "商户下尚无用户");
		MSG.put("CSD009", "该会员已注销资当前商户资金托管");
		MSG.put("BD001", "该宝付账户已经被绑定");
		MSG.put("BD002", "实名认证失败");
		MSG.put("BD003", "未进行实名验证");
		MSG.put("BD004", "平台绑定码验证失败");
		MSG.put("BD005", "绑定账号与账号类型不匹配");
		MSG.put("BD006", "信息发送失败");
		MSG.put("BD007", "邮箱 , 暂不支持，请使用手机号");
		MSG.put("BD008", "身份证号、姓名不能为空");
		MSG.put("BD009", "该会员已经存在");
		MSG.put("RQ001", "指令错误：每次只支持一个action");
		MSG.put("RQ002", "请确认该还款人的唯一性");
		MSG.put("RQ003", "余额不足");
		MSG.put("RQ004", "单号不能重复");
		MSG.put("RQ005", "只能有一个借款人");
		MSG.put("RQ006", "满标金额，与投资金额不一致");
		MSG.put("SQ002", "当前用户资金托管-未授权，需先开通授权");
		MSG.put("PW001", "该会员不支持提现服务");
		MSG.put("PW002", "该会员不支持充值服务");
		MSG.put("PW003", "该会员不支持提现服务");
		MSG.put("PW004", "提现失败");
		MSG.put("EX001", "系统异常");
		MSG.put("EX002", "数据库操作异常");
		MSG.put("EX003", "宝付处理失败,请重试");
		MSG.put("EX004", "HTTP请求异常");
	}

	/**
	 * 查询宝付余额
	 * 
	 * @param userId
	 *            宝付账户id
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 */
	public crs getCasbalance(String userId) throws IOException,TemplateException {
		
		BalanceQueryInfo balanceQueryInfo = new BalanceQueryInfo(userId,ParameterIps.getCert());
		String reqParams = ParseXML.BalanceInquiryXml(balanceQueryInfo);
		nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("requestParams", reqParams));
		nvps.add(new BasicNameValuePair("sign", CommonUtil.MD5(reqParams + "~|~" + ParameterIps.getmerchantKey())));
		LOG.error("开始调用查询宝付余额接口：" + PayURL.BALABCEINQUIRYTESTURL + ", 参数为：userId=" + userId);
		String result2 = CommonUtil.excuteRequest(PayURL.BALABCEINQUIRYTESTURL,nvps);
		LOG.error("查询宝付余额接口返回结果：" + result2);
		result2 = result2.replace("\"", "\'");
		crs cr = new crs();
		XStream xss2 = new XStream(new DomDriver());
		xss2.alias(cr.getClass().getSimpleName(), cr.getClass());
		cr = (crs) xss2.fromXML(result2);
		return cr;
	}
	
	/***
	 * 查询银行卡信息
	 * @param userId
	 * @return
	 */
	public String getUserBank(long userId) {
		Userbasicsinfo user = userbasicsinfoService.queryUserById(userId);
		if(user.getpMerBillNo()==null||user.getpMerBillNo().equals("") || user.getIsAuthIps()==null || user.getIsAuthIps()==0){
			return "1";
		}
		nvps = new ArrayList<NameValuePair>();
		try {
			nvps.add(new BasicNameValuePair("requestParams", user.getpMerBillNo()));
			nvps.add(new BasicNameValuePair("sign", CommonUtil.MD5(user.getpMerBillNo() + "~|~" + ParameterIps.getmerchantKey())));
			String result = CommonUtil.excuteRequest(PayURL.GETBANKCARURL, nvps);
			System.out.println("绑定验证码=" + result);
			Document doc = DocumentHelper.parseText(result);
			Element rootElt = doc.getRootElement(); // 获取根节点
			System.out.println("根节点：" + rootElt.getName()); // 拿到根节点的名称
			// 拿到crs节点下的子节点code值
			String code = rootElt.elementTextTrim("code");
			String msg = rootElt.elementTextTrim("msg");
			String sign = rootElt.elementTextTrim("sign");
			String infoXml = rootElt.elementTextTrim("info");
			String Md5sign = CommonUtil.MD5(code + "~|~" + msg + "~|~"+ ParameterIps.getDes_algorithm());
			if (sign.equals(Md5sign)) {
				if (code.equals("CSD000")) {
					String info = CommonUtil.aesDecryptKey16(infoXml,ParameterIps.getmerchantKey());
					System.out.println(info);
					Document infoDoc = DocumentHelper.parseText(info);
					Element rootInfoElt = infoDoc.getRootElement(); // 获取根节点
					System.out.println("根节点：" + rootInfoElt.getName()); // 拿到根节点的名称
					Iterator iteratorOrder = rootInfoElt.elementIterator("bank"); // 获取子节点result下的子节点order
					if(bankService.getUserBankId(user.getId())){
						fundsAccountService.deleteUserBank(user.getId());
					}
					while (iteratorOrder.hasNext()) {
						Element elementOrder = (Element) iteratorOrder.next();
						String bank_no = elementOrder.elementTextTrim("bank_no");
						String bank_name = elementOrder.elementTextTrim("bank_name");
						String pro_value = elementOrder.elementTextTrim("pro_value");
						String city_value = elementOrder.elementTextTrim("city_value");
						String bank_address = elementOrder.elementTextTrim("bank_address");
						UserBank userBank=new UserBank();
						userBank.setUserbasicsinfo(user);
						userBank.setBank_no(bank_no);
						userBank.setBank_name(bank_name);
						userBank.setPro_value(pro_value);
						userBank.setCity_value(city_value);
						userBank.setBank_address(bank_address);
						userBank.setState(1);
						fundsAccountService.saveOrUpdateUserBank(userBank);
					}
					return "2";
				}  else {
					return "6";//未查到信息
				}
			} else {
				return "3";
			}
		} catch (Exception e) {
			return "3";
		}
	}
	
}
