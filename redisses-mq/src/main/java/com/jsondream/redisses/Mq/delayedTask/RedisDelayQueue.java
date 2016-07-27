package com.jsondream.redisses.Mq.delayedTask;

import com.jsondream.redisses.client.RedisClient;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.Tuple;

import java.util.Set;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>
 * 基于redis的延迟队列
 * </p>
 * <p>
 * TODO:这里在考虑是否用直接从delayQueue弹出任务放入到另外的list中，还是直接给使用者返回元素值
 *
 * @author wangguangdong
 * @version 1.0
 * @Date 16/7/27
 * @see java.util.concurrent.DelayQueue
 * @see ReentrantLock
 * @see Condition
 */
public class RedisDelayQueue {

    /**
     * 用单例的操作队列
     */
    private RedisDelayQueue() {
    }

    private static class LazyHolder {
        private static RedisDelayQueue instance = new RedisDelayQueue();
    }

    public static RedisDelayQueue getInstance() {
        return LazyHolder.instance;
    }

    /**
     * 借鉴delayQueue的锁机制
     */
    private final transient ReentrantLock lock = new ReentrantLock();

    /**
     * Condition signalled when a newer element becomes available at the head of
     * the queue or a new thread may need to become leader.
     */
    private final Condition available = lock.newCondition();

    /**
     * <p>
     * 参考了delayQueue的内部结构
     * </p>
     * </br>
     * Thread designated to wait for the element at the head of
     * the queue.  This variant of the Leader-Follower pattern
     * (http://www.cs.wustl.edu/~schmidt/POSA/POSA2/) serves to
     * minimize unnecessary timed waiting.  When a thread becomes
     * the leader, it waits only for the next delay to elapse, but
     * other threads await indefinitely.  The leader thread must
     * signal some other thread before returning from take() or
     * poll(...), unless some other thread becomes leader in the
     * interim.  Whenever the head of the queue is replaced with
     * an element with an earlier expiration time, the leader
     * field is invalidated by being reset to null, and some
     * waiting thread, but not necessarily the current leader, is
     * signalled.  So waiting threads must be prepared to acquire
     * and lose leadership while waiting.
     */
    private Thread leader = null;

    /**
     * 存储延迟任务的队列
     */
    private static final String delayedQueueKeyName = "DelayedQueueSet";

    /**
     * 真正需要执行的队列
     */
    private static final String executeQueueKeyName = "DelayedQueueExecuteSet";

    /**
     * 最大wait时间
     */
    private static final long MAX_WAIT_TIME = 1000 * 60 * 60;

    /**
     * 入队
     * Inserts the specified element into this delay queue.
     *
     * @param businessObjectString
     * @param delay
     */
    public void offer(String businessObjectString, final long delay) {
        // 生成延迟队列时间
        long currentTimeMillis = System.currentTimeMillis();
        final long key = currentTimeMillis + delay + 1;

        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            RedisClient.doWithOut(redis -> {
                if (delay > 0) {
                    redis.zadd(delayedQueueKeyName, key, businessObjectString);
                } else {
                    redis.rpush(executeQueueKeyName, businessObjectString);
                }
            });
            // 判断加入的元素是否会在队首
            if (key < peekScore()) {
                /** DelayQueue中是先判断是否是队首 */
                leader = null;
                available.signal();
                /** end */
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 出队操作
     * <description>
     * 这里的take出队方法在很大的程度上参照了DelayQueue中的take的实现方式
     * </description>
     *
     * @throws InterruptedException
     * @see #peek()
     * @see DelayQueue#take()
     */
    public Set<String> take() throws InterruptedException {
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            // 用for(;;)代替while(true)
            // 因为for(;;)编译后指令少，不占用寄存器，而且没有判断跳转
            for (;;) {
                // 获取到期的业务对象信息
                Set<String> elementValue = peek();
                // 判断是否有过期的元素,如果有的话直接出队
                if (!elementValue.isEmpty()) {
                    return elementValue;
                }

                if (leader != null) {
                    // 阻塞当前线程
                    available.await();
                } else {
                    Thread thisThread = Thread.currentThread();
                    leader = thisThread;
                    try {
                        // 获取首元素的score
                        long firstElementDelayScore = peekScore();
                        // 计算出需要等待的时间
                        long waitTime = firstElementDelayScore == MAX_WAIT_TIME ?
                            firstElementDelayScore :
                            firstElementDelayScore - System.currentTimeMillis();
                        // 这里的阻塞可能会比leader为空的时候更少,是一个更高效的阻塞方式
                        available.await(waitTime, TimeUnit.MILLISECONDS);
                    } finally {
                        if (leader == thisThread)
                            leader = null;
                    }
                }
            }
        } finally {
            if (leader == null && !peek().isEmpty())
                available.signal();
            lock.unlock();
        }
    }

    /**
     * 查询出key大于当前时间的
     * <p>
     * 此方法的
     * </p>
     *
     * @return
     */
    private Set<String> peek() {
        Long currentTimeMillis = System.currentTimeMillis();
        return RedisClient.domain(redis -> {
            // 开启事务保持原子性
            Transaction tx = redis.multi();
            // 获得到期需要执行的队列
            Response<Set<String>> getResult =
                tx.zrangeByScore(delayedQueueKeyName, 0, currentTimeMillis);
            Set<String> result = getResult.get();
            // 执行删除
            if (!result.isEmpty()) {
                tx.zremrangeByScore(delayedQueueKeyName, 0, currentTimeMillis);
            }
            // 提交事务
            tx.exec();
            return result;

        });

    }

    /**
     * 查询出最小的score
     *
     * @return
     */
    private long peekScore() {
        final Long currentTimeMillis = System.currentTimeMillis();
        return RedisClient.domain(redis -> {

            /**
             * 获取到Score在当前时间~之后的#MAX_WAIT_TIME#时间内的的任务
             * 当然，这个MAX_WAIT_TIME最好根据实际的使用情况来调整，或者根据一定的用量算法来匹配一个合适的值
             */
            Set<Tuple> tupleSet = redis
                .zrangeByScoreWithScores(delayedQueueKeyName, currentTimeMillis,
                    currentTimeMillis + MAX_WAIT_TIME);
            // 如果MAX_WAIT_TIME内没任务就返回最大等待时间MAX_WAIT_TIME
            if (tupleSet.isEmpty()) {
                return MAX_WAIT_TIME;
            } else {
                // 返回队首的score值
                Tuple tuple = tupleSet.iterator().next();
                return (long) tuple.getScore();
            }
        });
    }

    public static void main(String[] a) throws Exception {
        RedisDelayQueue redisDelayQueueApi = RedisDelayQueue.getInstance();
        redisDelayQueueApi.offer("sss", 60l * 1000l * 60l * 24l * 30l * 3l);
        redisDelayQueueApi.take();
    }
}
