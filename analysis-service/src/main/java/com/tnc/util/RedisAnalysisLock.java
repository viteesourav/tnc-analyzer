package com.tnc.util;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

/*
    Role of this Class is to implement a Strong atomic Redis Distributed lock on Redis.
    Reason: 
        We need this to support 2 concurrent req having same payload, hitting our service simulataneously.
        Distributed lock allows the system to safely executes 1 req, update the cacehe and let the other req access from cache. Thus saving tokens and optmizing performance.
    Supporting a req:
        1. To Acquire a lock safely on cacheKey using uniquq uuid token as lockTokens. uuid token is unique to every reqs.
        2. To safely remove the token. Lua Script is used to ensure atomic GET + Delete Opeeration on the cacheKey.

*/
@Component
public class RedisAnalysisLock {

    private static final String LOCK_SUFFIX = ":lock";
    private static final long LOCK_TIMEOUT_SECONDS = 60;
    
    // injecting dependency using constructore injection..
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisAnalysisLock(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    // @method: Acquire the lock on lockKey [cacheKey + lock-suffix] using uuid.
    public String acquireLock(String cacheKey) {

        String lockKey = cacheKey + LOCK_SUFFIX;

        String lockToken = UUID.randomUUID().toString();

        // NOTE: .setIfAbsent() -> this works only if that lockKey is new, if already Lock Key exist, it will be False.
        Boolean acquired = redisTemplate.opsForValue().setIfAbsent(
                    lockKey,
                    lockToken,
                    LOCK_TIMEOUT_SECONDS,
                    TimeUnit.SECONDS
                );
        
        // If lock qcquired successfully -> it sends back a unique token to the req for tracking...
        return Boolean.TRUE.equals(acquired) 
                ? lockToken 
                : null;

    }

    // @method: ReleaseLock -> it ensure atomic operation using the cacheKey and token value.
    public void releaseLock(String cacheKey, String lockToken) {

        String lockKey = cacheKey + LOCK_SUFFIX;

        // Lua Script: Get the key. If it matches the token, Delete it. Return 1 else 0.
        String script = """
                if redis.call('get', KEYS[1]) == ARGV[1] then
                    return redis.call('del', KEYS[1])
                else
                    return 0
                end
                """;
        
        redisTemplate.execute(
            new DefaultRedisScript<>(script, Long.class), 
            List.of(lockKey), 
            lockToken
        );
    }

    
    
}
