package com.jsondream.redisses.lock;

import com.jsondream.redisses.client.RedisClient;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import static org.junit.Assert.*;

/**
 * <p>
 * 测试park挂起线程
 * </p>
 *
 * @author wangguangdong
 * @version 1.0
 * @Date 16/8/5
 */
public class TestLockPark {

    @Test
    public void test1() {
        long start = System.nanoTime();
        long parkTime = 10;
        long parkNanoTime = TimeUnit.NANOSECONDS.convert(parkTime, TimeUnit.MILLISECONDS);
        LockSupport.parkNanos(parkNanoTime);
        long end = System.nanoTime();
        System.out
            .println("start  " + start + "     parkNanoTime   " + parkNanoTime + "   end  " + end);
        System.out.println(
            "end-start   " + TimeUnit.MILLISECONDS.convert(end - start, TimeUnit.NANOSECONDS));
        assertTrue(start < end - parkNanoTime);
    }

    @Test
    public void test2() {

        RedisClient.domain(redis -> redis.del("ssssssaa"));
        String a =
            RedisClient.domain(redis -> redis.set("ssssssaa", "1111", "NX", "PX", 30 * 1000));

        assertTrue("OK".equals(a));
        a = RedisClient.domain(redis -> redis.set("ssssssaa", "1111", "NX", "PX", 30 * 1000));
        assertTrue(null == a);
        RedisClient.domain(redis -> redis.del("ssssssaa"));
    }

}
