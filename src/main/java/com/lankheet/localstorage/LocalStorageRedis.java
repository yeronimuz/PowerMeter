package com.lankheet.localstorage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lankheet.pmagent.P1Datagram;
import com.lankheet.utils.JsonUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class LocalStorageRedis implements LocalStorage {

	private static final Logger LOG = LogManager.getLogger(LocalStorageRedis.class);

	private static final String P1DG_KEY = "p1dg";

	private static JedisPool jedisPool;

	public LocalStorageRedis(JedisPool jedisPool) {
		LocalStorageRedis.jedisPool = jedisPool;
	}

	public void activate() {
		if (jedisPool == null) {
			jedisPool = new JedisPool(new JedisPoolConfig(), "localhost");
		}
		LOG.info("SensorEventStorageRedisImpl started!");
	}

	public void deactivate() {
		jedisPool = null;
		LOG.info("SensorEventStorageRedisImpl stopped!");
	}

	/**
	 * Store the datagram locally Cleanup old data
	 */
	@Override
	public void storeP1Measurement(P1Datagram datagram) {
		String jsonString = JsonUtil.toJson(datagram);
		System.out.println(jsonString);
		try (Jedis jedis = jedisPool.getResource()) {
			jedis.lpush(P1DG_KEY, jsonString);
		} catch (Exception e) {
			LOG.fatal("Could not get REDIS resource");
		}
	}
}
