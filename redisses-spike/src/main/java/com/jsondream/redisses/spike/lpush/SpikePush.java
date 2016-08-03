package com.jsondream.redisses.spike.lpush;

import com.jsondream.redisses.client.RedisClient;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Transaction;

import java.util.List;

/**
 * <p>
 * 基于redis的抢红包功能
 * </p>
 *
 * @author wangguangdong
 * @version 1.0
 * @Date 16/8/2
 */
public class SpikePush {
    /**
     * 最大购买数量
     */
    int spikeMaxGoodNumber = 2;

    String purchaseKey = "PURCHASE:KEY";
    String spikeMaxGoodNumberKey = "SPIKE:MAX:GOOD:NUMBER:KEY";

    /**
     * 抢购方法
     * <description>
     * 1. 首先判断成功抢购的人的数量
     * 2. 判断是否已经买过
     * 3. 开启cas和事务
     * 4. 成功抢购人数+1
     * 5. 抢购队列增加用户
     * 6. 判断执行结果
     * </description>
     *
     * @param userId
     */
    public boolean spike(String userId) {
        return RedisClient.domain(redis -> {
            String watchResult = redis.watch(spikeMaxGoodNumberKey);
            // 开启cas是否成功
            if (!"OK".equals(watchResult))
                return false;
            // 判断是否已经买过,这里更推荐的方式是利用上游的请求进行拦截
            // 如nginx每秒内限制同一个userId调用该接口的次数
            Boolean isBuy = redis.sismember(purchaseKey, userId);
            // 防止占用购买名额
            if (isBuy) {
                return false;
            }
            // 获取已经购买的用户数
            String spikeNum = redis.get(spikeMaxGoodNumberKey);
            int spikeNumber = StringUtils.isBlank(spikeNum) ? 0 : Integer.parseInt(spikeNum);
            // 判断是否超过抢购数量的限额
            if (spikeNumber >= spikeMaxGoodNumber) {
                return false;
            }
            // 开启事务
            Transaction tx = redis.multi();
            // 增加抢购人员的数量
            tx.set(spikeMaxGoodNumberKey, spikeNumber + 1 + "");
            // 提交事务
            List<Object> result = tx.exec();
            // 根据事务执行结果判断是否抢购成功
            if (result == null || result.isEmpty()) {
                redis.unwatch();
                return false;
            } else {
                // 加入到抢购成功的用户队列
                tx.sadd(purchaseKey, userId);
                return true;
            }
        });
    }

    /**
     * 该方法适用于特殊的抢购情况(延迟查看结果的情况)
     * 例如:需要后续公布结果的
     * 这种方式是无锁的方式，只需要在最后的时候取的抢购数额的队列值的内容即可
     * @param userId
     * @param purchaseKey
     * @return
     */
    public boolean spikeUseLrem(String userId, String purchaseKey) {
        return RedisClient.domain(redis -> {
            long spikeNum = redis.llen(purchaseKey);
            try {
                // 模拟4ms的redis数据获取的网络延迟的情况
                Thread.sleep(4);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (spikeNum >= spikeMaxGoodNumber) {
                return false;
            }
            // 加入到抢购成功的用户队列
            Long purchaseResult = redis.sadd(purchaseKey, userId);
            return purchaseResult == 1;
        });
    }
}
