package org.whispersystems.textsecuregcm.providers;

import com.codahale.metrics.health.HealthCheck;
import org.whispersystems.textsecuregcm.redis.FaultTolerantRedisCluster;

import java.util.concurrent.CompletableFuture;

public class RedisClusterHealthCheck extends HealthCheck {

    private final FaultTolerantRedisCluster redisCluster;

    public RedisClusterHealthCheck(final FaultTolerantRedisCluster redisCluster) {
        this.redisCluster = redisCluster;
    }

    @Override
    protected Result check() throws Exception {
        return CompletableFuture.allOf(redisCluster.withReadCluster(connection -> connection.async().masters().commands().ping()).futures())
                .thenApply(v -> Result.healthy())
                .exceptionally(Result::unhealthy)
                .get();
    }
}