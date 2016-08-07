package com.jsondream.redisses.lock.lockTest;

import com.jsondream.redisses.lock.RLock;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import static org.junit.Assert.*;

/**
 * <p>
 * <p>
 * </p>
 *
 * @author wangguangdong
 * @version 1.0
 * @Date 16/8/7
 */
public class LockTest {

    RLock rLock = new RLock();

    final String lockKey = "lockKey";
    CountDownLatch countDownLatch ;

    @Test
    public void test() throws InterruptedException {
        int loopNumber = 5;
        countDownLatch= new CountDownLatch(loopNumber);
        for (int i = 0; i < loopNumber; i++) {
            final int number = i;
            new Thread(()->doSomeThingWithLock(number)).start();
        }
        countDownLatch.await();
        System.out.println("全部工作完成");
    }

    public void doSomeThingWithLock(int i) {
        rLock.lock(lockKey);
        try {
            System.out.println(System.currentTimeMillis() + "   线程" + i + "获得了锁并开始了工作" );
            // 模拟耗时操作
            Thread.sleep(i * 1000);
            System.out.println(System.currentTimeMillis() + "   线程" + i + "完成了工作" );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            countDownLatch.countDown();
            rLock.unlock(lockKey);
        }

    }
}
