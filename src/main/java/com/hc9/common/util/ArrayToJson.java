package com.hc9.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**   
 * Filename:    ArrayToJson.java   
 * Company:     前海红筹  
 * @version:    3.0   
 * @since:  JDK 1.7.0_25  
 * Create at:   2014年2月11日 下午1:53:22   
 * Description:  这个工具类主要是将用sql查询出来的结果（数组）转换成json集合
 *   

 */

/**
 * <p>
 * Title:ArrayToJson
 * </p>
 * <p>
 * Description: 数组转json集合工具类
 * </p>
 * <p>
 * Company: 前海红筹
 * </p>

 */
public class ArrayToJson {

    /**
     * <p>
     * Title: arrayToJson
     * </p>
     * <p>
     * Description: 将数组对象转换成json集合
     * </p>
     * 
     * @param titles
     *            返回json对象的键，键与键之间用"<B><font color="red">,</font></B>"隔开 如
     *            id,username,realname......
     * @param datalist
     *            数据库查询出来的<B><font color="red">数组</font></B>结果
     * @param jsonlist
     *            存放数据的json集合
     */
    public static void arrayToJson(String titles, List datalist,
            JSONArray jsonlist) {

        // 判断查询结果是否为空
        if (null != datalist && !datalist.isEmpty()) {
            JSONObject json = null;
            // 根据","截取json对象的键
            String[] title = titles.split(",");
            int titleLength = title.length;

            // 循环赋值
            for (Object obj : datalist) {

                Object[] result = (Object[]) obj;

                json = new JSONObject();

                for (int i = 0; i < titleLength; i++) {
                    json.element(title[i], result[i]);
                }
                jsonlist.add(json);

            }
        }
    }
    
    /** 会员账号处理相关 */
    public static List<String> memberIdList(List datalist) {
    	List<String> memberIdList = new ArrayList<String>();
    	
        // 判断查询结果是否为空
        if (null != datalist && !datalist.isEmpty()) {
            // 循环赋值
            for (Object obj : datalist) {
                Object[] result = (Object[]) obj;
                String userId = StatisticsUtil.getStringFromObject(result[1]);
                memberIdList.add(userId);
            }
        }
        return memberIdList;
    }
    
    /** 处理会员中心相关数据 */
    public static void memberListToJson(String titles, List datalist,
            JSONArray jsonlist, Map<String, String> userChannelMap) {

        // 判断查询结果是否为空
        if (null != datalist && !datalist.isEmpty()) {
            JSONObject json = null;
            // 根据","截取json对象的键
            String[] title = titles.split(",");
            int titleLength = title.length;

            // 循环赋值
            for (Object obj : datalist) {

                Object[] result = (Object[]) obj;

                json = new JSONObject();
                String username = (String)result[1];
                for (int i = 0; i < titleLength; i++) {
                	if(i == (titleLength - 1)) {
                		String channeName = "";
                		if(userChannelMap != null && userChannelMap.size() > 0) {
                			channeName = userChannelMap.get(username);
                		}
                		json.element(title[i], channeName);
                	} else {
                		json.element(title[i], result[i]);
                	}
                }
                jsonlist.add(json);

            }
        }
    }
}
