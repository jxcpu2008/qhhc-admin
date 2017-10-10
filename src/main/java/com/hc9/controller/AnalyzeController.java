package com.hc9.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.model.RechargeModel;
import com.hc9.service.AnalyzeService;
import com.hc9.service.LocalService;

/**
 * 数据分析控制器
 * @author frank
 *
 */
@Controller
@RequestMapping(value ="/analyze")
@CheckLogin(value=CheckLogin.ADMIN)
public class AnalyzeController {
	@Resource
	private AnalyzeService analyzeService;
	@Resource
	private RechargeModel rechargeModel;
	
	@Resource
	private LocalService localService;
	/**
	 * 获取某条标投资者的信息
	 * @return
	 */
	@RequestMapping("investorInfo.htm")
	public String getInvestorInfo(HttpServletRequest request,HttpServletResponse response,Long loanid){
		List results=analyzeService.getInvestorInfo(loanid);
		List<Map<String, String>> content = new ArrayList<>();
		String loanName=analyzeService.getLoanName(loanid);
		if(results!=null){
			for(int i=0; i<results.size();i++){
				Object[] objects=(Object[]) results.get(i);//[0]姓名[1]投资金额[2]投资时间[3]投资类型[4]标名[5]ip[6]大致地址
				Map<String, String> map=new HashMap<String, String>();
				map.put("投资者姓名", objects[0].toString());
				map.put("投资金额", objects[1].toString());
				map.put("投资时间", objects[2].toString());
				map.put("投资类型", getType(Integer.parseInt(objects[3].toString())));
				map.put("标名称", loanName);
				map.put("投资者IP", objects[4].toString());
				map.put("大致地址", objects[5].toString());
				content.add(map);
			}
			String title="标号"+loanid+"数据文件";
			String[] header={"投资者姓名","投资金额","投资时间","投资类型","标名称","投资者IP","大致地址"};
			Integer[] column={20,20,30,10,40,20,20};
			rechargeModel.downloadExcel(title, column, header, content, response);
		}

		return null;
	}
	
	/**
	 * 返回投资的类型
	 * @param i
	 * @return
	 */
	private String getType(int i){
		String type="";
		switch (i){
			case 1:
				type="优先";
				break;
			case 2:
				type="夹层";
				break;
			case 3:
				type="劣后";
				break;	
		}
		return type;
	}
}
