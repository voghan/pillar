package com.voghan.pillar.core.models.cfm;

import com.voghan.pillar.common.links.SimpleLinkBuilder;
import com.voghan.pillar.common.links.model.SimpleLink;
import javax.annotation.PostConstruct;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Model(adaptables = Resource.class)
public class LinkCfm extends BaseModelCfm implements SimpleLink {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Self
  private SimpleLinkBuilder linkBuilder;

  @ValueMapValue
  private String linkText;

  @ValueMapValue
  private String linkPath;

  private SimpleLink link;

  @PostConstruct
  protected void init() {
    link = linkBuilder.withPath(linkPath).withText(linkText).build();
  }

  @Override
  public String getLinkText() {
    return link != null ? link.getLinkText() : linkText;
  }

  @Override
  public String getLinkPath() {
    return link != null ? link.getLinkPath() : linkPath;
  }
}
