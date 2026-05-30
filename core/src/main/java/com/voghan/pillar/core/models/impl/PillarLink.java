package com.voghan.pillar.core.models.impl;

import com.voghan.pillar.common.links.model.SimpleLink;

public class PillarLink implements SimpleLink {

  private final String linkText;
  private final String linkPath;

  public PillarLink(String text, String url) {
    this.linkText = text;
    this.linkPath = url;
  }

  @Override
  public String getLinkText() {
    return linkText;
  }

  @Override
  public String getLinkPath() {
    return linkPath;
  }
}
