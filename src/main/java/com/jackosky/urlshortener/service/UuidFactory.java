package com.jackosky.urlshortener.service;

import com.devskiller.friendly_id.FriendlyId;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UuidFactory {

  public String next() {
    return FriendlyId.toFriendlyId(UUID.randomUUID());
  }
}
