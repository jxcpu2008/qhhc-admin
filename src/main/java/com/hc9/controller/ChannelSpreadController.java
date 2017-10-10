package com.hc9.controller;

import java.util.List;
import java.util.Random;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.dao.entity.ChannelSpread;
import com.hc9.model.PageModel;
import com.hc9.service.ChannelSpreadService;

@Controller
@RequestMapping("/channelSpread")
@CheckLogin(value=CheckLogin.ADMIN)
public class ChannelSpreadController {

	@Resource
	private ChannelSpreadService channelService;

	/**
	 * 跳转到渠道管理
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/channelManage")
	public String channelManage(HttpServletRequest request, PageModel page,
			Integer no) {

		if (no == null) {
			page.setPageNum(1);
		} else {
			page.setPageNum(no);
		}
		page.setNumPerPage(5);
		page = channelService.queryChannel(page);
		List list = channelService.queryadvert();
		request.setAttribute("page", page);
		request.setAttribute("list", list);
		return "/WEB-INF/views/admin/channel/channelManage";
	}

	/**
	 * 跳转到广告位详情页
	 * 
	 * @param request
	 * @param page
	 * @param sId
	 * @param code
	 * @return
	 */
	@RequestMapping("/channelDetails")
	public String channelDetails(HttpServletRequest request, PageModel page,
			Integer sId, Integer no) {
		if (no == null) {
			page.setPageNum(1);
		} else {
			page.setPageNum(no);
		}
		page.setNumPerPage(10);
		page = channelService.queryadvList(page, sId);
		ChannelSpread chan = channelService.queryChannelById(sId);
		request.setAttribute("page", page);
		request.setAttribute("sId", sId);
		if (chan != null) {
			request.setAttribute("code", chan.getSpreadId());
			request.setAttribute("name", chan.getName());
		}
		return "/WEB-INF/views/admin/channel/chanelDetails";
	}

	/**
	 * 跳转到广告详情页
	 * 
	 * @param request
	 * @param page
	 * @param sId
	 * @param code
	 * @return
	 */
	@RequestMapping("/channelAdvDetails")
	public String channelAdvDetails(HttpServletRequest request, PageModel page,
			Integer sId, Integer no) {
		if (no == null) {
			page.setPageNum(1);
		} else {
			page.setPageNum(no);
		}
		page.setNumPerPage(10);
		page = channelService.queryadvvList(page, sId);
		ChannelSpread chan = channelService.queryChannelById(sId);
		if (chan != null) {
			request.setAttribute("code", chan.getSpreadId());
			request.setAttribute("name", chan.getName());
		}
		request.setAttribute("page", page);
		request.setAttribute("sId", sId);

		return "/WEB-INF/views/admin/channel/channelAdvDetails";
	}

	@RequestMapping("/saveChannel")
	@ResponseBody
	public String saveChannel(HttpServletRequest request,
			ChannelSpread channelSpread) {
		Random random = new Random();
		String cha = "";
		for (int i = 0; i < 8; i++) {
			int choice = random.nextInt(2) % 2 == 0 ? 65 : 97; // 取得大写还是小写
			cha += (char) (choice + random.nextInt(26));
		}
		channelSpread.setSpreadId(cha);
		channelService.saveChannel(channelSpread);
		return "1";
	}

	@RequestMapping("/updateChannel")
	@ResponseBody
	public String updateChannel(HttpServletRequest request,
			ChannelSpread channelSpread) {
		ChannelSpread channel = channelService.queryChannelById(channelSpread
				.getId());
		channel.setName(channelSpread.getName());
		channelService.saveChannel(channel);
		return "1";
	}
}
