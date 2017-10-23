package me.iswear.springdistributedlock.redis;

import me.iswear.springdistributedlock.CacheLockCallBack;
import me.iswear.springdistributedlock.exception.LockExpiredException;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.concurrent.locks.ReentrantLock;

public class RedisReentrantLockTest {

    private RedisReentrantLock lock;

    @Before
    public void before() {
        lock = new RedisReentrantLock();

        RedisReentrantLockConfig config = new RedisReentrantLockConfig();
        config.setCorePoolSize(1);
        config.setMaxPoolSize(10);
        config.setLockKeepInterval(1);
        config.setLockTimeOut(10);

        RedisLockHandler handler = new AbstractRedisLockHandler() {

            private Jedis jedis = new Jedis("127.0.0.1", 6379);

            private ReentrantLock lock = new ReentrantLock();

            @Override
            public Long setNx(String key, String value) {
                try {
                    this.lock.lock();
                    jedis.connect();
                    return jedis.setnx(key, value);
                } finally {
                    jedis.close();
                    this.lock.unlock();
                }
            }

            @Override
            public Long expire(String key, int seconds) {
                try {
                    this.lock.lock();
                    jedis.connect();
                    return jedis.expire(key, seconds);
                } finally {
                    jedis.close();
                    this.lock.unlock();
                }
            }

            @Override
            public Long del(String key) {
                try {
                    this.lock.lock();
                    jedis.connect();
                    return jedis.del(key);
                } finally {
                    jedis.close();
                    this.lock.unlock();
                }
            }

            @Override
            public String get(String key) {
                try {
                    this.lock.lock();
                    jedis.close();
                    return jedis.get(key);
                } finally {
                    jedis.close();
                    this.lock.unlock();
                }
            }
        };

        lock.setConfig(config);
        lock.setRedisLockHandler(handler);
        lock.initLockEnvironment();
    }

    @Test
    public void lock() throws Exception {
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lock.lock("aaa", new CacheLockCallBack() {
                        @Override
                        public void onLockExpiredException(Throwable throwable) {
                            System.out.println("lock expired");
                        }

                        @Override
                        public void onOtherException(Throwable throwable) {
                            System.out.println("lock other exception");
                        }
                    });
                    try {
                        Thread.sleep(15000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    lock.unLock("aaa");
                } catch (LockExpiredException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (lock.tryLock("aaa", null)) {
                            System.out.println("get lock");
                        } else {
                            System.out.println("get lock failed");
                        }
                    } catch (LockExpiredException e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        thread1.start();
        Thread.sleep(1000);
        thread2.start();
        while (true) {
            Thread.sleep(100000);
        }
    }

    @Test
    public void tryLock() throws Exception {
    }

    @Test
    public void unLock() throws Exception {
    }

}
