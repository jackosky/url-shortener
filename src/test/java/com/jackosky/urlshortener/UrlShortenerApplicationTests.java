package com.jackosky.urlshortener;

import static org.assertj.core.api.Assertions.assertThat;

import com.jackosky.urlshortener.dto.CreateShortUrlResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

@SpringBootTest(classes = TestRedisConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UrlShortenerApplicationTests {

  @Autowired
  WebTestClient webTestClient;

  @Test
  void create_short_url_valid_url() {
    WebTestClient.ResponseSpec response = webTestClient
        .post()
        .uri("/short-url")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue("{\"url\":\"http://youtube.com\"}"))
        .exchange();

    response
        .expectStatus()
        .isOk();

    var responseBody = response
        .expectBody(CreateShortUrlResponse.class)
        .returnResult()
        .getResponseBody();
    assertThat(responseBody.getKey()).isNotBlank();
    assertThat(responseBody.getOriginUrl()).isEqualTo("http://youtube.com");
  }

  @Test
  void create_short_url_valid_invalid_url() {
    WebTestClient.ResponseSpec response = webTestClient
        .post()
        .uri("/short-url")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue("{\"url\":\"blablabla\"}"))
        .exchange();

    response
        .expectStatus()
        .isBadRequest();
  }

  @Test
  void make_redirect_when_link_still_available() {
    WebTestClient.ResponseSpec response = webTestClient
        .post()
        .uri("/short-url")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue("{\"url\":\"http://youtube.com\"}"))
        .exchange();

    response
        .expectStatus()
        .isOk();

    var responseBody = response
        .expectBody(CreateShortUrlResponse.class)
        .returnResult()
        .getResponseBody();

    response = webTestClient
        .get()
        .uri("/" + responseBody.getKey())
        .exchange();

    response
        .expectStatus()
        .isTemporaryRedirect()
        .expectHeader()
        .location("http://youtube.com");
  }

  @Test
  void return_404_when_link_expire() throws InterruptedException {
    WebTestClient.ResponseSpec response = webTestClient
        .post()
        .uri("/short-url")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue("{\"url\":\"http://youtube.com\"}"))
        .exchange();

    response
        .expectStatus()
        .isOk();

    var responseBody = response
        .expectBody(CreateShortUrlResponse.class)
        .returnResult()
        .getResponseBody();

    response = webTestClient
        .get()
        .uri("/" + responseBody.getKey())
        .exchange();

    response
        .expectStatus()
        .isTemporaryRedirect()
        .expectHeader()
        .location("http://youtube.com");

    //wait 6 seconds so that link will expire
    Thread.sleep(6000);

    response = webTestClient
        .get()
        .uri("/" + responseBody.getKey())
        .exchange();

    response
        .expectStatus()
        .isNotFound();
  }

}
