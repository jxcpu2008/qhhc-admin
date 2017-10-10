package com.hc9.common.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import redis.clients.jedis.Jedis;

import com.hc9.common.json.JsonUtil;
import com.hc9.common.log.LOG;

/** redsi客户端操作工具类 */
public class RedisHelper {

	/** 设置redis对应的key和value */
	public static String set(String key, String value) {
		Jedis jedis = null;
		try{
			jedis = RedisProvider.getJedis();
			String rtn = jedis.set(key, value);
			return rtn;
		} catch(Exception e) {
			LOG.error("set方法报错：key=" + key + ",value=" + value, e);
			throw new RuntimeException("set方法报错。");
		} finally {
			RedisProvider.returnJedis(jedis);
		}
	}
	
	public static String set(byte[] key, byte[] value) {
		Jedis jedis = null;
		try{
			jedis = RedisProvider.getJedis();
			String rtn = jedis.set(key, value);
			return rtn;
		} catch(Exception e) {
			LOG.error("set方法报错：key=" + key + ",value=" + value, e);
			throw new RuntimeException("set方法报错。");
		} finally {
			RedisProvider.returnJedis(jedis);
		}
	}

	/** 根据key从redis删除相关值 */
	public static void del(String key) {
		Jedis jedis = null;
		try{
			jedis = RedisProvider.getJedis();
			jedis.del(key);
		} catch(Exception e) {
			LOG.error("del方法报错：key=" + key, e);
		} finally {
			RedisProvider.returnJedis(jedis);
		}
	}
	
	/** 可以设置过期时间的set方法 */
	public static String setWithExpireTime(String key, String value, int seconds) {
		Jedis jedis = null;
		try{
			jedis = RedisProvider.getJedis();
			String rtn = jedis.set(key, value);
			jedis.expire(key, seconds);
			return rtn;
		} catch(Exception e) {
			LOG.error("setWithExpireTime方法报错：key=" + key + ",value=" + value, e);
			throw new RuntimeException("get方法报错。");
		} finally {
			RedisProvider.returnJedis(jedis);
		}
	}
	
	/** 根据key从redis获取相关值 */
	public static String get(String key) {
		Jedis jedis = null;
		try{
			jedis = RedisProvider.getJedis();
			String rtn = jedis.get(key);
			return rtn;
		} catch(Exception e) {
			LOG.error("get方法报错：key=" + key, e);
			throw new RuntimeException("get方法报错。");
		} finally {
			RedisProvider.returnJedis(jedis);
		}
	}

	/**
	 * 根据key从redis获取相关值
	 * @param key byte[]
	 * @return byte[]
	 */
	public static byte[] get(byte[] key) {
		Jedis jedis = null;
		try{
			jedis = RedisProvider.getJedis();
			byte[] rtn = jedis.get(key);
			return rtn;
		} catch(Exception e) {
			LOG.error("get方法报错：key=" + key, e);
			throw new RuntimeException("get方法报错。");
		} finally {
			RedisProvider.returnJedis(jedis);
		}
	}

	/** 往redis中设置map对象 */
	public static String hmset(String key, Map<String, String> hash) {
		Jedis jedis = null;
		try{
			jedis = RedisProvider.getJedis();
			String rtn = jedis.hmset(key, hash);
			return rtn;
		} catch(Exception e) {
			LOG.error("hmset方法报错：key=" + key, e);
			throw new RuntimeException("hmset方法报错。");
		} finally {
			RedisProvider.returnJedis(jedis);
		}
	}
	
	/** 从redis中获取map对象 */
	public static Map<String, String> hgetall(String key) {
		Jedis jedis = null;
		try{
			jedis = RedisProvider.getJedis();
			Map<String, String> rtn = jedis.hgetAll(key);
			return rtn;
		} catch(Exception e) {
			LOG.error("hgetall方法报错：key=" + key, e);
			throw new RuntimeException("hgetall方法报错。");
		} finally {
			RedisProvider.returnJedis(jedis);
		}
	}

