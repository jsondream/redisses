package com.jsondream.redisses.Mq;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.Assert.*;
/**
 * <p>
 * 测试在没有wait的情况下是否可以signal
 * </p>
 *
 * @author wangguangdong
 * @version 1.0
 * @Date 16/7/19
 */
public class TestLock {

    @org.junit.Test
    public void test(){
        new TestSignal().offer();
        assertTrue(true);
    }

    class TestSignal {
        ReentrantLock lock = new ReentrantLock();
        Condition condition = lock.newCondition();

        public void offer(){
            lock.lock();
            try {
                condition.signal();
            }finally {
                lock.unlock();
            }
        }
    }
}
