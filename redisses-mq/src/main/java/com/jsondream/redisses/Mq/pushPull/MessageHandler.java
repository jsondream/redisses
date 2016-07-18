package com.jsondream.redisses.Mq.pushPull;

import com.alibaba.fastjson.JSON;
import com.jsondream.redisses.Mq.pushPull.bean.RedisQueueMessage;
import com.jsondream.redisses.Mq.pushPull.bean.User;
import com.jsondream.redisses.Mq.pushPull.constants.RedisMessageQueueConstants;
import com.jsondream.redisses.client.RedisClient;

/**
 * 消息处理接口。这里不引入MessageConverter的概念，只接收textMessage，一般来说是JSON。
 *
 * @author wangguangdong
 */
public class MessageHandler {

    /**
     * <p>
     * 任务执行的地方,这里可以自己对接任务接口,需要在任务执行之后
     * <br>根据fastJson序列化之后的classKey来判断
     * </p>
     *
     * @param message
     */
    public static void onMessage(String message) {
        try {
            String clazz = JSON.parseObject(message).getString("classKey");
            User user = null;
            if (clazz.toLowerCase().equals("user")) {
                user = JSON.parseObject(message, User.class);
            }
            // 模拟任务消费
            System.out.println(user.getId() + " is doing......");
            Thread.sleep(3000);
            System.out.println(user.getId() + " done......");
            // 从redis中删除信息
            completedTaskDelMessageFromRedis(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * 以协议的形式传参
     *
     * @param message
     */
    public static void onMessage2(String message) {
        try {

            RedisQueueMessage redisQueueMessage =
                JSON.parseObject(message, RedisQueueMessage.class);
            int bodyCode = redisQueueMessage.getBodyCode();
            User user = null;
            if (bodyCode == 102) {
                user = JSON.parseObject(message).getObject("body", User.class);
            }
            // 模拟任务消费
            System.out.println(user.getId() + " is doing......");
            Thread.sleep(3000);
            System.out.println(user.getId() + " done......");
            // 从redis中删除信息
            completedTaskDelMessageFromRedis(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 完成任务，删除redis队列中的message信息
     */
    public static void completedTaskDelMessageFromRedis(String message){
        // 删除doing中的任务，表示任务完成
        long result = RedisClient.domain(
            jedis -> jedis.lrem(RedisMessageQueueConstants.consumerQueueName, 1, message));
        // 说明这任务已经不在doing队列中
        if (result != 1) {
            long resultPending = RedisClient
                .domain(jedis -> jedis.lrem(RedisMessageQueueConstants.queueName, 1, message));
            // 说明被再次消费了
            if(resultPending!=1){
                //TODO:发送重复消费的情况的处理
                //messageQueueDoingError(message);
            }
        }
    }


    // 考虑让用户记录重复消费
    // abstract void messageQueueDoingError(String message);
}
