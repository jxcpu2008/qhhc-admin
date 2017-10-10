package com.hc9.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.dao.entity.Smsswitch;
import com.hc9.dao.impl.HibernateSupport;

@Service
public class SmsSwitchService {
	
	@Resource
    private HibernateSupport dao;
	public Smsswitch getSwitch() {
		String sql="SELECT * FROM smsswitch WHERE id=?";
		Smsswitch smsswitch=dao.findObjectBySql(sql, Smsswitch.class, 1);
		return smsswitch;
	}
	
	public void saveSwitch(Smsswitch smsswitch) {
		String sql="update smsswitch set triger=?,marketing=? WHERE id=? ";
		dao.executeSql(sql,smsswitch.getTriger(),smsswitch.getMarketing(),smsswitch.getId());
	}

}
