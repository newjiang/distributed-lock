package com.jiang.lock.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.redis")
public class RedissonProperties {
    private int database = 0;
    private String url;
    private String host = "localhost";
    private String password;
    private int port = 6379;
    private boolean ssl;
    private int timeout;
    private RedissonProperties.Sentinel sentinel;
    private RedissonProperties.Cluster cluster;
    private final RedissonProperties.Lettuce lettuce = new RedissonProperties.Lettuce();

    @Data
    public class Lettuce {
        private int shutdownTimeout = 100;
        private RedissonProperties.Pool pool = new RedissonProperties.Pool();
    }

    @Data
    public class Sentinel {
        private String master;
        private String nodes;
    }

    @Data
    public class Cluster {
        private String nodes;
        private Integer maxRedirects;
    }

    @Data
    public class Pool {
        private int maxIdle = 8;
        private int minIdle = 0;
        private int maxActive = 8;
        private int maxWait = -1;
    }
}
