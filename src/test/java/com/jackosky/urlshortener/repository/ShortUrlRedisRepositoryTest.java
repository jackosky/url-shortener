package com.jackosky.urlshortener.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jackosky.urlshortener.dto.ShortUrlDto;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Mono;

class ShortUrlRedisRepositoryTest {

  final ReactiveStringRedisTemplate mockTemplate = mock(ReactiveStringRedisTemplate.class);

  final ShortUrlRepository shortUrlRepository = new ShortUrlRedisRepository(mockTemplate, 5);


  @Test
  void test_save_repository_save_success() {
    ReactiveValueOperations<String, String> mockOps = mock(ReactiveValueOperations.class);
    when(mockTemplate.opsForValue()).thenReturn(mockOps);
    when(mockOps.set(any(), any(), any())).thenReturn(Mono.just(true));

    String expectedUrl = "http://youtube.com";
    shortUrlRepository.save(new ShortUrlDto("key", expectedUrl)).block();

    verify(mockOps, times(1)).set("key", expectedUrl, Duration.ofSeconds(5));
  }

  @Test
  void test_save_repository_save_error() {
    ReactiveValueOperations<String, String> mockOps = mock(ReactiveValueOperations.class);
    when(mockTemplate.opsForValue()).thenReturn(mockOps);
    when(mockOps.set(any(), any(), any())).thenReturn(Mono.just(false));

    String expectedUrl = "http://youtube.com";

    var dto = new ShortUrlDto("key", expectedUrl);
    var mono = shortUrlRepository.save(dto);
    assertThatThrownBy(mono::block)
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Could not store short url");

    verify(mockOps, times(1)).set("key", expectedUrl, Duration.ofSeconds(5));
  }

  @Test
  void test_findByKey_success() {
    String expectedUrl = "http://youtube.com";
    ReactiveValueOperations<String, String> mockOps = mock(ReactiveValueOperations.class);
    when(mockTemplate.opsForValue()).thenReturn(mockOps);
    when(mockOps.get(any())).thenReturn(Mono.just(expectedUrl));

    var mono = shortUrlRepository.findByKey("key");
    assertThat(mono.block())
        .extracting(ShortUrlDto::getKey, ShortUrlDto::getOriginUrl)
        .containsExactlyInAnyOrder("key", expectedUrl);

    verify(mockOps, times(1)).get("key");
  }

  @Test
  void test_findByKey_empty() {
    ReactiveValueOperations<String, String> mockOps = mock(ReactiveValueOperations.class);
    when(mockTemplate.opsForValue()).thenReturn(mockOps);
    when(mockOps.get(any())).thenReturn(Mono.empty());

    var mono = shortUrlRepository.findByKey("key");
    assertThat(mono.hasElement().block())
        .isFalse();

    verify(mockOps, times(1)).get("key");
  }

}
