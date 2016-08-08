## redisses的client  

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


