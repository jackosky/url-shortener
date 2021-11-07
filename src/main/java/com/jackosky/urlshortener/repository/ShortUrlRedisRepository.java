package com.jackosky.urlshortener.repository;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Implement custom repository as so far Spring does not support reactive CRUD repository for Redis
 * <p>
 * See: https://docs.spring.io/spring-data/data-redis/docs/current/reference/html/#redis:reactive
 */
@Repository
public class ShortUrlRedisRepository implements ShortUrlRepository {

  private final ReactiveStringRedisTemplate redisTemplate;
  private final int retentionTimeInSeconds;

  public ShortUrlRedisRepository(ReactiveStringRedisTemplate redisTemplate,
      @Value("${retentionTimeInSeconds}") int retentionTimeInSeconds) {
    this.redisTemplate = redisTemplate;
    this.retentionTimeInSeconds = retentionTimeInSeconds;
  }

  @Override
  public Mono<ShortUrlDto> save(ShortUrlDto shortUrl) {
    return redisTemplate.opsForValue()
        .set(shortUrl.getKey(), shortUrl.getOriginUrl(), Duration.ofSeconds(retentionTimeInSeconds))
        .filter(result -> result)
        .flatMap(result -> Mono.just(new ShortUrlDto(shortUrl.getKey(), shortUrl.getOriginUrl())))
        .switchIfEmpty(Mono.error(new IllegalStateException("Could not store short url")));
  }

  @Override
  public Mono<ShortUrlDto> findByKey(String key) {
    return redisTemplate.opsForValue().get(key)
        .flatMap(url -> Mono.just(new ShortUrlDto(key, url)));
  }
}
