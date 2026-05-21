package com.voghan.pillar.core.models.pages;

import com.adobe.cq.wcm.core.components.models.Page;
import com.voghan.pillar.core.models.CardPage;
import java.util.Arrays;
import javax.annotation.PostConstruct;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.via.ResourceSuperType;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Model(
    adaptables = SlingHttpServletRequest.class,
    adapters = CardPage.class,
    resourceType = CardPageModel.RESOURCE_TYPE,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class CardPageModel implements CardPage {

  public static final String RESOURCE_TYPE = "pillar/components/page/v1/page";
  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Self
  private SlingHttpServletRequest request;

  @Self
  @Via(type = ResourceSuperType.class)
  private Page currentPage;

  private String[] selectors;

  @PostConstruct
  protected void init() {
    // Get all selectors as an array
    selectors = request.getRequestPathInfo().getSelectors();
    logger.debug("Current selectors: " + Arrays.toString(selectors));
  }

  public String[] getSelectors() {
    return selectors;
  }

  @Override
  public @NotNull String getExportedType() {
    return RESOURCE_TYPE;
  }
}
