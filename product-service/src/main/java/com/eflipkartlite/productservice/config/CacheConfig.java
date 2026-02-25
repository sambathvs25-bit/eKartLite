package com.eflipkartlite.productservice.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {
    private static final Logger log = LoggerFactory.getLogger(CacheConfig.class);

    @Bean
    CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisSerializer<Object> safeSerializer = safeJsonSerializer();
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                safeSerializer))
                .entryTtl(Duration.ofMinutes(10));
        return RedisCacheManager.builder(connectionFactory).cacheDefaults(config).build();
    }

    private RedisSerializer<Object> safeJsonSerializer() {
        GenericJackson2JsonRedisSerializer delegate = new GenericJackson2JsonRedisSerializer();
        return new RedisSerializer<>() {
            @Override
            public byte[] serialize(Object value) {
                return delegate.serialize(value);
            }

            @Override
            public Object deserialize(byte[] bytes) {
                try {
                    return delegate.deserialize(bytes);
                } catch (RuntimeException ex) {
                    // Treat stale/invalid cache payload as cache miss.
                    log.warn("Ignoring invalid cache payload and falling back to DB.", ex);
                    return null;
                }
            }
        };
    }

    @Bean
    CacheErrorHandler cacheErrorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception, org.springframework.cache.Cache cache, Object key) {
                log.warn("Cache GET failed for {} key {}. Falling back to DB.", cache != null ? cache.getName() : "unknown", key, exception);
            }

            @Override
            public void handleCachePutError(RuntimeException exception, org.springframework.cache.Cache cache, Object key, Object value) {
                log.warn("Cache PUT failed for {} key {}.", cache != null ? cache.getName() : "unknown", key, exception);
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, org.springframework.cache.Cache cache, Object key) {
                log.warn("Cache EVICT failed for {} key {}.", cache != null ? cache.getName() : "unknown", key, exception);
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, org.springframework.cache.Cache cache) {
                log.warn("Cache CLEAR failed for {}.", cache != null ? cache.getName() : "unknown", exception);
            }
        };
    }
}
