package com.jackosky.urlshortener.dto;

import lombok.Data;

@Data
public class ShortUrlDto {

  private final String key;
  private final String originUrl;

}
