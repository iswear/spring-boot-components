package me.iswear.springdistributedlock.redis;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import me.iswear.springdistributedlock.Lock;
import me.iswear.springdistributedlock.LockCallBack;
import me.iswear.springdistributedlock.exception.LockException;
import me.iswear.springdistributedlock.exception.LockExpiredException;
import me.iswear.springdistributedlock.utils.NetWorkUtils;

import java.net.SocketException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Data
public class RedisReentrantLock implements Lock {

    private static final String splitChar = "#";

    private ThreadPoolExecutor keepLockPoolExecutor;

    private ScheduledThreadPoolExecutor scheduledPoolExecutor;

    private RedisLockHandler redisLockHandler;

    private RedisReentrantLockConfig config;

    private Map<String, LockInfo> lockMap = new ConcurrentHashMap<>();

    private ReentrantLock scheduledPoolExecutorLock = new ReentrantLock();

    private String generateLockCacheContent(Long threadId) throws SocketException {
        return NetWorkUtils.getHardwareAddressHexStringByIndex(0) + splitChar + threadId;
    }

    private void dispatchKeepLockTask() {
        this.scheduledPoolExecutor.schedule(
                () -> {
                    try {
                        if (scheduledPoolExecutorLock.tryLock()) {
                            try {
                                Set<Map.Entry<String, LockInfo>> entries = lockMap.entrySet();
                                for (Map.Entry<String, LockInfo> entry : entries) {
                                    keepLockPoolExecutor.execute(() -> {
                                        try {
                                            if (!redisLockHandler.keepLockOfKey(entry.getKey(), config.getLockTimeOut())) {
                                                if (entry.getValue().getCallBack() != null) {
                                                    entry.getValue().getCallBack().onLockExpiredException(
                                                            new LockExpiredException("LockKey(" + entry.getKey() + ")已过期")
                                                    );
                                                }
                                            } else {
                                                if (entry.getValue().getCallBack() != null) {
                                                    String lockContent = generateLockCacheContent(entry.getValue().getThreadId());
                                                    String cacheLockContent = redisLockHandler.getLockContentOfKey(entry.getKey());
                                                    if (!lockContent.equals(cacheLockContent)) {
                                                        entry.getValue().getCallBack().onLockExpiredException(
                                                                new LockExpiredException("LockKey(" + entry.getKey() + ")已过期")
                                                        );
                                                    }
                                                }
                                            }
                                        } catch (Exception ex) {
                                            entry.getValue().getCallBack().onOtherException(ex);
                                            log.error("更新缓存失效时间异常", ex);
                                        }
                                    });
                                }
                            } finally {
                                scheduledPoolExecutorLock.unlock();
                            }
                        }
                    } catch (Exception ex) {
                        log.error("定时线程异常", ex);
                    } finally {
                        dispatchKeepLockTask();
                    }
                },
                config.getLockKeepInterval(),
                TimeUnit.SECONDS
        );
    }

    public void initLockEnvironment() {
        this.keepLockPoolExecutor.shutdown();
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
    public void lock(String key, LockCallBack callBack) throws LockExpiredException {
        try {
            String lockContent =  this.generateLockCacheContent(Thread.currentThread().getId());
            while (!this.redisLockHandler.getLockOfKey(key, lockContent, config.getLockTimeOut())) {
                LockInfo lockInfo = this.lockMap.get(key);
                if (lockInfo != null) {
                    if (lockInfo.getThreadId() == Thread.currentThread().getId()) {
                        String cacheLockContent = redisLockHandler.getLockContentOfKey(key);
                        if (lockContent.equals(cacheLockContent)) {
                            break;
                        } else {
                            this.lockMap.remove(key);
                            new LockExpiredException("LockKey(" + key + ")已过期");
                        }
                    }
                }

                try {
                    Thread.sleep(this.config.getLockKeepInterval() * 1000);
                } catch (InterruptedException e) {
                    log.error("线程中断异常", e);
                }
            }
            this.lockMap.put(key, new LockInfo(Thread.currentThread().getId(), callBack));
        } catch (SocketException ex) {
            throw new LockException("获取本机MAC地址异常", ex);
        }
    }

    @Override
    public boolean tryLock(String key, LockCallBack callBack) throws LockExpiredException {
        try {
            String lockContent = this.generateLockCacheContent(Thread.currentThread().getId());
            if (this.redisLockHandler.getLockOfKey(key, lockContent, config.getLockTimeOut())) {
                this.lockMap.put(key, new LockInfo(Thread.currentThread().getId(), callBack));
                return true;
            } else {
                LockInfo lockInfo = this.lockMap.get(key);
                if (lockInfo != null) {
                    if (lockInfo.getThreadId() == Thread.currentThread().getId()) {
                        String cacheLockContent = redisLockHandler.getLockContentOfKey(key);
                        if (lockContent.equals(cacheLockContent)) {
                            return true;
                        } else {
                            this.lockMap.remove(key);
                            throw new LockExpiredException("LockKey(" + key + ")已过期");
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        } catch (SocketException ex) {
            throw new LockException("获取本机MAC地址异常", ex);
        }
    }

    @Override
    public void unLock(String key) throws LockExpiredException {
        try {
            LockInfo lockInfo = this.lockMap.get(key);
            if (lockInfo != null) {
                String lockContent = this.generateLockCacheContent(lockInfo.getThreadId());
                String cacheLockContent = redisLockHandler.getLockContentOfKey(key);
                if (lockContent.equals(cacheLockContent)) {
                    this.redisLockHandler.releaseLockOfKey(key);
                    this.lockMap.remove(key);
                } else {
                    this.lockMap.remove(key);
                    throw new LockExpiredException("LockKey(" + key + ")已过期");
                }
            }
        } catch (SocketException ex) {
            throw new LockException("获取本机MAC地址异常", ex);
        }
    }

    @Data
    private class LockInfo {

        private long threadId;

        private LockCallBack callBack;

        public LockInfo(long threadId, LockCallBack callBack) {
            this.threadId = threadId;
            this.callBack = callBack;
        }

    }

}