	/** 往redis中设置List对象 */
	public static void setList(String key, List<String> list) {
		if(list != null) {
			Jedis jedis = null;
			try{
				jedis = RedisProvider.getJedis();
				jedis.del(key);
				for(String str : list) {
					jedis.rpush(key, str);
				}
			} catch(Exception e) {
				LOG.error("setList方法报错：key=" + key, e);
				throw new RuntimeException("setList方法报错。");
			} finally {
				RedisProvider.returnJedis(jedis);
			}
		}
	}
	
	/** 获取list列表 */
	public static <T> List<T> getList(String key, Class<T> clazz) {
		List<T> list = new ArrayList<T>();
		Jedis jedis = null;
		try{
			jedis = RedisProvider.getJedis();
			List<String> redisStrList = jedis.lrange(key, 0, -1);
			for(String str : redisStrList) {
				T rank = JsonUtil.jsonToObject(str, clazz);
				list.add(rank);
			}
		} catch(Exception e) {
			LOG.error("getList方法报错：key=" + key, e);
			throw new RuntimeException("getList方法报错。");
		} finally {
			RedisProvider.returnJedis(jedis);
		}
		return list;
	}
	
	/** 给特定key的值新增制定值，返回新增后该key的值 */
	public static long incrBy(String key, long value) {
		Jedis jedis = null;
		try{
			jedis = RedisProvider.getJedis();
			long rtn = jedis.incrBy(key, value);
			return rtn;
		} catch(Exception e) {
			LOG.error("incrBy方法报错：key=" + key + ",value=" + value, e);
			throw new RuntimeException("incrBy方法报错。");
		} finally {
			RedisProvider.returnJedis(jedis);
		}
	}

	/** 给特定key的值减少指定，返回修改后该key的值 */
	public static long decrBy(String key, long value) {
		Jedis jedis = null;
		try{
			jedis = RedisProvider.getJedis();
			long rtn = jedis.decrBy(key, value);
			return rtn;
		} catch(Exception e) {
			LOG.error("decrBy方法报错：key=" + key + ",value=" + value, e);
			throw new RuntimeException("decrBy方法报错。");
		} finally {
			RedisProvider.returnJedis(jedis);
		}
	}
	
	/** 判断key是否已经在缓存中存在 */
	public static boolean isKeyExistSetWithExpire(String key, int seconds) {
		Jedis jedis = null;
		try{
			jedis = RedisProvider.getJedis();
			long rtn = jedis.setnx(key, "1");
			if(1 == rtn) {
				if(seconds > 0) {
					jedis.expire(key, seconds);
				}
			}
			return rtn == 0;
		} catch(Exception e) {
			LOG.error("isKeyExistSetWithExpire方法报错：key=" + key + ",seconds=" + seconds, e);
			throw new RuntimeException("isKeyExistSetWithExpire方法报错。");
		} finally {
			RedisProvider.returnJedis(jedis);
		}
	}
	
	/** 设置过期时间的方法 */
	public static void expireByKey(String key, int seconds) {
		Jedis jedis = null;
		try{
			jedis = RedisProvider.getJedis();
			if(seconds > 0) {
				jedis.expire(key, seconds);
			}
		} catch(Exception e) {
			LOG.error("expireByKey方法报错：key=" + key + ",seconds=" + seconds, e);
			throw new RuntimeException("expireByKey方法报错。");
		} finally {
			RedisProvider.returnJedis(jedis);
		}
	}
	
	/** 判断key在缓存中是否存在  */
	public static boolean isKeyExist(String key) {
		Jedis jedis = null;
		try{
			jedis = RedisProvider.getJedis();
			return jedis.exists(key);
		} catch(Exception e) {
			LOG.error("isKeyExist方法报错：key=" + key, e);
			throw new RuntimeException("isKeyExist方法报错。");
		} finally {
			RedisProvider.returnJedis(jedis);
		}
	}
}