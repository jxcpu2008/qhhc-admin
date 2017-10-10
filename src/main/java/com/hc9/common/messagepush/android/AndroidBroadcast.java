package com.hc9.common.messagepush.android;

import com.hc9.common.messagepush.AndroidNotification;

public class AndroidBroadcast extends AndroidNotification {
	public AndroidBroadcast(String appkey,String appMasterSecret) throws Exception {
			
		setAppMasterSecret(appMasterSecret);
		setPredefinedKeyValue("appkey", appkey);
		setPredefinedKeyValue("type", "broadcast");	
	}
}