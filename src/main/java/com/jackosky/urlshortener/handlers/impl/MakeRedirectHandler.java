package com.jackosky.urlshortener.handlers.impl;

import static org.springframework.web.reactive.function.server.ServerResponse.temporaryRedirect;

import com.jackosky.urlshortener.service.ShortUrlService;
import java.net.URI;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class MakeRedirectHandler {

  private final ShortUrlService shortUrlService;

  public MakeRedirectHandler(ShortUrlService shortUrlService) {
    this.shortUrlService = shortUrlService;
  }

  public Mono<ServerResponse> handleRequest(ServerRequest request) {
    return Mono.just(request.pathVariable("urlKey"))
        .flatMap(shortUrlService::resolveUrlByKey)
        .flatMap(resolvedUrl -> temporaryRedirect(URI.create(resolvedUrl.getOriginUrl())).build())
        .switchIfEmpty(ServerResponse.notFound().build());
  }
}
