package com.jiang.lock.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.StaticScriptSource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisLockDemo {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public void v1() {
        String lock = "LOCK_KEY";
        Boolean absent = redisTemplate.opsForValue().setIfAbsent(lock, "LOCK_VALUE");
        if (absent) {
            try {
                // todo 执行业务代码
            } finally {
                // 删除LOCK_KEY，防止死锁
                redisTemplate.delete(lock);
            }
        } else {
            log.error("获取锁失败");
        }
    }

    public void v2() {
        String lock = "LOCK_KEY";
        Boolean absent = redisTemplate.opsForValue().setIfAbsent(lock, "LOCK_VALUE");
        if (absent) {
            try {
                // 设置过期时间
                redisTemplate.expire(lock, 10L, TimeUnit.SECONDS);
                // todo 执行业务代码
            } finally {
                // 删除LOCK_KEY，防止死锁
                redisTemplate.delete(lock);
            }
        } else {
            log.error("获取锁失败");
        }
    }

    public void v3() {
        String lock = "LOCK_KEY";
        Boolean absent = redisTemplate.opsForValue().setIfAbsent(lock, "LOCK_VALUE", 10L, TimeUnit.SECONDS);
        if (absent) {
            try {
                // todo 执行业务代码
            } finally {
                // 删除LOCK_KEY，防止死锁
                redisTemplate.delete(lock);
            }
        } else {
            log.error("获取锁失败");
        }
    }

    public void v4() {
        String lock = "LOCK_KEY";
        // 保证value的唯一性
        String value = UUID.randomUUID().toString() + "_" + Thread.currentThread().getName();
        Boolean absent = redisTemplate.opsForValue().setIfAbsent(lock, "LOCK_VALUE", 10L, TimeUnit.SECONDS);
        if (absent) {
            try {
                // todo 执行业务代码
            } finally {
                // lua脚本
                String scriptString = "if redis.call('get', KEYS[1]) == ARGV[1] "
                        + "then "
                        + "    return redis.call('del', KEYS[1]) "
                        + "else "
                        + "    return 0 "
                        + "end";
                // 创建RedisScript对象，用于执行lua脚本
                DefaultRedisScript script = new DefaultRedisScript();
                script.setScriptSource(new StaticScriptSource(scriptString));
                script.setResultType(String.class);

                // 对应的是KEYS[1]
                List<String> keys = Collections.singletonList(lock);

                // 对应的是ARGV[1]
                List<String> argv = Collections.singletonList(value);

                Object execute = redisTemplate.execute(script, keys, argv);
                if ("1".equals(execute.toString())) {
                    log.info("删除锁成功");
                } else {
                    log.info("删除锁失败");
                }
            }
        } else {
            log.error("获取锁失败");
        }
    }

    public void v5() {
        String lock = "LOCK_KEY";
        // 保证value的唯一性
        String value = UUID.randomUUID().toString() + "_" + Thread.currentThread().getName();
        Boolean absent = redisTemplate.opsForValue().setIfAbsent(lock, "LOCK_VALUE", 10L, TimeUnit.SECONDS);
        if (absent) {
            try {
                // todo 执行业务代码
            } finally {
                while(true){
                    redisTemplate.watch(lock);
                    // 判断值是否符合预期
                    String result = redisTemplate.opsForValue().get(lock);
                    if(value.equals(result)){
                        redisTemplate.setEnableTransactionSupport(true);
                        redisTemplate.multi();
                        redisTemplate.delete(lock);
                        List<Object> list = redisTemplate.exec();
                        if (list == null) { // 执行失败则继续
                            continue;
                        }
                    }
                    // 执行成功，取消监听，推出循环
                    redisTemplate.unwatch();
                    break;
                }
            }
        } else {
            log.error("获取锁失败");
        }
    }

}
