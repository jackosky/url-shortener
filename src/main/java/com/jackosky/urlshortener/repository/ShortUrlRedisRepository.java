package com.jackosky.urlshortener.repository;

import com.jackosky.urlshortener.dto.ShortUrlDto;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class ShortUrlRedisRepository implements ShortUrlRepository {

  private final ReactiveValueOperations<String, String> ops;
  private final int retentionTimeInSeconds;

  public ShortUrlRedisRepository(ReactiveStringRedisTemplate redisTemplate,
      @Value("${retentionTimeInSeconds}") int retentionTimeInSeconds) {
    this.ops = redisTemplate.opsForValue();
    this.retentionTimeInSeconds = retentionTimeInSeconds;
  }

  @Override
  public Mono<ShortUrlDto> save(ShortUrlDto shortUrl) {
    return ops.set(shortUrl.getKey(), shortUrl.getOriginUrl(), Duration.ofSeconds(retentionTimeInSeconds))
        .flatMap(result -> Mono.just(new ShortUrlDto(shortUrl.getKey(), shortUrl.getOriginUrl())));
  }

  @Override
  public Mono<ShortUrlDto> findByKey(String key) {
    return ops.get(key)
        .flatMap(url -> Mono.just(new ShortUrlDto(key, url)));
  }
}
