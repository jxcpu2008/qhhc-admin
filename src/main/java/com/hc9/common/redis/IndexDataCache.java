package com.hc9.common.redis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.hc9.common.json.JsonUtil;
import com.hc9.common.log.LOG;

/**
 * 首页数据缓存
 * @author Administrator
 *
 */
public class IndexDataCache {
    
	/**
	 * 对象存放到缓存
	 * @param key
	 * @param value
	 */
	public static <T> void set(String key, T value){
		List<T> list = new ArrayList<T>();
		list.add(value);
		RedisHelper.set(key.getBytes(), serialize(list));
	}
	
	/**
	 * 获取缓存中对象
	 * @param key 类似 "LIST:HC9:INDEX:ARTICLE";
	 * @param <T>
	 * @return
	 */
	public static <T> T getObject(String key) {
		byte[] bytes = null;
		boolean hasData = false;
		if(RedisHelper.isKeyExist(key)) {
			hasData = true;
		}
		if(hasData) {
			bytes = RedisHelper.get(key.getBytes());
		}
		if(null != bytes && 0 < bytes.length) {
			return (T)deserialize(bytes).get(0);
		}
		return null;
	}
	
	/**
	 * 获取缓存中list对象
	 * @param key 类似 "LIST:HC9:INDEX:ARTICLE";
	 * @param <T>
	 * @return
	 */
	public static <T> List<T> getList(String key) {
		
		List<T> list=null;
		byte[] bytes=null;
		boolean hasData=false;
		if(RedisHelper.isKeyExist(key)){
			hasData=true;
		}
		if(hasData){
			bytes=RedisHelper.get(key.getBytes());
		}
		if(null!=bytes && 0<bytes.length){
			list=deserialize(bytes);
			return list;
		}
		return list;
	} 
	
	/**
	 * 使用json序列化
	 * @param key
	 * @param clazz
	 * @return
	 */
	public static <T> List<T> getList(String key, Class<T> clazz) {
		
		List<T> list=null;
		String value = null;
		boolean hasData=false;
		if(RedisHelper.isKeyExist(key)){
			hasData=true;
		}
		if(hasData){
			value = RedisHelper.get(key);
		}
		if(null != value && value.length() > 0){
			list = JsonUtil.jsonToList(value, clazz);
			return list;
		}
		return list;
	}
	

	/**
	 * 对象存放到缓存
	 * @param key
	 * @param value
	 */
	public static <T> void set(String key,List<T> value){
		RedisHelper.set(key.getBytes(),serialize(value));
	}
	
	/**
	 * 序列化
	 * @param value
	 * @return
	 */
	public static <T> byte[] serialize(List<T> value) {  
        if (value == null) {  
            throw new NullPointerException("参数不能为空");  
        }  
        byte[] rv=null;  
        ByteArrayOutputStream bos = null;  
        ObjectOutputStream os = null;  
        try {  
            bos = new ByteArrayOutputStream();  
            os = new ObjectOutputStream(bos);  
            for(T b : value){  
                os.writeObject(b);  
            }  
            os.writeObject(null);  
            os.close();  
            bos.close();  
            rv = bos.toByteArray();  
        } catch (IOException e) {  
            throw new IllegalArgumentException("无法序列化对象：", e);  
        } finally {  
        	close(os);
        	close(bos);
        }  
        return rv;  
    }
	
	/**
	 * 反序列化
	 * @param <T>
	 * @param in
	 * @return
	 */
    public static <T> List<T> deserialize(byte[] in) {  
        List<T> list = new ArrayList<T>();  
        ByteArrayInputStream bis = null;  
        ObjectInputStream is = null;  
        try {  
            if(in != null) {  
                bis=new ByteArrayInputStream(in);  
                is=new ObjectInputStream(bis);  
                while (true) {  
                	Object cls = (Object) is.readObject();  
                    if(cls == null){  
                        break;  
                    }else{  
                        list.add((T) cls);  
                    }  
                }  
                is.close();  
                bis.close();  
            }  
        } catch (IOException e) {  
        	LOG.error("更新缓存过程中报错！", e);
        } catch (ClassNotFoundException e) {  
        	LOG.error("更新缓存过程中报错！", e);
        } finally {  
            close(is);  
            close(bis);  
        }  
        return list;  
    }
    
    /**
     * 关闭流
     * @param closeable
     */
    public static void close(Closeable closeable) {  
        if (closeable != null) {  
            try {  
                closeable.close();  
            } catch (Exception e) {  
            	LOG.error("关闭失败"+e);
            }  
        }  
    }

}
