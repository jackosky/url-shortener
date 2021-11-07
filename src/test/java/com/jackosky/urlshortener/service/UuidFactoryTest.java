package com.jackosky.urlshortener.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class UuidFactoryTest {

  final UuidFactory underTest = new UuidFactory();

  @Test
  void next_generates_unique() {
    var first = underTest.next();
    var second = underTest.next();

    assertThat(first)
        .isNotEqualTo(second)
        .hasSize(10);
  }

}
