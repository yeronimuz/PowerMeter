package com.lankheet.localstorage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lankheet.pmagent.P1Datagram;
import com.lankheet.utils.JsonUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Local Redis storage of P1 datagrams<br>
 * This is meant as temporary storage<br>
 * New datagram will be pushed to the head. <br>
 * Consumer will remove them from the tail.
 * @author jeroen
 *
 */
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
	 * Store the datagram locally<br>
	 */
	@Override
	public void storeP1Measurement(P1Datagram datagram) {
		String jsonString = JsonUtil.toJson(datagram);
		System.out.println(jsonString);
		try (Jedis jedis = jedisPool.getResource()) {
			// We use the same key for all packets (no hash-code added)
			// This is not a problem if we consider this just as serial data
			jedis.lpush(P1DG_KEY, jsonString);
			LOG.debug("Datagrams locally stored: " + jedis.lrange(P1DG_KEY, 0, -1).size());
		} catch (Exception e) {
			LOG.fatal("Could not get REDIS resource");
		}
	}
}
