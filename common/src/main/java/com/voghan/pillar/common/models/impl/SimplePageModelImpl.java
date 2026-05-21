package com.voghan.pillar.common.models.impl;

import com.adobe.cq.wcm.core.components.models.Page;
import com.voghan.pillar.common.models.PillarPageModel;
import javax.annotation.PostConstruct;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.via.ResourceSuperType;

@Model(
    adaptables = SlingHttpServletRequest.class,
    adapters = PillarPageModel.class,
    resourceType = "pillar-common/components/page",
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class SimplePageModelImpl implements PillarPageModel {

  @Self
  private SlingHttpServletRequest request;

  @Self
  @Via(type = ResourceSuperType.class)
  private Page currentPage;

  @ValueMapValue(name = "jcr:title")
  private String pageTitle;

  @ValueMapValue
  private String jcrDescription;

  @ValueMapValue
  private boolean hideInNav;

  @PostConstruct
  protected void init() {
    if (pageTitle == null && currentPage != null) {
      pageTitle = currentPage.getTitle();
    }
  }

  @Override
  public String getPageTitle() {
    return pageTitle;
  }

  @Override
  public String getPageDescription() {
    return jcrDescription;
  }

  @Override
  public boolean isHideInNav() {
    return false;
  }

}
