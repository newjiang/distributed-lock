package com.jiang.lock.config;

import com.jiang.lock.model.CuratorProperties;
import com.jiang.lock.model.RedissonProperties;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

/**
 * 分布式锁的配置
 */
@Configuration
public class DistributedLockConfig {

    @Autowired
    private CuratorProperties curatorProperties;

    @Autowired
    private RedissonProperties redissonProperties;

    @Bean
    public CuratorFramework curatorFramework() {
        CuratorFramework client = CuratorFrameworkFactory.newClient(
                curatorProperties.getService(),
                curatorProperties.getSessionTimeout(),
                curatorProperties.getConnectionTimeout(),
                new RetryNTimes(curatorProperties.getRetryCount(), curatorProperties.getRetryTime()));
        client.start();
        return client;
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
        if (Objects.nonNull(redissonProperties.getCluster())) {
            // 集群模式
            String[] nodes = redissonProperties.getSentinel().getNodes().split(",");
            for (int i = 0; i < nodes.length; i++) {
                nodes[i] = "redis://" + nodes[i];
            }
            config.useClusterServers()
                    .addNodeAddress(nodes)
                    .setPassword(redissonProperties.getPassword()) // 密码
                    .setTimeout(redissonProperties.getTimeout());  // 超时时间
        } else if (Objects.nonNull(redissonProperties.getSentinel())) {
            // 哨兵模式
            String[] nodes = redissonProperties.getCluster().getNodes().split(",");
            for (int i = 0; i < nodes.length; i++) {
                nodes[i] = "redis://" + nodes[i];
            }
            config.useSentinelServers()
                    .addSentinelAddress(nodes) // 节点
                    .setPassword(redissonProperties.getPassword()) // 密码
                    .setTimeout(redissonProperties.getTimeout());  // 超时时间
        } else {
            // 单机模式
            String address = String.format("redis://%s:%d", redissonProperties.getHost(), redissonProperties.getPort());
            config.useSingleServer()
                    .setAddress(address)
                    .setPassword(redissonProperties.getPassword())
                    .setDatabase(redissonProperties.getDatabase())
                    .setTimeout(redissonProperties.getTimeout())
                    .setConnectionPoolSize(redissonProperties.getLettuce().getPool().getMaxActive())
                    .setConnectionMinimumIdleSize(redissonProperties.getLettuce().getPool().getMinIdle());
        }
        return config;
    }
}
