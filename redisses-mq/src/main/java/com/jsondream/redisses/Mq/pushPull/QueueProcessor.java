package com.jsondream.redisses.Mq.pushPull;

import com.jsondream.redisses.Mq.pushPull.constants.RedisMessageQueueConstants;
import com.jsondream.redisses.client.RedisClient;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 消息监听器。消费者端逻辑
 *
 * @author wangguangdong
 */
public class QueueProcessor {

    private int timeout = 30;

    /**
     * 守护线程
     */
    private Thread daemonThread;

    private ExecutorService executor = Executors.newFixedThreadPool(30);

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    @PostConstruct
    public void init() {
        daemonThread = new Thread(() -> process(executor));
        daemonThread.setDaemon(true);
        daemonThread.setName("Redis Queue Daemon Thread");
        daemonThread.start();
    }

    public void destroy() {
        // TODO
    }

    /**
     * Default constructor for convenient dependency injection via setters.
     */
    public QueueProcessor() {
        init();
    }

    /**
     * 异步执行。消息处理器在executor执行。
     *
     * @param executor
     * @throws Exception
     */
    public void process(ExecutorService executor) {

        while (true) {
            // 取出要消费的消息
            final String messages = RedisClient.domain(jedis -> jedis
                .brpoplpush(RedisMessageQueueConstants.queueName,
                    RedisMessageQueueConstants.consumerQueueName, timeout));
            //            List<String> messages =
            //                RedisClient.domain(jedis ->jedis.brpop(this.timeout, queueName));
            // final String payload = messages.get(1);
            // final String payload = messages;
            submitTask(executor, messages);
        }
    }

    private void submitTask(ExecutorService executor, final String payload) {
        if (StringUtils.isNotBlank(payload)) {
            executor.execute(() -> MessageHandler.onMessage(payload));
            //Future future = executor.submit(() -> MessageHandler.onMessage(payload));
            //future.cancel(true);
        }
    }

    public static void main(String[] a) {
        QueueProcessor queueProcessor = new QueueProcessor();
        for (; ; )
            ;
    }
}
