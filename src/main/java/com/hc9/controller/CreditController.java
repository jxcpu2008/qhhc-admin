package com.hc9.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.common.util.DataSecurityUtil;
import com.hc9.common.util.DateUtils;
import com.hc9.common.util.HttpRequestUtil;
import com.hc9.common.util.ParameterPaic;
import com.hc9.common.util.StringUtil;

/**
 * <p>
 * Title:CreditController
 * </p>
 * <p>
 * Description: 普通标Controller
 * </p>
 * <p>
 * Company: 前海红筹
 * </p>
 * 
 * @author shalianzhi
 *         <p>
 *         date 2015年10月27日
 *         </p>
 */
@Controller
@RequestMapping("/credit")
@CheckLogin(value=CheckLogin.ADMIN)
public class CreditController {
	private static final Logger LOGGER = Logger
			.getLogger(UserInfoController.class);

	/*-----------------项目开始-------------------*/
	/**
	 * <p>
	 * Title: index
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @return 后台信用查询页
	 */
	@RequestMapping(value = { "index", "/" })
	public ModelAndView index() {
		ModelAndView returnModelAndView = new ModelAndView(
				"WEB-INF/views/admin/credit/credit");
		return returnModelAndView;
	}

	/**
	 * <p>
	 * Title: creditSearch
	 * </p>
	 * <p>
	 * Description: 查询信用
	 * </p>
	 * 
	 * @param start
	 *            开始
	 * @param limit
	 *            结束
	 * @param loansignbasics
	 *            借款标基础信息表
	 * @param request
	 *            请求的request
	 * @return 结果 JSONObject
	 */
	@ResponseBody
	@RequestMapping("creditSearch")
	public JSONObject creditSearch(String chnlId, String name,
			String idType, String idNo, String mobileNo, String cardNo, String reasonNo, HttpServletRequest request) {

		JSONObject resultjson = new JSONObject();
		
		try {
//			postHttpRequest(chnlId, name, idType, idNo, mobileNo, cardNo, reasonNo);
			resultjson = postHttpsRequest(chnlId, getBusiData("blachlist", name, idType, idNo, mobileNo, cardNo, reasonNo), ParameterPaic.getUrlBlachlist(), request);

			JSONArray jsonRecords = JSONArray.fromObject(resultjson.get("records"));
			JSONObject jsonRecord = JSONObject.fromObject(jsonRecords.get(0));
			if("000000".equals(jsonRecord.get("erCode"))){
				return jsonRecord;
			}
			resultjson = postHttpsRequest(chnlId, getBusiData("credoo", name, idType, idNo, mobileNo, cardNo, reasonNo), ParameterPaic.getUrlCredoo(), request);
			jsonRecords = JSONArray.fromObject(resultjson.get("records"));
			jsonRecord = JSONObject.fromObject(jsonRecords.get(0));
			return jsonRecord;
			
//			System.out.println(resultjson);
//			System.out.println(jsonRecords);
//			System.out.println(jsonRecord);
//			
//			System.out.println(jsonRecord.get("erCode"));
		} catch (Exception e) {
			resultjson = new JSONObject();
			resultjson.element("status", "9999");
			e.printStackTrace();
		}

		return resultjson;
	}
	
