package me.iswear.springdistributedlock.redis;

import me.iswear.springdistributedlock.Lock;
import me.iswear.springdistributedlock.ThreadUtils;
import redis.clients.jedis.Jedis;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

public class RedisReentrantLock implements Lock {

    private ExecutorService holdPoolExecutor;






//    private static Map<String, RedisReentrantLock> lockMap = new ConcurrentHashMap<>();
//
//    {
//        (new Thread(() -> {
//            while (true) {
//                Set<String> keys = lockMap.keySet();
//                for (String key : keys) {
//                    RedisReentrantLock lock = lockMap.get(key);
//                    if (ThreadUtils.isThreadAlive(lock.threadId)) {
//                        lock.jedis.expire(lock.key, 5);
//                    } else {
//                        lock.jedis.del(lock.key);
//                    }
//                }
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        })).start();
//    }
//
//    private Jedis jedis;
//
//    private String key;
//
//    private long threadId;
//
//    public RedisReentrantLock(Jedis jedis, String key) {
//        this.jedis = jedis;
//        this.key = key;
//        this.threadId = Thread.currentThread().getId();
//    }
//
//    @Override
//    public void Lock() {
//        while (this.jedis.incr(key) != 1) {
//            RedisReentrantLock lock = lockMap.get(key);
//            if (lock.threadId == Thread.currentThread().getId()) {
//                break;
//            } else {
//                try {
//                    Thread.sleep(10);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        this.jedis.expire(key, 5);
//        lockMap.put(key, this);
//    }
//
//    @Override
//    public boolean tryLock() {
//        if (this.jedis.incr(key) == 1) {
//            lockMap.put(key, this);
//            this.jedis.expire(key, 5);
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    @Override
//    public void unLock() {
//        this.jedis.del(this.key);
//    }

}
