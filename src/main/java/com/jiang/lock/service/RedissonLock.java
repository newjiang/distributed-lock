package com.jiang.lock.service;

import com.jiang.lock.config.LockConst;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 基于Redisson实现的分布式锁服务
 */
@Slf4j
@Service
public class RedissonLock implements ILockService {

    @Autowired
    private RedissonClient client;

    @Autowired
    private SeckillService seckillService;

    @Override
    public String alpha() {
        return lock();
    }

    @Override
    public String beta() {
        return lock();
    }

    private String lock() {
        RLock lock = client.getLock(LockConst.REDIS_LOCK);
        lock.lock(10, TimeUnit.SECONDS);
        try {
            return seckillService.execute();
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}