package com.jackosky.urlshortener.repository;

import com.jackosky.urlshortener.dto.ShortUrlDto;
import reactor.core.publisher.Mono;

public interface ShortUrlRepository {

  Mono<ShortUrlDto> save(ShortUrlDto shortUrl);

  Mono<ShortUrlDto> findByKey(String key);

}
