package com.hc9.service.sms.mw;

import java.util.List;

import com.hc9.model.MO_PACK;
import com.hc9.model.MULTIX_MT;
import com.hc9.model.RPT_PACK;

public interface ISms
{
	public final int PARAM_ERROR=-1002;
	/**
	 * 1.短信息发送接口（相同内容群发，可自定义流水号）
	 * @param strPtMsgId 平台返回的流水号
	 * @param strUserId  帐号
	 * @param strPwd 密码
	 * @param strMobiles 手机号
	 * @param strMessage 短信内容
	 * @param strSubPort 扩展子号
	 * @param strUserMsgId 用户自编流水号
	 * @param bKeepAlive 长连接或短连接,默认短连接,false短连接,true长连接
	 * @param connection 连接对象,如果是短连接，传null
	 * @return 0:成功 非0:返回webservice接口返回的错误代码
	 */
	public int SendSms(StringBuffer strPtMsgId, String strMobiles, String strMessage, String strSubPort, String strUserMsgId, boolean bKeepAlive,Object connection);
	
	
	/**
	 * 2.短信息发送接口（不同内容群发，可自定义不同流水号，自定义不同扩展号）
	 * @param strPtMsgId 平台返回的流水号
	 * @param strUserId 帐号
	 * @param strPwd 密码
	 * @param MultixMt 批量请求包
	 * @param bKeepAlive 长连接或短连接,默认短连接,false短连接,true长连接
	 * @param connection 连接对象,如果是短连接，传null
	 * @return 0:成功 非0:返回webservice接口返回的错误代码
	 */
	public int SendMultixSms(StringBuffer strPtMsgId, List<MULTIX_MT> MultixMt, boolean bKeepAlive,Object connection);
	
	
	/**
	 * 3.查询余额接口
	 * @param nBalance 帐号费用
	 * @param strUserId 帐号
	 * @param strPwd 密码
	 * @param bKeepAlive 长连接或短连接,默认短连接,false短连接,true长连接
	 * @param connection 连接对象,如果是短连接，传null
	 * @return 0:成功 非0:返回webservice接口返回的错误代码
	 */
	public int QueryBalance(StringBuffer nBalance, boolean bKeepAlive,Object connection);
	
	
	/**
	 * 4.获取上行
	 * @param strUserId 帐号
	 * @param strPwd 密码
	 * @param bKeepAlive 长连接或短连接,默认短连接,false短连接,true长连接
	 * @param connection 连接对象,如果是短连接，传null
	 * @return 返回上行集合
	 */
	public List<MO_PACK> GetMo(boolean bKeepAlive,Object connection);
	
	
	/**
	 * 5.状态报告
	 * @param strUserId 帐号
	 * @param strPwd 密码
	 * @param bKeepAlive 长连接或短连接,默认短连接,false短连接,true长连接
	 * @param connection 连接对象,如果是短连接，传null
	 * @return 返回状态报告集合
	 */
	public List<RPT_PACK> GetRpt(boolean bKeepAlive,Object connection);

	/**
	 * 初始化参数
	 * @param sn
	 * @param pwd
	 */
	public void init(String userId, String pwd);
	
	
}
