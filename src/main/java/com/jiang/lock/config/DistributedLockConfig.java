package com.jiang.lock.config;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class DistributedLockConfig {

    @Autowired
    private CuratorConfig curatorConfig;

    @Autowired
    private RedissonConfig redissonConfig;

    @Bean
    public CuratorFramework curatorFramework() {
        return CuratorFrameworkFactory.newClient(
                curatorConfig.getService(),
                curatorConfig.getSessionTimeout(),
                curatorConfig.getConnectionTimeout(),
                new RetryNTimes(curatorConfig.getRetryCount(), curatorConfig.getRetryTime()));
    }

    @Bean
    public RedissonClient redissonClient() {
        Config config = getRedisConfig();
        return Redisson.create(config);
    }

    /**
     * 获取redis的配置
     *
     * @return 配置
     */
    private Config getRedisConfig() {
        Config config = new Config();
        if (redissonConfig.getCluster() != null) { // 集群模式
            // todo 待实现
            config.useClusterServers();
        } else if (redissonConfig.getSentinel() != null) { // 哨兵模式
            // todo 待实现
            config.useSentinelServers();
        } else { // 单机模式
            String url = String.format("redis://%s:%d", redissonConfig.getHost(), redissonConfig.getPort());
            Duration timeout = redissonConfig.getTimeout();
            long seconds = timeout.getSeconds();
            config.useSingleServer()
                    .setAddress(url)
                    .setPassword(redissonConfig.getPassword())
                    .setDatabase(redissonConfig.getDatabase())
                    .setTimeout(Integer.parseInt(String.valueOf(seconds)))
                    .setConnectionPoolSize(redissonConfig.getLettuce().getPool().getMaxActive())
                    .setConnectionMinimumIdleSize(redissonConfig.getLettuce().getPool().getMinIdle());
        }
        return config;
    }


}
