# 总结实现的一些spring-boot组件
## 一.基于redis的分布式锁
### 使用说明
#### 一、配置
核心类:RedisReentrantLockConfig，参数说明如下
```java
public class RedisReentrantLockConfig implements Serializable {

    private static final long serialVersionUID = -349252563748015280L;

    /**
     * 保持锁的线程池核心线程数
     */
    private int corePoolSize;

    /**
     * 保持锁的线程池最大线程数
     */
    private int maxPoolSize;

    /**
     * 锁超时时间
     */
    private int lockTimeOut;

    /**
     * 重置锁超时时间周期
     */
    private int lockKeepInterval;

}
```
#### 二、核心接口实现
考虑用到的redis封装类不同，比如有人直接用jedis有用Spring的RedisTemplate，从扩展性和灵活性上提供通用接口，根据具体情况实现即可
核心接口：RedisLockHandler
```java

public interface RedisLockHandler {

    /**
     * 获取锁
     * @param key 锁名（redis的key）
     * @param lockContent 存储内容，目前存储网卡地址+线程ID
     * @param timeOut 锁超时时间
     * @return 成功返回true否则false
     */
    boolean getLockOfKey(String key, String lockContent, int timeOut);

    /**
     * 保持锁，内部实现为更新锁超时时间
     * @param key 锁名（redis的key）
     * @param timeOut 锁超市时间
     * @return 成功返回true，否则false
     */
    boolean keepLockOfKey(String key, int timeOut);

    /**
     * 释放所
     * @param key
     * @return
     */
    boolean releaseLockOfKey(String key);

    /**
     * 获取锁内容
     * @param key
     * @return
     */
    String getLockContentOfKey(String key);

}
```
为简化实现，已提供抽象实现类，辅助实现，redis直接实现抽象类方法即可
AbstractRedisLockHandler
```java
public abstract class AbstractRedisLockHandler implements RedisLockHandler {

    /**
     * redis setnx 指令
     * @param key
     * @param value
     * @return
     */
    public abstract Long setNx(String key, String value);

    /**
     * redis expire 指令
     * @param key
     * @param seconds
     * @return
     */
    public abstract Long expire(String key, int seconds);

    /**
     * redis del 指令
     * @param key
     * @return
     */
    public abstract Long del(String key);

    /**
     * redis get 指令
     * @param key
     * @return
     */
    public abstract String get(String key);
}
```
#### 三、使用
```java 
public interface CacheLock {

    void lock(String key, CacheLockCallBack callBack) throws LockExpiredException;

    boolean tryLock(String key, CacheLockCallBack callBack) throws LockExpiredException;

    void unLock(String key) throws LockExpiredException;

}
```

#### 四、回调异常处理，加锁时传入CacheLockCallBack实现即可，当维持锁失败或者网络异常时会触发回调，业务方根据需要进行处理，有一定延迟，延迟时间一般情况下最大不会超过定时周期
```JAVA
public interface CacheLockCallBack {

    /**
     * 锁超时失效或key被错误清楚等
     * @param throwable
     */
    void onLockExpiredException(Throwable throwable);

    /**
     * 其他异常
     * @param throwable
     */
    void onOtherException(Throwable throwable);

}
```

#### 五、使用示例
```java
        lock = new RedisReentrantLock();

        RedisReentrantLockConfig config = new RedisReentrantLockConfig();
        config.setCorePoolSize(1);
        config.setMaxPoolSize(10);
        config.setLockKeepInterval(1);
        config.setLockTimeOut(10);

        // RedisLockHandler的简单实现
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

        // 设置配置
        lock.setConfig(config);
        // 设置LockHandler
        lock.setRedisLockHandler(handler);
        // 初始化锁环境
        lock.initLockEnvironment();
        
        // 后续即可直接调用lock.lock, lock.tryLock, lock.unLock等
```
