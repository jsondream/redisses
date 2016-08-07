package com.jsondream.redisses.lock;

import com.jsondream.redisses.client.RedisClient;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Transaction;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * <p>
 * 参考文章地址:
 * http://blog.csdn.net/ugg/article/details/41894947
 * http://www.blogjava.net/caojianhua/archive/2013/01/28/394847.html
 * </p>
 *
 * @author wangguangdong
 * @version 1.0
 * @Date 16/8/3
 */
public class RLock {

    /**
     * 分布式锁的前缀
     */
    private static final String lockPrefix = "RLOCK:";
    private static final String subPrefix = "-";

    /**
     * 最大等待时限,单位为秒
     */
    private static final int maxLockSecond = 30;

    /**
     * 线程获取锁失败的时候挂起的时间
     */
    private static final long parkNanosTime =
        TimeUnit.NANOSECONDS.convert(3, TimeUnit.MILLISECONDS);

    /**
     * 加锁操作
     *
     * @param key
     * @param time
     * @param unit
     * @return
     */
    public boolean tryLock(String key, long time, TimeUnit unit) throws InterruptedException {
        long nanosTimeout = TimeUnit.NANOSECONDS.convert(time, unit);
        if (time <= 0L)
            return false;
        // 过期的nanoTime
        final long deadline = System.nanoTime() + nanosTimeout;
        for (; ; ) {
            // 尝试获取锁
            if (tryAcquire(key)) {
                return true;
            }
            nanosTimeout = deadline - System.nanoTime();
            if (nanosTimeout <= 0L)
                return false;
            // 挂起线程
            parkThread(parkNanosTime);
            // 支持线程中断
            if (Thread.interrupted())
                throw new InterruptedException();
        }
    }

    /**
     * 加锁
     *
     * @param key
     */
    public void lock(String key) {
        // 第一版可能是线程sleep(N秒)然后重新尝试获取锁
        // 第二版考虑吧每个尝试锁的线程放到一个等待队列中
        // 然后根据等待队列的数量(即锁竞争的激烈情况)来动态规划尝试获取锁的时间
        // 因为这里的获取锁是从redis中做大量的操作,所以和普通的lock获取不同
        // 所以这里需要考虑对redis锁获取的频次

        // 循环获取锁
        for (; ; ) {
            // 查看是否获取到锁
            if (tryAcquire(key)) {
                return;
            }
            // 线程挂起一点时间后继续重试获取锁
            parkThread(parkNanosTime);
        }
    }

    /**
     * 挂起当前线程
     */
    private void parkThread(long nanoTime) {
        LockSupport.parkNanos(nanoTime);
    }

    /**
     * 尝试获取锁</br>
     * 我们这里的获取方式是非公平式的获取
     * 没有一个先入先出的队列限制,日后考虑扩展
     *
     * @param key
     * @return
     */
    private boolean tryAcquire(String key) {
        final String keys = lockPrefix + key;
        return RedisClient.domain(redis -> {
            String uid = UUID.randomUUID().toString();
            long currentTimeMillis = System.currentTimeMillis();
            // 超时的时间
            long expireTime = +maxLockSecond * 1000;
            // 一个系统时间+uid来组成值
            String lockValue = expireTime + subPrefix + uid;
            // 尝试获取锁
            Long res = redis.setnx(keys, lockValue);
            // 获取锁成功的情况
            if (res.equals(1l)) {
                // 设置过期时间,防止线程崩溃后死锁
                redis.expire(keys, maxLockSecond);
                return true;
            } else {
                /**
                 * setnx获取锁失败,但是有可能上次获得锁的线程已经崩溃
                 * 这里指的是setnx成功，但是还没来得及设置过期时间的时候崩溃
                 * 这里需要重新判断锁是否是失效,来重新进行锁的竞争
                 * 这里需要先开启watch命令,用来保证之后判断操作的原子性
                 */
                redis.watch(keys);
                // 获取现在的值
                String oldValue = redis.get(keys);
                if (StringUtils.isNoneBlank(oldValue)) {
                    String[] valueResult = oldValue.split(subPrefix);
                    // 处理锁超时的情况
                    if (Long.parseLong(valueResult[0]) > currentTimeMillis) {
                        /**
                         * 这里为什么没有采取参考的文章中getSet的方式呢
                         * 试想一下，如果我们的c1死锁
                         * c2调用getSet获取失败(可能c3先获取锁成功)
                         * 这时候锁里面的value就是c2的value
                         * 假如这时候c4重新尝试获取锁的话就会出问题,因为看到的是c2的值
                         * 那么久需要c2在调用getSet获取失败之后把old值重新塞回去
                         * 那么这时候假如c3操作完了，要释放锁，那么这时候c2又把c3的值搞回去了
                         * 我们可能就的认为c3是获取锁了。。那么这时候又得等到c3超过过期时间</br>
                         * 这种操作是一种不必要的浪费,用redis提供事务(基于cas)的原子操作就足够了
                         */
                        // 开启事务
                        Transaction tx = redis.multi();
                        // 执行操作,此操作具有原子性
                        tx.set(keys, lockValue);
                        // 提交事务
                        List<Object> txResult = tx.exec();
                        // 根据事务执行结果判断是否成功获取了锁
                        if (txResult == null || txResult.isEmpty()) {
                            redis.unwatch();
                            return false;
                        } else {
                            // 事务执行成功,锁获取成功
                            return true;
                        }
                    } else {
                        return false;
                    }
                } else {
                    // 如果值位空很可能是释放锁的操作已经是临界点了
                    // 为了安全操作的考虑需要重新调用tryLock来尝试获取锁
                    tryAcquire(keys);
                }
            }
            return false;
        });
    }

    /**
     * 释放锁
     *
     * @param key
     * @return
     */
    public boolean unlock(String key) {
        return RedisClient.domain(redis -> {
            Long res = redis.del(lockPrefix+key);
            return res.equals(1l);
        });
    }

}