    /**
     * 发送HTTPs请求,注意这里我们信任了任何服务器证书
     * 
     * @throws Exception
     */
    private static JSONObject postHttpsRequest(String chnlId, String sbBusiData, String url, HttpServletRequest request) throws Exception
    {
    	String realPath = request.getSession().getServletContext().getRealPath("");
    	
    	 StringBuffer sbHeader = new StringBuffer();
    	 sbHeader.append("{\"orgCode\":\"");
    	 //机构代码
    	 sbHeader.append(ParameterPaic.getOrgCode());
    	 sbHeader.append("\",\"chnlId\":\"");
    	 //接入系统ID
    	 sbHeader.append(chnlId);
    	 sbHeader.append("\",\"transNo\":\"");
    	 sbHeader.append(StringUtil.pMerBillNo());
    	 sbHeader.append("\",\"transDate\":\"");
    	 sbHeader.append(DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
    	 sbHeader.append("\",\"authCode\":\"");
    	 //授权码
    	 sbHeader.append(ParameterPaic.getAuthCode());
    	 sbHeader.append("\",\"authDate\":\"");
    	 sbHeader.append("2015-12-02 14:12:14");
    	 sbHeader.append("\"}");
    	 
        String header = "\"header\":" + sbHeader.toString();
        //校验码(正式需要修改)
        String encBusiData = DataSecurityUtil.encrypt(sbBusiData.getBytes(),
        		ParameterPaic.getCheckKey());
        String busiData = "\"busiData\":\"" + encBusiData + "\"";
        String sigValue = DataSecurityUtil.signData(encBusiData, realPath);
        //密码
        String pwd = DataSecurityUtil.digest(ParameterPaic.getUserPassword().getBytes());
        

	   	 StringBuffer sb = new StringBuffer();
	   	 sb.append("{\"signatureValue\":\"");
	   	 sb.append(sigValue);
	   	 sb.append("\",\"userName\":\"");
	   	 //用户名
	   	 sb.append(ParameterPaic.getUserName());
	   	 sb.append("\",\"userPassword\":\"");
	   	 sb.append(pwd);
	   	 sb.append("\"}");
        
//	   	 System.out.println(getBusiData("blachlist", name, idType, idNo, mobileNo, cardNo, reasonNo).toString());
        String securityInfo = "\"securityInfo\":" + sb.toString();
        String message = "{" + header + "," + busiData + "," + securityInfo + "}";
//        System.out.println("请求：" + message);

        String res = HttpRequestUtil.sendJsonWithHttps(url, message);

//        System.out.println("响应Message：" + res);
        JSONObject msgJSON = net.sf.json.JSONObject.fromObject(res);
//        System.out.println("响应BusiData密文：" + msgJSON.getString("busiData"));

        // 一定要验证签名的合法性！！！！！！！！
        DataSecurityUtil.verifyData(msgJSON.getString("busiData"), msgJSON.getJSONObject("securityInfo").getString(
                "signatureValue"), realPath);

//        System.out.println("响应BusiData明文："
//                + DataSecurityUtil.decrypt(msgJSON.getString("busiData"), "SK803@!QLF-D25WEDA5E52DA"));
        
        String strBusiData = DataSecurityUtil.decrypt(msgJSON.getString("busiData"), ParameterPaic.getCheckKey());
        JSONObject jsonBusiData = JSONObject.fromObject(strBusiData);
		return jsonBusiData;
    }

	public static String getBusiData(String type, String name, String idType, String idNo, String mobileNo, String cardNo, String reasonNo) {
		StringBuffer sbBusiData = new StringBuffer();
		if("credoo".equals(type)){
			sbBusiData.append("{\"batchNo\":\"");
			sbBusiData.append(StringUtil.pMerBillNo());
			sbBusiData.append("\",\"records\":[{");
			sbBusiData.append("\"idNo\":\"");
			sbBusiData.append(idNo);
			sbBusiData.append("\",\"idType\":\"");
			sbBusiData.append(idType);
			sbBusiData.append("\",\"name\":\"");
			sbBusiData.append(name);
			sbBusiData.append("\",\"mobileNo\":\"");
			sbBusiData.append(mobileNo);
			sbBusiData.append("\",\"cardNo\":\"");
			sbBusiData.append(cardNo);
			sbBusiData.append("\",\"reasonNo\":\"");
			sbBusiData.append(reasonNo);
			sbBusiData.append("\",\"seqNo\":\"");
			sbBusiData.append(StringUtil.pMerBillNo());
			sbBusiData.append("\"}]}");
		}else{
			sbBusiData.append("{\"batchNo\":\"");
			sbBusiData.append(StringUtil.pMerBillNo());
			sbBusiData.append("\",\"records\":[{");
			sbBusiData.append("\"idNo\":\"");
			sbBusiData.append(idNo);
			sbBusiData.append("\",\"idType\":\"");
			sbBusiData.append(idType);
			sbBusiData.append("\",\"reasonCode\":\"");
			sbBusiData.append(reasonNo);
			sbBusiData.append("\",\"name\":\"");
			sbBusiData.append(name);
			sbBusiData.append("\",\"seqNo\":\"");
			sbBusiData.append(StringUtil.pMerBillNo());
			sbBusiData.append("\"}]}");
		}
		return sbBusiData.toString();
	}
    
}
