package com.voghan.pillar.core.models.cmp;

import com.adobe.cq.export.json.ComponentExporter;
import com.voghan.pillar.core.models.FeaturedCard;
import com.voghan.pillar.core.models.Link;
import com.voghan.pillar.core.models.cfm.FeaturedCardCfm;
import java.util.List;
import javax.annotation.PostConstruct;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Model(
    adaptables = SlingHttpServletRequest.class,
    adapters = {
        FeaturedCard.class, ComponentExporter.class
    },
    resourceType = FeaturedCardCmp.RESOURCE_TYPE,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class FeaturedCardCmp extends BaseModelCmp implements FeaturedCard {
  static final String RESOURCE_TYPE = "pillar/components/featured/v1/featured";

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Self
  private SlingHttpServletRequest servletRequest;

  @ValueMapValue
  private String fragmentPath;

  @ValueMapValue
  private String variationName;

  private FeaturedCard featuredCard;

  @PostConstruct
  protected void init() {
    logger.info("Post Construct for {} version {}", fragmentPath, variationName);
    featuredCard = new FeaturedCardCfm();
    if (servletRequest != null) {
      Resource cfm = servletRequest.getResourceResolver()
          .getResource(fragmentPath + "/jcr:content/data/" + variationName);
      if (cfm != null) {
        logger.info("Found resource at {} ", cfm.getPath());
        featuredCard = cfm.adaptTo(FeaturedCardCfm.class);
      }
    }
  }

  @Override
  public String getSubheadline() {
    return featuredCard.getSubheadline();
  }

  @Override
  public String getImage() {
    return featuredCard.getImage();
  }

  @Override
  public String getHeadline() {
    return featuredCard.getHeadline();
  }

  @Override
  public String getShortDescription() {
    return featuredCard.getShortDescription();
  }

  @Override
  public List<Link> getCallToActions() {
    return featuredCard.getCallToActions();
  }

  @Override
  public boolean isCallToActionEnabled() {
    return featuredCard.isCallToActionEnabled();
  }
}
