package com.jiang.lock.service;

import com.jiang.lock.config.LockConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class SeckillService {

    @Autowired
    private StringRedisTemplate template;

    public String execute() {
        String value = template.opsForValue().get(LockConst.GOODS);
        int count = StringUtils.isEmpty(value) ? 0 : Integer.parseInt(value);
        if (count > 0) {
            template.opsForValue().set(LockConst.GOODS, String.valueOf(count - 1));
            log.info("秒杀[{}]成功", value);
            return value;
        } else {
            String failure = "谢谢惠顾";
            log.info(failure);
            return failure;
        }
    }

    public void before() {
        template.opsForValue().set(LockConst.GOODS, "100");
    }

    public String after() {
        return template.opsForValue().get(LockConst.GOODS);
    }
}
