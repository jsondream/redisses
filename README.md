
# redisses  

## redisses是什么  

**redisses一个redis操作相关的项目，包括用户存储redis对象变成hashmap的工具，包括支持ack应答，取消消息的mq**  
**redisses是一个机遇redis的base-demo工程，包括使用java8的lambda来封装连接的获取和释放，
包括简单的redisMq的实现**  

### redisses的client  

#### client描述  

redisses使用java8的lambda来封装了redis的连接资源的获取和释放，让用户只关心业务操作和逻辑,
其本质的实现跟aop的原理是相同的，所以我们只需要关注于业务端的调用即可  

#### 使用方法   

需要返回值的情况:  

```java  
    RedisClient.domain(jedis -> jedis.lrem(consumerQueueName, 1, message));
```  

不关心返回值的情况:  

```java  
    RedisClient.doWithOut(jedis -> jedis.lrem(consumerQueueName, 1, message));
```  


也就是说只需要在domian/doWithOut里传递我们要做的redis操作的lambda表达式即可  


### redisses的消息队列   
 
#### 实现的功能   

简单的消息队列，消息存储，消息消费,ack机制  

#### 使用方式  



#### 缺陷  

push/pull模型的ack机制引入之后有可能会出现消息重复的情况，所以需要消费者方幂等机制。  

>发送消息重复的情况是这样的情况,在doing执行了一个任务之后,scan执行了，并且取出这个任务,认为这个任务没执行成功，
吧任务重新塞会到pending队列中，那么这时候如果doing的任务没有执行完事，而pending中的这个任务在doing完成之前又
被第二次消费掉了，那么这时候我们就可以任务这个任务是重复了，（scan的话可以考虑用delayed任务来完成），给任务一个
延迟的消费方式，这样可以降低消息的重复性。  

任务处理那一快没有支持spring,以后考虑支持spring。  


### demo环境描述  

redis2.7、java8、maven3
