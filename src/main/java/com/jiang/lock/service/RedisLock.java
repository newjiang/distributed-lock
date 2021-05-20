package com.jiang.lock.service;

import com.jiang.lock.config.RedissonConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RedisLock {

    @Autowired
    private RedissonConfig redissonConfig;


    public void lock() {
        log.error("redissionConfig = {}", redissonConfig);
    }

}
