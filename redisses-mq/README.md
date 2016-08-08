
# redisses的消息队列:***redisses-mq***   
 
## 实现的功能   

简单的消息队列，消息存储，消息消费,ack机制  

### 基于lpush/brpop的消息队列  

#### 使用方式  

```
做成中。。。。。
```

#### 缺陷  

push/pull模型的ack机制引入之后有可能会出现消息重复的情况，所以需要消费者方幂等机制。  

> 发送消息重复的情况是这样的情况,在doing执行了一个任务之后,scan执行了，并且取出这个任务,认为这个任务没执行成功，
吧任务重新塞会到pending队列中，那么这时候如果doing的任务没有执行完事，而pending中的这个任务在doing完成之前又
被第二次消费掉了，那么这时候我们就可以任务这个任务是重复了，（scan的话可以考虑用delayed任务来完成），给任务一个
延迟的消费方式，这样可以降低消息的重复性。  

### 发布订阅  

#### 使用方式  
在一个新开的线程中创建Subscriber对象,并且订阅响应的队列  

```java
final Subscriber subscriber = new Subscriber();
RedisClient.doWithOut(redis -> redis.subscribe(subscriber, CHANNEL_NAME));
```  

***因为订阅的行为会阻塞线程,所以要使用一个新的线程去订阅呢。***  

生产者发送消息直接调用  

```java
Publisher.publish(CHANNEL_NAME, line);
```


## TODO-LIST  

任务处理那一快没有支持spring,以后考虑支持spring。  
