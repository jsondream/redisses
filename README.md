
# redisses  

## redisses是什么  

**redisses一个redis操作相关的项目，包括用户存储redis对象变成hashmap的工具，包括支持ack应答，取消消息的mq**  
**redisses是一个基于redis的base-demo工程，包括使用java8的lambda来封装连接的获取和释放，
包括简单的redisMq的实现,包括抢购,分布式锁等功能**  

### 目前的功能列表  

* [redisses的client](redisses-client/README.md)  
* [基于redis的抢购](redisses-spike/README.md)  
* [基于rediss的并且具有ack机制的消息队列](redisses-mq/README.md)   
* [基于rediss的分布式锁](redisses-lock/README.md)   

### demo环境描述  

redis2.7、java8、maven3  

### TODO-LIST  

* 优化基于redis实现的分布式锁   
* 优化redisses-Mq的实现机制,争取让他没有重复消息

