package com.jsondream.redisses.client.domainInterfaces;

import redis.clients.jedis.Jedis;

/**
 * <p>
 * <p>
 * </p>
 *
 * @author wangguangdong
 * @version 1.0
 * @Date 15/12/24
 */
public interface RedisDomainInterface <T> {
    public T domain(Jedis jedis);
}
