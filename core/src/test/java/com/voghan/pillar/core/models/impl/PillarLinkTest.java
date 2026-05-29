package com.voghan.pillar.core.models.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PillarLinkTest {

  @Test
  void getLinkText_returnsConstructorText() {
    PillarLink link = new PillarLink("Read More", "/us/en/articles/downhill-skiing-wyoming");
    assertEquals("Read More", link.getLinkText());
  }

  @Test
  void getLinkPath_returnsConstructorPath() {
    PillarLink link = new PillarLink("Read More", "/us/en/articles/downhill-skiing-wyoming");
    assertEquals("/us/en/articles/downhill-skiing-wyoming", link.getLinkPath());
  }

  @Test
  void getLinkText_withNullText_returnsNull() {
    PillarLink link = new PillarLink(null, "/us/en/articles/downhill-skiing-wyoming");
    assertNull(link.getLinkText());
  }

  @Test
  void getLinkPath_withNullPath_returnsNull() {
    PillarLink link = new PillarLink("Read More", null);
    assertNull(link.getLinkPath());
  }
}
