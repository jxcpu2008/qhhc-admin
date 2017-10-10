package com.hc9.common.redis;

import java.util.ResourceBundle;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**  redis缓存池初始化工具类 */
public class RedisProvider {
	private static JedisPool jedispool;

	static {
		ResourceBundle bundle = ResourceBundle.getBundle("/config/redis/redis");
		if (bundle == null) {
			throw new IllegalArgumentException("[redis.properties] is not found!");
		}
		JedisPoolConfig jedisconfig = new JedisPoolConfig();
		jedisconfig.setMaxActive(Integer.valueOf(bundle.getString("redis.pool.maxActive")).intValue());
		jedisconfig.setMaxIdle(Integer.valueOf(bundle.getString("redis.pool.maxIdle")).intValue());
		jedisconfig.setMaxWait(Long.valueOf(bundle.getString("redis.pool.maxWait")).longValue());
		jedisconfig.setTestOnBorrow(Boolean.valueOf(bundle.getString("redis.pool.testOnBorrow")).booleanValue());
		jedisconfig.setTestOnReturn(Boolean.valueOf(bundle.getString("redis.pool.testOnReturn")).booleanValue());
		jedispool = new JedisPool(jedisconfig, 
					bundle.getString("redis.ip"),
					Integer.valueOf(bundle.getString("redis.port")).intValue(),
					Integer.valueOf(bundle.getString("redis.timeout")).intValue(),bundle.getString("redis.pwd"));
	}

	public static Jedis getJedis() {
		return (Jedis)jedispool.getResource();
	}

	public static void returnJedis(Jedis jedis) {
		if(jedis != null) {
			jedispool.returnResource(jedis);
		}
	}
}