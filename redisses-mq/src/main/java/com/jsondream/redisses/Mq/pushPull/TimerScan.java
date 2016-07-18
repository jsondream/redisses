package com.jsondream.redisses.Mq.pushPull;

import com.jsondream.redisses.client.RedisClient;

/**
 * <p>
 * 定时器接口，定时器定时扫描需要从doing中废弃的task重新塞会到pending队列中
 * </p>
 *
 * @author wangguangdong
 * @version 1.0
 * @Date 16/7/15
 */
public class TimerScan {

    public static void init() {
        scan();
    }

    private static void scan() {
        MessageRePending.rePending();
        //        RedisClient.doWithOut(jedis -> jedis.());
    }
}
