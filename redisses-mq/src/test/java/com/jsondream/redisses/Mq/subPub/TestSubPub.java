package com.jsondream.redisses.Mq.subPub;

import com.jsondream.redisses.client.RedisClient;
import junit.framework.TestCase;

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
public class TestSubPub extends TestCase{

    public static final String CHANNEL_NAME = "commonChannel";


    public void test() throws IOException{
        final Subscriber subscriber = new Subscriber();

        new Thread(() -> {
            try {
                RedisClient.doWithOut(redis -> redis.subscribe(subscriber, CHANNEL_NAME));
            } catch (Exception e) {
            }
        }).start();

        // new Publisher(CHANNEL_NAME).start();

        long resultSend = Publisher.publish(CHANNEL_NAME, "TEST");
        assertEquals(resultSend,3l);
        subscriber.unsubscribe();
    }

    public void test1() throws IOException{
        final Subscriber subscriber = new Subscriber();


        // new Publisher(CHANNEL_NAME).start();

        long resultSend = Publisher.publish(CHANNEL_NAME, "TEST");
        assertEquals(resultSend,2l);
        new Thread(() -> {
            try {
                RedisClient.doWithOut(redis -> redis.subscribe(subscriber, CHANNEL_NAME));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }
}
