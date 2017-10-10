package com.hc9.common.messagepush.ios;

import com.hc9.common.messagepush.IOSNotification;

public class IOSBroadcast extends IOSNotification {
	
	public IOSBroadcast(String appkey, String appMasterSecret) {
			
		setAppMasterSecret(appMasterSecret);
		setPredefinedKeyValue("appkey", appkey);
		setPredefinedKeyValue("type", "broadcast");	
	}
}