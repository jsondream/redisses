package com.jsondream.redisses.Mq.subPub;

import redis.clients.jedis.JedisPubSub;

/**
 * <p>
 * <p>
 * </p>
 *
 * @author wangguangdong
 * @version 1.0
 * @Date 16/7/19
 */
public class Subscriber extends JedisPubSub {


    @Override
    public void onMessage(String channel, String message) {
        //TODO:
        System.out.println("channel name is:" +channel +"\nmessage is:"+message);
    }

    @Override
    public void onPMessage(String pattern, String channel, String message) {

    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels) {

    }

    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {

        System.out.println();
    }

    @Override
    public void onPUnsubscribe(String pattern, int subscribedChannels) {

    }

    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {

    }
}
