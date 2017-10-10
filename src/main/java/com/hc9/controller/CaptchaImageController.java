package com.hc9.controller;

import java.awt.Color;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hc9.common.constant.Constant;
import com.hc9.common.redis.RedisHelper;
import com.hc9.common.redis.RedisUtil;
import com.hc9.common.util.DateFormatUtil;
import com.hc9.common.util.GenerateLinkUtils;
import com.hc9.common.util.QRcodeUtil;
import com.hc9.common.util.RadarChartUtils;
import com.hc9.common.util.StringUtil;
import com.hc9.commons.image.SecurityImageTool;
import com.hc9.commons.log.LOG;
import com.hc9.commons.normal.RandomUtils;
import com.hc9.dao.entity.Userbasicsinfo;

/**
 * 验证码生成控制器
 * 
 * @author frank
 */
@RequestMapping("cic")
@Controller
public class CaptchaImageController {
    /** 不缓存 */
    private static final String NO_CACHE = "No-cache";
    
    /**
     * 得到一个校验码
     * @param name      校验码保存键值
     * @param request   请求
     * @param response  响应
     */
    @RequestMapping("/code")
    public void getCode(String name, HttpServletRequest request, HttpServletResponse response) {
        if (null==name){
            return;
        }
        
        boolean genFlag = false;
        String userInfo = "新用户注册:";
        Userbasicsinfo userbasic = (Userbasicsinfo) request.getSession().getAttribute(Constant.SESSION_USER);
        if(userbasic != null) {
        	userInfo = "用户id:" + userbasic.getId() + ",用户名：" + userbasic.getName();
        	genFlag = true;
        }
        String sessionId = request.getSession().getId();
        
        Date nowTime = new Date();
        String numKey = "INT:HC9:Code:GEN:NUM:" + sessionId;
        String dateKey = "INT:HC9:Code:GEN:DATE:" + sessionId;
        String lastGenCodeDateStr = RedisHelper.get(dateKey);
        if(StringUtil.isBlank(lastGenCodeDateStr)) {
        	lastGenCodeDateStr = DateFormatUtil.dateToString(nowTime, "yyyy-MM-dd HH:mm:ss");
        }
        Date lastGenCodeDate = DateFormatUtil.stringToDate(lastGenCodeDateStr, "yyyy-MM-dd HH:mm:ss");
        if(nowTime.getTime() - lastGenCodeDate.getTime() <= 1 * 60 * 60 * 1000) {
            String sessionIdCodeNum = RedisHelper.get(numKey);
            if(StringUtil.isBlank(sessionIdCodeNum)) {
            	sessionIdCodeNum = "0";
            	RedisHelper.set(numKey, "0");
            }
            int sessionIdGenCodeNum = Integer.valueOf(sessionIdCodeNum);
            if(sessionIdGenCodeNum < RedisUtil.getSystemUpperLimit()) {
            	genFlag = true;
            }
        } else {
        	RedisHelper.set(numKey, "0");
        }
        if(genFlag) {
        	String code = RandomUtils.getNumberString(4);
			String queryString = request.getQueryString();
            LOG.info(userInfo + " 产生一个验证码[" + code + "],保存于[" + name 
            		+ "], sessionId=" + sessionId + ",ip=" + request.getRemoteAddr() 
            		+ ",port=" + request.getRemotePort()
            		+ ",queryString:" + queryString);
            RedisHelper.incrBy(numKey, 1);
            RedisHelper.set(dateKey, DateFormatUtil.dateToString(nowTime, "yyyy-MM-dd HH:mm:ss"));

            response.setHeader("Pragma",NO_CACHE);// 禁止缓存
            response.setHeader("Cache-Control",NO_CACHE);
            response.setDateHeader("Expires", 0);
            response.setContentType("image/jpeg");// 指定生成的响应是图片
            request.getSession().setAttribute(name, code);
            try {
                ImageIO.write(SecurityImageTool.createImage(code), "JPEG",response.getOutputStream());
                response.getOutputStream().close();
                LOG.error("图形码："+code);
            } catch (IOException e) {
                LOG.error("生成验证图片失败！", e);
            }
        }
    }

    /** 获得二维码 */
    @RequestMapping("/QRcode")
    public void getQRcode(HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException{
    	String link=(String) request.getSession().getAttribute("myPromoteLikn");
    	int width=140;
    	int height=140;
    	if(null==link){
    		return;
    	}

        response.setHeader("Pragma",NO_CACHE);// 禁止缓存
        response.setHeader("Cache-Control",NO_CACHE);
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");// 指定生成的响应是图片
        try {
			ImageIO.write(QRcodeUtil.createImage(link,width,height), "JPEG",response.getOutputStream());
		} catch (UnsupportedEncodingException e) {
			LOG.error("编码格式错误", e);
			e.printStackTrace();
		} catch (IOException e) {
			LOG.error("生成验证图片失败！", e);
			e.printStackTrace();
		}
    }

    /** 获得H5二维码 */
    @RequestMapping("/QRcodeH5")
    public void getQRcodeH5(HttpServletRequest request,HttpServletResponse response,String str) throws UnsupportedEncodingException{
    	String link=GenerateLinkUtils.getServiceHostnew(request) + "h5/h5regist.htm?member=" + str;
    	int width=140;
    	int height=140;
    	if(null==link){
    		return;
    	}

        response.setHeader("Pragma",NO_CACHE);// 禁止缓存
        response.setHeader("Cache-Control",NO_CACHE);
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");// 指定生成的响应是图片
        try {
			ImageIO.write(QRcodeUtil.createImage(link,width,height), "JPEG",response.getOutputStream());
		} catch (UnsupportedEncodingException e) {
			LOG.error("编码格式错误", e);
			e.printStackTrace();
		} catch (IOException e) {
			LOG.error("生成验证图片失败！", e);
			e.printStackTrace();
		}
    }
    
    @RequestMapping("/radar")
    public void getRadarChart(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	List<float[]> list=new ArrayList<float[]>();
    	float[] fl0={Float.parseFloat("7"),Float.parseFloat("4"),Float.parseFloat("9"),Float.parseFloat("5")};
    	list.add(fl0);
    	List<Color> colors=new ArrayList<Color>();
    	colors.add(Color.RED);
    	String[] str={"企业基本资料","法人背景征信","还款能力","企业经营状况"};
    	List<String> names=new ArrayList<String>();
    	names.add("信用评级");
    	RadarChartUtils radarChart=new RadarChartUtils("1", str, list, colors, names);
    	response.setHeader("Pragma",NO_CACHE);// 禁止缓存
    	response.setHeader("Cache-Control",NO_CACHE);
    	response.setDateHeader("Expires", 0);
    	response.setContentType("image/jpeg");// 指定生成的响应是图片
    	ImageIO.write(radarChart.makeRadarChart(), "JPEG",response.getOutputStream());
    }
}
