package com.voghan.pillar.common.links.impl;

import com.voghan.pillar.common.links.model.SimpleLink;

public class SimpleLinkImpl implements SimpleLink {

  private String linkText;

  private String linkPath;

  public SimpleLinkImpl(String linkText, String linkPath) {
    this.linkText = linkText;
    this.linkPath = linkPath;
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
