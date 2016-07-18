package com.jsondream.redisses.Mq.pushPull.bean;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author wangguangdong
 * @version 1.0
 * @Date 16/7/16
 */
public class BaseBean implements Serializable {
    // 塞入队列的时间戳
    private long timeMillis;
    // 为了以后支持集群的扩展,寻找对应的server在处理的id
    private String serverId;
    // 重新定义的消息Id,这个是为了完成消费者玩的幂等性
    private String messageId;
    // 消息消费的时间(塞入到doing队列的时间)
    private long execTime;
}
