package com.jsondream.redisses.client;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * <p>
 * redis连接池客户端管理类
 * </p>
 *
 * @author wangguangdong
 * @version 1.0
 * @Date 15/12/23
 */
public class RedisPoolClient {

    private RedisPoolClient() {
    }

    public static RedisPoolClient getInstance() {
        return LazyHolder.redisPoolClient;
    }

    private static class LazyHolder {
        private static final RedisPoolClient redisPoolClient = new RedisPoolClient();
    }

    // 连接池
    private static JedisPool pool;

    private static final ResourceBundle bundle = ResourceBundle.getBundle("redis");

    public void initPool() {
        if (bundle == null) {
            throw new IllegalArgumentException("[redis.properties] is not found!");
        }
        // redis配置信息
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(Integer.valueOf(bundle.getString("redis.pool.maxTotal")));
        config.setMaxIdle(Integer.valueOf(bundle.getString("redis.pool.maxIdle")));
        config.setMaxWaitMillis(Long.valueOf(bundle.getString("redis.pool.maxWait")));
        config.setTestOnBorrow(Boolean.valueOf(bundle.getString("redis.pool.testOnBorrow")));
        config.setTestOnReturn(Boolean.valueOf(bundle.getString("redis.pool.testOnReturn")));

        // redis连接信息
        pool = new JedisPool(config, bundle.getString("redis.ip"),
            Integer.valueOf(bundle.getString("redis.port")),
            Integer.valueOf(bundle.getString("redis.connectionOutTime")),
            bundle.getString("redis.auth"));
    }

    //    public static void main(String[] args) {
    //        RedisPoolClient.getInstance().initPool();
    //        // 从池中获取一个Jedis对象
    //        Jedis jedis = RedisPoolClient.getInstance().getJedis();
    //        String keys = "name";
    //        // 删数据
    //        jedis.del(keys);
    //        // 存数据
    //        jedis.set(keys, "snowolf");
    //        // 取数据
    //        String value = jedis.get(keys);
    //
    //        Map<String, String> map = new HashMap<>();
    //        map.put("sc", "sb");
    //        //        jedis.hmset("map", map);
    //        jedis.hdel("game", "sc");
    //        System.out.println(map == null ? 1 : 2);
    //        System.out.println(jedis.hgetAll("map"));
    //        System.out.println(jedis.hgetAll("game"));
    //        System.out.println(value);
    //        System.out.println(jedis.hget("map", "sc"));
    //        jedis.hdel("map", "sc");
    //        System.out.println(jedis.hgetAll("map"));
    //        System.out.println(jedis.hget("map", "sc"));
    //
    //    }

    public static void main(String[] args) {
        RedisPoolClient.getInstance().initPool();
        Jedis jedis = RedisPoolClient.getInstance().getJedis();

//        jedis.zadd("hehu", new Date().getTime(), "09987:123");
//        jedis.zadd("hehu", 12, "4567:1");
//        jedis.zadd("hehu", 234, "345:32");

       // jedis.zrem("hehu","345");

        //jedis.zrem("hehu","09987:");

//        Set<String> ss = jedis.zrangeByLex("hehu","(09987:","-");
//        Iterator<String> t = ss.iterator();
//        while(t.hasNext()){
//            System.out.println(t.next());
//        }



        jedis.zremrangeByLex("hehu","[09987:123","[－");
        Set<String> s = jedis.zrevrange("hehu", 0, 10);





        Iterator<String> it = s.iterator();
        while(it.hasNext()){
            System.out.println(it.next());
        }


    }

    /**
     * 获取Jedis实例
     *
     * @return
     */
    public synchronized Jedis getJedis() {
        try {
            if (pool != null) {
                Jedis resource = pool.getResource();
                return resource;
            } else {
                // 腾讯云的redis,3个小时没有数据传输就会断开长连接,这里是为了重新建立长连接
                initPool();
                return pool != null ? pool.getResource() : null;
            }
        } catch (Exception e) {
            //TODO: connect error log的记录....
        }
        return null;
    }

    /**
     * 释放jedis资源
     *
     * @param jedis
     */
    public void returnResource(final Jedis jedis) {
        if (jedis != null && pool != null) {
            pool.returnResourceObject(jedis);
        }
    }

    /**
     * 释放对象池
     */
    public void destroy() {
        synchronized (pool) {
            if (pool != null) {
                pool.destroy();
            }
        }
    }
}
