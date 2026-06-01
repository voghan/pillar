package com.voghan.pillar.common.links.impl;

import com.voghan.pillar.common.links.SimpleLinkBuilderConfig;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@Component(service = SimpleLinkBuilderConfig.class)
@Designate(ocd = SimpleLinkBuilderConfigImpl.Config.class)
public class SimpleLinkBuilderConfigImpl implements SimpleLinkBuilderConfig {

  @ObjectClassDefinition(name = "Pillar - Simple Link Builder Configuration")
  public @interface Config {

    @AttributeDefinition(
        name = "Site Root",
        description = "Root content path used to resolve relative URLs (e.g. /content/pillar)")
    String siteRoot() default "/content/pillar";
  }

  private String siteRoot;

  @Activate
  protected void activate(Config config) {
    siteRoot = config.siteRoot();
  }

  @Override
  public String getSiteRoot() {
    return siteRoot;
  }
}
