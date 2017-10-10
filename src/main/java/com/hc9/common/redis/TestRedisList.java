package com.hc9.common.redis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.hc9.dao.entity.Banner;

public class TestRedisList {

	private static JedisPool jedispool;

	public static Jedis getJedis() {
		JedisPoolConfig jedisconfig = new JedisPoolConfig();
		jedisconfig.setMaxActive(64);
		jedisconfig.setMaxIdle(8);
		jedisconfig.setMaxWait(1000);
		jedisconfig.setTestOnBorrow(true);
		jedisconfig.setTestOnReturn(true);
		jedispool = new JedisPool(jedisconfig, "127.0.0.1",6379,2000,"123456");
		return (Jedis)jedispool.getResource();
	}
	public static List<Banner> getBannerList(){
		List<Banner> banners=new ArrayList<>();
		Banner b1=new Banner();
		b1.setId(1l);
		b1.setImgurl("/upload/banner/20150902085552740.JPG");
		b1.setNumber(1);
		b1.setUrl("/to/article-1-4-82.htm");
		b1.setPicturename("注册送100元红包");
		Banner b2=new Banner();
		b2.setId(2l);
		b2.setImgurl("/upload/banner/20150910155003197.JPG");
		b2.setNumber(2);
		b2.setUrl("http://www.dadabus.com/");
		b2.setPicturename("嗒嗒巴士");
		banners.add(b1);
		banners.add(b2);
		return banners;
	}
	
	/**
	 * 序列化
	 * @param value
	 * @return
	 */
	public static <T> byte[] serialize(List<T> value) {  
        if (value == null) {  
            throw new NullPointerException("Can't serialize null");  
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
            throw new IllegalArgumentException("Non-serializable object", e);  
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
        } catch (ClassNotFoundException e) {  
        } finally {  
            close(is);  
            close(bis);  
        }  
        return list;  
    }
	public static void testSetElements(){
		Jedis jedis=getJedis();
		String key="LIST:HC9:INDEX:BANNER";
		jedis.set(key.getBytes(),serialize(getBannerList()));
		byte[] in = jedis.get(key.getBytes());
		List<Banner> list = deserialize(in); 
		
		for(Banner user : list){  
            System.out.println("testSetEnsemble user name is:" + user.getImgurl());  
        }  
	}
    public static void close(Closeable closeable) {  
        if (closeable != null) {  
            try {  
                closeable.close();  
            } catch (Exception e) {  
            	e.printStackTrace(); 
            }  
        }  
    }  

}
