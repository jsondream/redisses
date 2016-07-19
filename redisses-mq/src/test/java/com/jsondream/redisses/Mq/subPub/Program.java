package com.jsondream.redisses.Mq.subPub;

import com.jsondream.redisses.client.RedisClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * <p>
 * <p>
 * </p>
 *
 * @author wangguangdong
 * @version 1.0
 * @Date 16/7/19
 */

public class Program {

    public static final String CHANNEL_NAME = "commonChannel";

    public static void main(String[] args) throws Exception {

        final Subscriber subscriber = new Subscriber();

        new Thread(() -> {
            try {
                RedisClient.doWithOut(redis -> redis.subscribe(subscriber, CHANNEL_NAME));
            } catch (Exception e) {
            }
        }).start();

        // new Publisher(CHANNEL_NAME).start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            String line = reader.readLine();

            if (!"quit".equals(line)) {
                Publisher.publish(CHANNEL_NAME, line);
            } else {
                break;
            }
        }
        subscriber.unsubscribe();
    }
}
