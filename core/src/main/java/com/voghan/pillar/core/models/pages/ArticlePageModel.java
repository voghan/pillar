package com.voghan.pillar.core.models.pages;

import com.adobe.cq.wcm.core.components.models.Page;
import com.voghan.pillar.core.models.ArticlePage;
import com.voghan.pillar.core.models.SeoData;
import java.util.ArrayList;
import java.util.List;
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
    adapters = ArticlePage.class,
    resourceType = ArticlePageModel.RESOURCE_TYPE,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class ArticlePageModel implements ArticlePage {

  public static final String RESOURCE_TYPE = "pillar/components/pagearticle/v1/pagearticle";
  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Self
  private SlingHttpServletRequest request;

  @Self
  @Via(type = ResourceSuperType.class)
  private Page currentPage;

  private SeoData seoData;

  private String[] selectors;

  @PostConstruct
  protected void init() {
    // Get all selectors as an array
    selectors = request.getRequestPathInfo().getSelectors();
    seoData = request.adaptTo(SeoData.class);
    if (seoData != null) {
      logger.debug("Current seo data: " + seoData);
    } else {
      logger.debug("Current seo data is null");
    }
  }

  public String[] getSelectors() {
    return selectors;
  }

  @Override
  public String getTitle() {
    if (seoData != null) return seoData.getTitle();
    return currentPage != null ? currentPage.getTitle() : null;
  }

  @Override
  public String getDescription() {
    if (seoData != null) return seoData.getDescription();
    return currentPage != null ? currentPage.getDescription() : null;
  }

  @Override
  public String getCanonicalLink() {
    if (seoData != null) return seoData.getCanonicalUrl();
    return currentPage != null ? currentPage.getCanonicalLink() : null;
  }

  @Override
  public List<String> getRobotsTags() {
    if (seoData != null) return seoData.getRobots();
    return currentPage != null ? currentPage.getRobotsTags() : new ArrayList<>();
  }

  @Override
  public @NotNull String getExportedType() {
    return RESOURCE_TYPE;
  }
}
