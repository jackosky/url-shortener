package com.jackosky.urlshortener.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jackosky.urlshortener.repository.ShortUrlDto;
import com.jackosky.urlshortener.repository.ShortUrlRepository;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

class ShortUrlServiceTest {

  final ShortUrlRepository mockRepository = mock(ShortUrlRepository.class);
  final UuidFactory mockUuidFactory = mock(UuidFactory.class);

  final ShortUrlService underTest = new ShortUrlService(mockRepository, mockUuidFactory);


  @Test
  void test_createShortUrl() {
    String uuid = "uuid1";
    String url = "http://youtube.com";
    when(mockUuidFactory.next()).thenReturn(uuid);
    when(mockRepository.save(any())).thenReturn(Mono.just(new ShortUrlDto(uuid, url)));

    assertThat(underTest.createShortUrl(url))
        .extracting(Mono::block)
        .extracting(ShortUrlDto::getKey, ShortUrlDto::getOriginUrl)
        .containsExactlyInAnyOrder(uuid, url);
  }

  @Test
  void test_resolveUrlByKey() {
    String uuid = "uuid1";
    String url = "http://youtube.com";
    when(mockUuidFactory.next()).thenReturn(uuid);
    when(mockRepository.findByKey(any())).thenReturn(Mono.just(new ShortUrlDto(uuid, url)));
    when(mockRepository.save(any())).thenReturn(Mono.just(new ShortUrlDto(uuid, url)));

    assertThat(underTest.resolveUrlByKey(uuid))
        .extracting(Mono::block)
        .extracting(ShortUrlDto::getKey, ShortUrlDto::getOriginUrl)
        .containsExactlyInAnyOrder(uuid, url);

    verify(mockRepository, times(1)).findByKey(uuid);
    verify(mockRepository, times(1)).save(new ShortUrlDto(uuid, url));
  }


}
