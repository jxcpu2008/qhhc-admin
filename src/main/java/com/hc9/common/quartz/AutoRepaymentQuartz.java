package com.hc9.common.quartz;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;

import com.hc9.dao.entity.Repaymentrecord;
import com.hc9.service.AutoRepaymentService;

import freemarker.template.TemplateException;
/**
 * 定时自动还款
 * @author yu
 *
 */
public class AutoRepaymentQuartz {
	
	@Resource
	private AutoRepaymentService autoRepaymentService;
	/**
	 * 定时还款启动方法
	 */
	public void run(){
		List<Repaymentrecord> records=autoRepaymentService.getAutoRepaymentRecordList();
		for(int i=0;i<records.size();i++){
			autoRepaymentService.setRepaymentrecord(records.get(i));
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						autoRepaymentService.AutoRepayment();
					} catch (IOException | TemplateException e) {
						e.printStackTrace();
					}
					
				}
			}).start();
			//new Thread(autoRepaymentService).start();
		}
	} 
	
}
