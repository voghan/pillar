package com.voghan.pillar.core.models.cfm;

import com.voghan.pillar.common.links.model.SimpleLink;
import com.voghan.pillar.core.models.Card;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CardCfm extends BaseModelCfm implements Card {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Self
  private Resource resource;

  @ValueMapValue
  private String headline;

  @ValueMapValue
  private String shortDescription;

  @ValueMapValue
  private List<String> callToActions;

  private final List<SimpleLink> callToActionLinks = new ArrayList<>();

  @PostConstruct
  protected void init() {

    buildCallToActions();
  }

  protected void buildCallToActions() {
    logger.info("building callToActions for {}", resource.getPath());
    String version = getVersion();

    if (callToActions != null && resource != null) {
      ResourceResolver resourceResolver = resource.getResourceResolver();
      for (String path : callToActions) {
        Resource cta = resourceResolver.getResource(path + "/jcr:content/data/" + version);
        if (cta != null) {
          SimpleLink link = cta.adaptTo(LinkCfm.class);
          callToActionLinks.add(link);
        }
      }
    }
  }

  @Override
  public String getHeadline() {
    return headline;
  }

  @Override
  public String getShortDescription() {
    return shortDescription;
  }

  @Override
  public List<SimpleLink> getCallToActions() {
    return new ArrayList<>(callToActionLinks);
  }

  @Override
  public boolean isCallToActionEnabled() {
    return !callToActionLinks.isEmpty();
  }
}
