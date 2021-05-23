package com.jiang.lock.service;

import com.jiang.lock.config.LockConst;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 基于Curator的分布式锁服务
 */
@Slf4j
@Service
public class CuratorLock implements ILockService {

    @Autowired
    private CuratorFramework client;

    @Autowired
    private SeckillService service;

    @Override
    public String alpha() {
        return lock();
    }

    @Override
    public String beta() {
        return lock();
    }

    private String lock() {
        InterProcessMutex lock = new InterProcessMutex(client, LockConst.ZK_LOCK);
        try {
            lock.acquire();
            return service.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (lock.isAcquiredInThisProcess()) {
                try {
                    lock.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
