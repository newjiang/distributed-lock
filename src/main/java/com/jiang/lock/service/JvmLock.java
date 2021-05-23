package com.jiang.lock.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JvmLock implements ILockService{

    @Autowired
    private SeckillService seckillService;

    Object alpha = new Object();

    Object beta = new Object();

    @Override
    public String alpha() {
        synchronized (alpha) {
            return seckillService.execute();
        }
    }

    @Override
    public String beta() {
        synchronized (beta) {
            return seckillService.execute();
        }
    }
}
