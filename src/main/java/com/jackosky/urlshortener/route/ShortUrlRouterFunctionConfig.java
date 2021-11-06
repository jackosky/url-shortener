package com.jackosky.urlshortener.route;

import static org.springframework.web.reactive.function.BodyExtractors.toMono;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import static org.springframework.web.reactive.function.server.ServerResponse.temporaryRedirect;

import com.jackosky.urlshortener.dto.CreateShortUrlRequest;
import com.jackosky.urlshortener.dto.CreateShortUrlResponse;
import com.jackosky.urlshortener.service.ShortUrlService;
import java.net.URI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Configuration
public class ShortUrlRouterFunctionConfig {

  private final ShortUrlService shortUrlService;

  public ShortUrlRouterFunctionConfig(ShortUrlService shortUrlService) {
    this.shortUrlService = shortUrlService;
  }

  @Bean
  public RouterFunction<ServerResponse> createShorUrl() {
    return route(POST("/short-url"),
        request -> request.body(toMono(CreateShortUrlRequest.class))
            .flatMap(shortUrlReq -> shortUrlService.createShortUrl(shortUrlReq.getUrl()))
            .map(shortUrlDto -> new CreateShortUrlResponse(shortUrlDto.getKey(), shortUrlDto.getOriginUrl()))
            .flatMap(response -> ok().contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(response), CreateShortUrlResponse.class)));
  }

  @Bean
  public RouterFunction<ServerResponse> makeRedirect() {
    return route(GET("/{urlKey}"),
        request -> Mono.just(request.pathVariable("urlKey"))
            .flatMap(shortUrlService::resolveUrlByKey)
            .flatMap(resolvedUrl -> temporaryRedirect(URI.create(resolvedUrl.getOriginUrl())).build())
            .switchIfEmpty(ServerResponse.notFound().build()));
  }
}
