package com.hc9.common.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Base64;

import com.hc9.common.log.LOG;

/**
 * 谷歌2步认证服务端工具
 * 
 * @author Administrator
 * 
 */
public class GoogleAuthenticator {
	/*
	 * 这些变量默认就可
	 */
	public static final int SECRET_SIZE = 10;

	public static final String SEED = "g8GjEvTbW5oVSV7avLBdwIHqGlUYNzKFI7izOF8GwLDVKs2m0QN7vxRs2im5MDaNCWGmcD2rvcZx";

	public static final String RANDOM_NUMBER_ALGORITHM = "SHA1PRNG";

	int window_size = 3; // default 3 - max 17 (from google docs)最多可偏移的时间
	
    public void setWindowSize(int s) {  
        if (s >= 1 && s <= 17)  
            window_size = s;  
    } 
    /**
     * 校验方法。注意过期时间
     * @param codes app端获取的code
     * @param savedSecret 用户对应的secretCode
     * @return
     */
    public static Boolean authcode(String codes, String secretKey) {  
        
        long code = Long.parseLong(codes);  
        long t = System.currentTimeMillis();  
        GoogleAuthenticator ga = new GoogleAuthenticator();  
        ga.setWindowSize(15); // should give 5 * 30 seconds of grace...  
        boolean r = ga.checkCode(secretKey, code, t);  
        return r;  
    } 
    /**
     * 生成SECRET_KEY
     * @param userName 用户名
     * @param host 主机、域名都可以
     * @return
     */
    public static String genSecretKey(String userName,String host) {  
        String secretKey = GoogleAuthenticator.generateSecretKey();  
		GoogleAuthenticator.getQRBarcodeURL(userName, host, secretKey);  
        return secretKey;  
    } 
    
    /**
     * 生成SECRET_KEY
     * @return
     */
    public static String generateSecretKey() {  
        SecureRandom sr = null;  
        try {  
            sr = SecureRandom.getInstance(RANDOM_NUMBER_ALGORITHM);  
            sr.setSeed(Base64.decodeBase64(SEED));  
            byte[] buffer = sr.generateSeed(SECRET_SIZE);  
            Base32 codec = new Base32();  
            byte[] bEncodedKey = codec.encode(buffer);  
            String encodedKey = new String(bEncodedKey);  
            return encodedKey;  
        }catch (NoSuchAlgorithmException e) {  
            LOG.error("--->>SECRET_KEY生成出错"+e);  
        }  
        return null;  
    }
    /**
     * 生成二维码的扫描地址,附带注册了用户信息
     * @param user
     * @param host
     * @param secret
     * @return
     */
    public static String getQRBarcodeURL(String userName, String host, String secretKey) {  
        String format = "https://www.google.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=otpauth://totp/%s@%s%%3Fsecret%%3D%s";  
        return String.format(format, userName, host, secretKey);  
    }  
    
    /**
     * 校验
     * @param secretKey 注册的secretKey
     * @param code app获取的code
     * @param timeMsec 过期时间
     * @return
     */
    public boolean checkCode(String secretKey, long code, long timeMsec) {  
        Base32 codec = new Base32();  
        byte[] decodedKey = codec.decode(secretKey);  
        long t = (timeMsec / 1000L) / 30L;  
        for (int i = -window_size; i <= window_size; ++i) {  
            long hash;  
            try {  
                hash = verify_code(decodedKey, t + i);  
            }catch (Exception e) {  
                LOG.error("--->>谷歌2步校验失败"+e);  
                throw new RuntimeException(e.getMessage());  
            }  
            //校验成功
            if (hash == code) {  
                return true;  
            }  
        }  
        // 校验失败
        return false;  
    }
    
    
    private static int verify_code(byte[] key, long t) throws NoSuchAlgorithmException, InvalidKeyException {  
        byte[] data = new byte[8];  
        long value = t;  
        for (int i = 8; i-- > 0; value >>>= 8) {  
            data[i] = (byte) value;  
        }  
        SecretKeySpec signKey = new SecretKeySpec(key, "HmacSHA1");  
        Mac mac = Mac.getInstance("HmacSHA1");  
        mac.init(signKey);  
        byte[] hash = mac.doFinal(data);  
        int offset = hash[20 - 1] & 0xF;  
        // We're using a long because Java hasn't got unsigned int.  
        long truncatedHash = 0;  
        for (int i = 0; i < 4; ++i) {  
            truncatedHash <<= 8;  
            // We are dealing with signed bytes:  
            // we just keep the first byte.  
            truncatedHash |= (hash[offset + i] & 0xFF);  
        }  
        truncatedHash &= 0x7FFFFFFF;  
        truncatedHash %= 1000000;  
        return (int) truncatedHash;  
    } 
}
