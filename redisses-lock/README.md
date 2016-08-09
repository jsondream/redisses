
## redisses-lock  

redisses-lock是一个基于redis实现的分布式锁

### 使用方式  

```
    RLock rLock = new RLock();
    // lockKey为要锁住的字符串
    rLock.lock(lockKey);
        try {
            //TODO doSomeThing
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            rLock.unlock(lockKey);
        }
```

### 实现方式  

redis提供了一个SetNX命令,就是当key不存在的时候设置这个key的value  

我们可以基于这个命令扩展来实现我们的分布式锁,同时利用expire命令设置key超时时间,  

避免持有锁的进程挂掉之后不释放锁而造成的死锁.  

redis中提供了让SetNX和expire进行原子性一步操作的命令,使分布式锁更加可靠。

### 优点  

增强对死锁的检测，一个对象持有锁默认的最大工作时是30s    

如果30秒过后就对存储 value值进行检测  

利用multi机制加强了对死锁之后再次重新竞争发生同时获取锁的限制  

### 需要优化的地方

目前的锁机制是在尝试获取锁失败之后使用locksupport工具类讲线程挂起3s  

准备参考redisson的lock机制(pub/sub)进行锁等待和释放锁通知  
