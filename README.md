# redisses  

## redisses是什么  

redisses是一个机遇redis的base-demo工程，包括使用java8的lambda来封装连接的获取和释放，
包括简单的redisMq的实现  

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

#### 缺陷  

push/pull模型的ack机制引入之后有可能会出现消息重复的情况，所以需要消费者方幂等机制。


### demo环境描述  

redis2.7、java8、maven3
