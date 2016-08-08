
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

### 优点  

增强对死锁的检测，一个对象持有锁默认的最大工作时是30s    

如果30秒过后就对存储 value值进行检测  

利用multi机制加强了对死锁之后再次重新竞争发生同时获取锁的限制  

### 需要优化的地方

目前的锁机制是在尝试获取锁失败之后使用locksupport工具类讲线程挂起3s  

准备参考redisson的lock机制(pub/sub)进行锁等待和释放锁通知  
