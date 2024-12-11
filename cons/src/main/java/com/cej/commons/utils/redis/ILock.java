package com.cej.commons.utils.redis;

/**
 * 分布式锁
 *
 * */
public interface ILock {

    /**
     * 尝试获取锁
     * @param timeSec 超时时间
     * @return true false
     * */
    boolean tryLock(long timeSec);

    void unlock();
}
