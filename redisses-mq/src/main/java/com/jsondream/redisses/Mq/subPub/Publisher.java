package com.jsondream.redisses.Mq.subPub;

import com.jsondream.redisses.client.RedisClient;

import java.io.BufferedReader;
import java.io.IOException;
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


public class Publisher {



    private final String channel;

    public Publisher(String channel) {
        this.channel = channel;
    }

    public void start() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                String line = reader.readLine();

                if (!"quit".equals(line)) {
                    RedisClient.doWithOut(redis -> redis.publish(channel, line));
                } else {
                    break;
                }
            }

        } catch (IOException e) {
        }
    }
    public static Long publish(final String channel,String message){
        return RedisClient.domain(redis -> redis.publish(channel, message));
    }
}
