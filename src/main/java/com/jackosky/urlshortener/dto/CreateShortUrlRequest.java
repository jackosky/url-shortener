package com.jackosky.urlshortener.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateShortUrlRequest {

  @NotEmpty
  @NotNull
  @URL
  private String url;

}
