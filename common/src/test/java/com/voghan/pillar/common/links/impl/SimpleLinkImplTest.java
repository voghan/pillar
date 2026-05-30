package com.voghan.pillar.common.links.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class SimpleLinkImplTest {

  @Test
  void getLinkText_returnsConstructorText() {
    SimpleLinkImpl link = new SimpleLinkImpl("Read More", "/us/en/articles/test");
    assertEquals("Read More", link.getLinkText());
  }

  @Test
  void getLinkPath_returnsConstructorPath() {
    SimpleLinkImpl link = new SimpleLinkImpl("Read More", "/us/en/articles/test");
    assertEquals("/us/en/articles/test", link.getLinkPath());
  }

  @Test
  void getLinkText_withNullText_returnsNull() {
    SimpleLinkImpl link = new SimpleLinkImpl(null, "/us/en/articles/test");
    assertNull(link.getLinkText());
  }

  @Test
  void getLinkPath_withNullPath_returnsNull() {
    SimpleLinkImpl link = new SimpleLinkImpl("Read More", null);
    assertNull(link.getLinkPath());
  }
}
