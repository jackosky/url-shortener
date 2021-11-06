package com.jackosky.urlshortener.handlers.impl;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import com.jackosky.urlshortener.dto.CreateShortUrlRequest;
import com.jackosky.urlshortener.dto.CreateShortUrlResponse;
import com.jackosky.urlshortener.handlers.AbstractValidationHandler;
import com.jackosky.urlshortener.service.ShortUrlService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class CreateShortUrlHandler extends AbstractValidationHandler<CreateShortUrlRequest, Validator> {

  private final ShortUrlService shortUrlService;

  public CreateShortUrlHandler(Validator validator, ShortUrlService shortUrlService) {
    super(CreateShortUrlRequest.class, validator);
    this.shortUrlService = shortUrlService;
  }

  @Override
  protected Mono<ServerResponse> processBody(CreateShortUrlRequest validBody, ServerRequest originalRequest) {
    return Mono.just(validBody)
        .flatMap(shortUrlReq -> shortUrlService.createShortUrl(shortUrlReq.getUrl()))
        .map(shortUrlDto -> new CreateShortUrlResponse(shortUrlDto.getKey(), shortUrlDto.getOriginUrl()))
        .flatMap(response -> ok().contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(response), CreateShortUrlResponse.class));
  }
}
