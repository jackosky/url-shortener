package com.jackosky.urlshortener.service;

import com.jackosky.urlshortener.dto.ShortUrlDto;
import com.jackosky.urlshortener.repository.ShortUrlRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ShortUrlService {

  private final ShortUrlRepository shortUrlRepository;
  private final UuidFactory uuidFactory;

  public ShortUrlService(ShortUrlRepository shortUrlRepository, UuidFactory uuidFactory) {
    this.shortUrlRepository = shortUrlRepository;
    this.uuidFactory = uuidFactory;
  }


  public Mono<ShortUrlDto> createShortUrl(String originUrl) {
    return Mono.just(new ShortUrlDto(uuidFactory.next(), originUrl))
        .flatMap(shortUrlRepository::save);
  }

  /**
   * Resolves url and increases duration of availability
   */
  public Mono<ShortUrlDto> resolveUrlByKey(String urlKey) {
    return shortUrlRepository.findByKey(urlKey)
        .flatMap(found -> shortUrlRepository.save(new ShortUrlDto(found.getKey(), found.getOriginUrl())));
  }
}
