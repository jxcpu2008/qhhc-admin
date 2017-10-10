package com.hc9.common.db;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import com.hc9.common.redis.RedisHelper;
import com.hc9.common.util.StringTools;

/** 待机密功能的属性编辑器 */
public class DecryptPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {   
	private String toDecryptPro;
	 
    @Override
    protected String convertProperty(String propertyName, String propertyValue) {
        if (isEncryptProp(propertyName)) {
        	String dbKey = "HC9:ENCRY:PASS:KEY:DB";
        	String password = RedisHelper.get(dbKey);
        	byte[] key = StringTools.hexStringToBytes(password);//加密密钥
        	byte[] data = StringTools.hexStringToBytes(propertyValue.trim());//待解密的数据
            byte[] decryptValue = DbPassUtil.decrypt(data, key);
            String resultValue = new String(decryptValue);
            return resultValue;
        } else {
            return propertyValue;
        }
    }
 
    /**
     * 判断是否是加密的属性
     * 
     * @param propertyName
     * @return
     */
    private boolean isEncryptProp(String propertyName) {
    	if(toDecryptPro != null) {
    		String[] encryptPropNames = toDecryptPro.split(",");
            for (String encryptpropertyName : encryptPropNames) {
                if (encryptpropertyName.equals(propertyName))
                    return true;
            }
    	}
    	
        return false;
    }

	public void setToDecryptPro(String toDecryptPro) {
		this.toDecryptPro = toDecryptPro;
	}
    
}