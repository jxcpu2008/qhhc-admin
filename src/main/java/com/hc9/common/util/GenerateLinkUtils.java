package com.hc9.common.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletRequest;

import com.hc9.commons.normal.Md5Util;
import com.hc9.dao.entity.Userbasicsinfo;

/**
 * 链接工具
 * 
 * @author frank
 * 
 */
public class GenerateLinkUtils {

    /** CHECK_CODE*/
    private static final String CHECK_CODE = "checkCode";
    
    /**
     * 生成帐户激活链接
     * 
     * @param user
     *            用户
     * @param request
     *            请求
     * @return 链接
     */
    public static String generateActivateLink(Userbasicsinfo user,
            HttpServletRequest request) {
    	
    	return getServiceHostnew(request)+"registration/activateAccount?activationid="   
                + user.getId() + "&" + CHECK_CODE + "=" + generateCheckcode(user);  
    }
    
    public static String generateUptEmailLink(Userbasicsinfo user,
            HttpServletRequest request) {

        String str = StringUtil.generatePassword("" + user.getId());
        
        try {
            str = URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }

        return getServiceHostnew(request) + "member/forwardUptEmail?id="
                + str;
    }
    
    /**
     * 生成验证帐户的MD5校验码
     * 
     * @param user
     *            要激活的帐户
     * @return 将用户名和密码组合后，通过md5加密后的16进制格式的字符串
     */
    public static String generateCheckcode(Userbasicsinfo user) {
        return Md5Util.execute(user.getUserName() + ":"
                        + user.getRandomCode());
    }

    /**
     * 获取服务器的域名（包含端口号）
     * 
     * @param request   请求
     * @return          域名
     */
    public static String getServiceHostnew(HttpServletRequest request) {
    	
        String serverPort = null;

        if (request.getServerPort() != 80) {
            serverPort = ":" + request.getServerPort();
        }

        String basePath = request.getScheme() + "://" + request.getServerName()
                +  "/";
//        String basePath = Constant.WEBSERVER+request.getContextPath()+ "/";
//        String basePath = Constant.WEBSERVER+"/";
        return basePath;
    }
    
    /**
    * <p>Title: generateResetPwdLink</p>
    * <p>Description: 生成重设密码的链接 </p>
    * @param user  用户信息
    * @param request  请求
    * @return  链接url
    */
    public static String generateResetPwdLink(Userbasicsinfo user,HttpServletRequest request){
        
        return getServiceHostnew(request)+"find_password/checkresetpwdlink?userName="   
                +user.getId()+ "&" + CHECK_CODE + "=" + generateCheckcode(user);  
    }
    
    /** 
     * 验证校验码是否和注册时发送的验证码一致 
     * @param user 要激活的帐户 
     * @param request 请求
     * @return 如果一致返回true，否则返回false 
     */  
    public static boolean verifyCheckcode(Userbasicsinfo user,HttpServletRequest request) {  
        String checkCode = request.getParameter(CHECK_CODE); 
        String phone = Md5Util.execute(user.getUserName());
        if(generateCheckcode(user).equals(checkCode)||phone.equals(checkCode)){
        	return true;
        }else{return false;}
    }  
    
    /**
    * <p>Title: bytes2Hex</p>
    * <p>Description:  加密法</p>
    * @param byteArray 要加密的信息
    * @return 加密后的
    */
    private static String bytes2Hex(byte[] byteArray)  
    {  
        StringBuffer strBuf = new StringBuffer();  
        for (int i = 0; i < byteArray.length; i++)  
        {  
            if(byteArray[i] >= 0 && byteArray[i] < 16)  
            {  
                strBuf.append("0");  
            }  
            strBuf.append(Integer.toHexString(byteArray[i] & 0xFF));  
        }  
        return strBuf.toString();  
    } 
    
    /**
    * <p>Title: md5</p>
    * <p>Description: MD5加密 </p>
    * @param string  加密的字符串
    * @return  结果
    */
    public static String md5(String string) {  
        MessageDigest md = null;  
        try {  
            md = MessageDigest.getInstance("md5");  
            md.update(string.getBytes());  
            byte[] md5Bytes = md.digest();  
            return bytes2Hex(md5Bytes);  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        }  
          
        return null;  
    }
    /**
     * 生成一个分享链接
     * @param phone
     * @param request
     * @throws UnsupportedEncodingException
     */
	public static String generateShareLink(String phone,HttpServletRequest request) throws UnsupportedEncodingException {
		String str;
		str = StringUtil.generatePassword(phone);
		str = URLEncoder.encode(str, "UTF-8");
		request.setAttribute("strurl",getServiceHostnew(request) + "mobile/gatherFlowers.htm?user=" + str);
		return str;
	}  

}
