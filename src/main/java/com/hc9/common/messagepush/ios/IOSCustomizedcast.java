package com.hc9.common.messagepush.ios;

import com.hc9.common.messagepush.IOSNotification;

public class IOSCustomizedcast extends IOSNotification {
	public IOSCustomizedcast(String appkey, String appMasterSecret) throws Exception {
			setAppMasterSecret(appMasterSecret);
			setPredefinedKeyValue("appkey", appkey);
			setPredefinedKeyValue("type", "customizedcast");	
	}
	
	public void setAlias(String alias,String aliasType) throws Exception {
    	setPredefinedKeyValue("alias", alias);
    	setPredefinedKeyValue("alias_type", aliasType);
    }
		
	public void setFileId(String fileId, String aliasType) throws Exception {
		setPredefinedKeyValue("file_id", fileId);
		setPredefinedKeyValue("alias_type", aliasType);
	}

}
