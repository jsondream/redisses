package com.jsondream.rediss.spike;

import com.jsondream.redisses.spike.lpush.SpikePush;
import org.junit.Test;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * <p>
 * <p>
 * </p>
 *
 * @author wangguangdong
 * @version 1.0
 * @Date 16/8/2
 */
public class BenchMarkSpikePush {

    /**
     * BenchMarkTest
     * @see SpikePush#spike(String)
     * @throws InterruptedException
     */
    @Test
    public void test() throws InterruptedException {
        long start = System.currentTimeMillis();
        int threadCount = 200;
        final CountDownLatch latch = new CountDownLatch(threadCount);

        final CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        CopyOnWriteArrayList<String> resultList = new CopyOnWriteArrayList<>();
        final SpikePush spikePush = new SpikePush();
        for(int i = 0 ;i<threadCount;i++){

            final String userId = String.valueOf(i);
            Thread t= new Thread(()->{
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                boolean r = spikePush.spike(userId);
                System.out.println("  当前的线程id是"+userId+"  执行结果是"+r);

                if(r){
                    resultList.add(userId);
                }
                latch.countDown();
            });
            t.start();
            countDownLatch.countDown();
        }
        latch.await();
        System.out.println("总共花费了"+(System.currentTimeMillis()-start)+"ss");
        System.out.println(resultList);
    }

    /**
     * BenchMarkTest
     * @see SpikePush#spikeUseLrem(String, String)
     * @throws InterruptedException
     */
    @Test
    public void test2() throws InterruptedException {
        long start = System.currentTimeMillis();
        int threadCount = 200;
        final CountDownLatch latch = new CountDownLatch(threadCount);

        final CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        CopyOnWriteArrayList<String> resultList = new CopyOnWriteArrayList<>();
        final SpikePush spikePush = new SpikePush();
        for(int i = 0 ;i<threadCount;i++){

            final String userId = String.valueOf(i);
            Thread t= new Thread(()->{
                try {
                    countDownLatch.await();
                    System.out.println("收到请求的时间戳是:"+System.currentTimeMillis()+"  当前的线程id是"+userId);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                boolean r = spikePush.spikeUseLrem(userId, "purchaseKey.lrem");
                if(r){
                    resultList.add(userId);
                }
                latch.countDown();
            },userId);
            t.start();
            countDownLatch.countDown();
        }
        latch.await();
        System.out.println("总共花费了"+(System.currentTimeMillis()-start)+"ms");
        System.out.println(resultList);
    }



    

}
