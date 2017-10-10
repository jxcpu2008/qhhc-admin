package com.hc9.common.filter;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hc9.common.constant.Constant;
import com.hc9.common.redis.RedisHelper;
import com.hc9.common.util.DateFormatUtil;
import com.hc9.common.util.StringUtil;
import com.hc9.commons.log.LOG;
import com.hc9.dao.entity.Userbasicsinfo;

/** 渠道推广相关过滤器 */
public class SpreadLinkFilter implements Filter {
	
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		LOG.info("--->启动渠道推广过滤器...");
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;  
		HttpServletResponse response = (HttpServletResponse) res; 
		
		/** 处理恶意访问请求 */
		if(isBlackIpAndFirstRequestHandle(request)) {
			response.sendRedirect("");
		} else {
			/** 推广码id  */
			String channelSpreadId = request.getParameter("channelSpreadId");
			if(channelSpreadId != null && channelSpreadId.trim().length() > 0) {
				String uri = null;
				String queryString = request.getQueryString();
				/** url查询参数对应的正则表达式 */
				String regExpr = "[-a-zA-Z0-9+&@#/%=~_:^.{}%|]*channelSpreadId=[-a-zA-Z0-9+&@#/%=~_:^.{}%|]*";
				Pattern pattern = Pattern.compile(regExpr);
				Matcher matcher = pattern.matcher(queryString);
				boolean matchResult = matcher.matches();
				if(matchResult) {
//					uri = handleUrl(uri, queryString, channelSpreadId);
					String sessionId = request.getSession().getId();
					/** 来自同一个sessionId的记录一分钟内只记录一次 */
					String key = "RCS:HC9:CHANNEL:RECORD:" + sessionId;
					String url=null;
					if(!RedisHelper.isKeyExistSetWithExpire(key, 60)) {
//						C3p0DatasourceUtil.saveChannelVisitLog(channelSpreadId, sessionId);
						/** 写入cookie，有效时间30天只记录最后一次从渠道来的信息 */
						url=handleChannelSpreadCookie(request, response, channelSpreadId,sessionId); 
					}
					request.getSession().setAttribute("channel", "channel");

					if(url!=null){
						uri=url;
					}
				}
				if(uri!=null){
					response.sendRedirect(uri);
				}else{
					chain.doFilter(request, response);
				}
				
			} else {
				chain.doFilter(request, response);
			}
		}
	}

	@Override
	public void destroy() {

	}
	
	/**
	 * 生成cookie
	 * @param cookie 
	 * @param name 名
	 * @param value 值
	 * @return
	 */
	private Cookie generateCookie(String name,String value){
		Cookie cookie=new Cookie(name,value);
		cookie.setPath("/");
		cookie.setMaxAge(30*24*3600);	
		return cookie;
	}
	/**
	 * 拼接cookie的value
	 * value记录规则 -- 渠道id(后台生成)[:渠道参数]:sessionId
	 * @param map
	 * @param channelSpreadId
	 * @param sessionId
	 * @return
	 */
	private String generateValue(Map<String,String[]> map, String channelSpreadId, String sessionId){
		String queryValues[];
		String value=channelSpreadId;
		for(String key:map.keySet()){
			if("channelSpreadId".equals(key)){
				continue;
			}
			queryValues=map.get(key);
			for(int i=0;i<queryValues.length;i++){
				value+="@_@"+key+"="+""+queryValues[i];
			}
		}
		value += "@_@"+sessionId;
		return value;
	}
	/**
	 * 处理渠道推广cookie相关 
	 * @param request
	 * @param response
	 * @param channelSpreadId
	 * @param sessionId
	 * @return 跳转目标
	 */
	private String handleChannelSpreadCookie(HttpServletRequest request, 
			HttpServletResponse response, String channelSpreadId,String sessionId){
		String url=null;
		Map<String,String[]> map=request.getParameterMap();

		String value;
		String name="hc9";
		//不管有无cookie，简单粗暴的直接覆盖
		value=this.generateValue(map,channelSpreadId,sessionId);
		Cookie cookie=this.generateCookie(name, value);
		response.addCookie(cookie);
		//判断是否有跳转的参数
		if(value.contains("http")){
			String splits[]=value.split("@_@");
			for(int i=0;i<splits.length;i++){
				if(splits[i].contains("www")){
					url=splits[i].substring(splits[i].indexOf("=")+1, splits[i].length());
				}
			}
		}
		return url;
	}
	/** 处理渠道推广cookie相关 */
	
	/** 处理url中的推广参数，尽量防止刷新浏览器造成的影响 */
	private static String handleUrl(String uri, String queryString, String channelSpreadId) {
		String resultStr = "";
		/** 为第一个参数 */
		if(queryString.startsWith("channelSpreadId=")) {
			int beginIndex = "channelSpreadId=".length() + channelSpreadId.length();
			resultStr = queryString.substring(beginIndex, queryString.length());
			if(resultStr != null && resultStr.trim().length() > 0 && resultStr.startsWith("&")) {
				resultStr = resultStr.substring(1, resultStr.length());
			}
		}
		/** 
		 * 为中间参数 */
		if(queryString.contains("&channelSpreadId=")) {
			int endIndex = queryString.indexOf("&channelSpreadId=");
			String firstString = queryString.substring(0, endIndex);
			int beginIndex = firstString.length() + "&channelSpreadId=".length() + channelSpreadId.length();
			String endString = queryString.substring(beginIndex, queryString.length());
			resultStr = firstString + endString;
		}
		if(resultStr != null && resultStr.length() > 0) {
			uri = uri + "?" + resultStr;
		}
		return uri;
	}
	
	/** 频繁访问请求日志记录及处理 */
	private boolean isBlackIpAndFirstRequestHandle(HttpServletRequest request) {
		boolean blackIpFlag = false;
		String ip = request.getRemoteAddr();
		if(StringUtil.isNotBlank(ip)) {
			/** ip黑名单标识 */
			String key = "STR:HC9:IP:BLACK:FLAG:" + ip;
			if(!RedisHelper.isKeyExist(key)) {
				String sessionId = request.getSession().getId();
				String visitDate = DateFormatUtil.dateToString(new Date(), "yyyy-MM-dd");
				key = "STR:HC9:FIRST:REQUEST:" + sessionId + ":" + visitDate;
				if(!RedisHelper.isKeyExistSetWithExpire(key, 60 * 60 * 25)) {
					String uri = request.getRequestURI();
					String queryString = request.getQueryString();
					String port = "" + request.getRemotePort();
					Long userId = null;
					Userbasicsinfo userbasic = (Userbasicsinfo) request.getSession().
							getAttribute(Constant.SESSION_USER);
					if(userbasic != null) {
						userId = userbasic.getId();
					}
					//不写入数据
					/*C3p0DatasourceUtil.saveFirstRequestOfSessionId(sessionId, uri, queryString, 
							ip, port, visitDate);*/
				}
			} else {
				blackIpFlag = true;
			}
		}
		return blackIpFlag;
	}
}