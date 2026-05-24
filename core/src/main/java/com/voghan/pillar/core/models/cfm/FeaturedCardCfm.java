package com.voghan.pillar.core.models.cfm;

import com.voghan.pillar.core.models.FeaturedCard;
import javax.annotation.PostConstruct;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class FeaturedCardCfm extends CardCfm implements FeaturedCard {

  @Self
  private Resource resource;

  @ValueMapValue
  private String subheadline;

  @ValueMapValue
  private String image;

  @PostConstruct
  protected void init() {
    buildCallToActions();
  }

  @Override
  public String getSubheadline() {
    return subheadline;
  }

  @Override
  public String getImage() {
    return image;
  }
}
