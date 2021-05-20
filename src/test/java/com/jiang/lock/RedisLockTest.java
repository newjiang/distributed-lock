package com.jiang.lock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RKeys;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisLockTest {

    @Autowired
    private RedissonClient redissonClient;

    @Test
    public void test() {
        RKeys keys = redissonClient.getKeys();
        Iterable<String> iterable = keys.getKeys();
        for (String s : iterable) {
            System.out.println("===" + s);
        }
    }
}
