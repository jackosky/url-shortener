package com.jackosky.urlshortener.route;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import com.jackosky.urlshortener.handlers.impl.CreateShortUrlHandler;
import com.jackosky.urlshortener.handlers.impl.MakeRedirectHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ShortUrlRouterFunctionConfig {

  private final CreateShortUrlHandler createShortUrlHandler;
  private final MakeRedirectHandler makeRedirectHandler;

  public ShortUrlRouterFunctionConfig(CreateShortUrlHandler createShortUrlHandler,
      MakeRedirectHandler makeRedirectHandler) {
    this.createShortUrlHandler = createShortUrlHandler;
    this.makeRedirectHandler = makeRedirectHandler;
  }

  @Bean
  public RouterFunction<ServerResponse> createShorUrl() {
    return route(POST("/short-url"), createShortUrlHandler::handleRequest);
  }

  @Bean
  public RouterFunction<ServerResponse> makeRedirect() {
    return route(GET("/{urlKey}"), makeRedirectHandler::handleRequest);
  }
}
