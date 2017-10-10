package com.hc9.common.quartz;

import java.util.List;

import javax.annotation.Resource;

import com.hc9.dao.entity.Shop;
import com.hc9.service.AutoShopPreheatService;
import com.hc9.service.SmsService;


/***
 * 预热结束定时任务(每天早上9点)
 * @author LKL
 *
 */
public class AutoShopPreheatQuartz {
	
	@Resource
	public AutoShopPreheatService shopPreheatService;
	
	@Resource
	public SmsService smsService;
	
	public Shop shop;
	
	/**
	 * 预热结束定时任务启动方法
	 */
	public void  run(){
		List<Shop>  listShop=shopPreheatService.getShopList();
		for (int i = 0; i < listShop.size(); i++) {
			    shop=listShop.get(i);
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							smsService.sendSMS("尊敬的【"+shop.getUserbasicsinfo().getUserName()+"】客户，您好：您在【前海红筹】众筹的【"+shop.getShopName()+"】店铺预热期今天已到期了，请您到【前海红筹】平台上进行处理，谢谢！【前海红筹】", shop.getUserbasicsinfo().getUserrelationinfo().getPhone());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).start();
		}
	}

}
