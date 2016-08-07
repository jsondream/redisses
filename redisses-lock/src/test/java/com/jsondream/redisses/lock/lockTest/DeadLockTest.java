package com.jsondream.redisses.lock.lockTest;

import com.jsondream.redisses.lock.RLock;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

/**
 * <p>
 *     测试是否能够正确解决死锁的问题，
 *     但是这里需要把{@link RLock#tryAcquire(String)}方法里设置过期时间的那端代码暂时注释掉
 *     为的是模拟expire也不成功的情况
 * </p>
 *
 * @author wangguangdong
 * @version 1.0
 * @Date 16/8/7
 */
public class DeadLockTest {

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
            if(i==1){
                // 这里模拟线程0,2,3,4获取锁的时候线程挂掉的情况
                // 正常情况下的时候应该只有1是释放锁的
                rLock.unlock(lockKey);
            }
        }

    }
}
