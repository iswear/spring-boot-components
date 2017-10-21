package me.iswear.springdistributedlock.redis;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import me.iswear.springdistributedlock.Lock;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Data
public class RedisReentrantLock implements Lock {

    private ThreadPoolExecutor keepLockPoolExecutor;

    private ScheduledThreadPoolExecutor scheduledPoolExecutor;

    private RedisLockHandler redisLockHandler;

    private RedisReentrantLockConfig config;

    private Map<String, Long> lockMap = new ConcurrentHashMap<>();

    private ReentrantLock scheduledPoolExecutorLock = new ReentrantLock();

    private void dispatchKeepLockTask() {
        this.scheduledPoolExecutor.schedule(
                () -> {
                    if (scheduledPoolExecutorLock.tryLock()) {
                        try {
                            Set<Map.Entry<String, Long>> entries = lockMap.entrySet();
                            for (Map.Entry<String, Long> entry : entries) {
                                keepLockPoolExecutor.execute(() -> redisLockHandler.keepLockOfKey(entry.getKey(), 10));
                            }
                        } finally {
                            scheduledPoolExecutorLock.unlock();
                        }
                    }
                    dispatchKeepLockTask();
                },
                config.getLockKeepInterval(),
                TimeUnit.SECONDS
        );
    }

    public void initLockEnvironment() {
        if (keepLockPoolExecutor == null) {
            this.keepLockPoolExecutor = new ThreadPoolExecutor(
                    this.config.getCorePoolSize(),
                    this.config.getLockTimeOut(),
                    0,
                    TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>()
            );
        }
        if (scheduledPoolExecutor == null) {
            this.scheduledPoolExecutor = new ScheduledThreadPoolExecutor(1);
        }
        this.dispatchKeepLockTask();
    }

    @Override
    public void Lock(String key) {
        while (!this.redisLockHandler.getLockOfKey(key, 10)) {
            Long threadId = this.lockMap.get(key);
            if (threadId != null && threadId == Thread.currentThread().getId()) {
                break;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                log.error("线程中断异常", e);
            }
        }
        this.lockMap.put(key, Thread.currentThread().getId());
    }

    @Override
    public boolean tryLock(String key) {
        if (this.redisLockHandler.getLockOfKey(key, 10)) {
            this.lockMap.put(key, Thread.currentThread().getId());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void unLock(String key) {
        this.redisLockHandler.releaseLockOfKey(key);
        this.lockMap.remove(key);
    }

}
