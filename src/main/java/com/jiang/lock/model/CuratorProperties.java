package com.jiang.lock.model;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.curator")
public class CuratorProperties {
    /**
     * zookeeper地址,集群以","分割
     */
    private String service = "127.0.0.1:2181";

    /**
     * 重试次数
     */
    private int retryCount = 5;

    /**
     * 重试间隔时间,单位ms
     */
    private int retryTime = 5000;

    /**
     * 会话超时时间,单位ms
     */
    private int sessionTimeout = 5000;

    /**
     * 连接超时时间,单位ms
     */
    private int connectionTimeout = 5000;

}
