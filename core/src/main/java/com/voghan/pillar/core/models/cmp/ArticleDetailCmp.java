package com.voghan.pillar.core.models.cmp;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.commons.link.LinkManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.voghan.pillar.core.models.ArticleDetail;
import com.voghan.pillar.core.models.cfm.ArticleDetailCfm;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
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
        ArticleDetail.class, ComponentExporter.class
    },
    resourceType = ArticleDetailCmp.RESOURCE_TYPE,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ArticleDetailCmp extends BaseModelCmp  implements ArticleDetail {
  static final String RESOURCE_TYPE = "pillar/components/articledetail/v1/articledetail";

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Self
  private SlingHttpServletRequest request;

  @Self
  private LinkManager linkManager;

  @ValueMapValue
  private String fragmentPath;

  private ArticleDetail articleDetail;

  private Page parentPage;

  private boolean parentPageResolved = false;

  @PostConstruct
  protected void init() {
    logger.debug("initializing ArticleDetailCmp");
    articleDetail = new ArticleDetailCfm();

    String[] selectors = request.getRequestPathInfo().getSelectors();
    if (selectors == null || selectors.length == 0) return;

    String articleName = selectors[0];
    String language = request.getLocale().getLanguage();
    String articlePath = articleName + "/jcr:content/data/" + language;

    logger.debug("Current article name: {}", articleName);
    logger.debug("Current path: {}", articlePath);
    logger.debug("Current fragment Path: {}", fragmentPath);

    if (fragmentPath != null) {
      Resource resource = request.getResourceResolver().getResource(fragmentPath);
      if (resource != null && resource.getChild(articlePath) != null) {
        articleDetail = resource.getChild(articlePath).adaptTo(ArticleDetailCfm.class);
        logger.debug("ArticleDetailCmp found articleDetail: {}", articleDetail);
      }
    }
  }

  @Override
  public String getHeadline() {
    return articleDetail.getHeadline();
  }

  @Override
  public String getSubheadline() {
    return articleDetail.getSubheadline();
  }

  @Override
  public String getDescription() {
    return articleDetail.getDescription();
  }

  @Override
  public String getBannerImageUrl() {
    return articleDetail.getBannerImageUrl();
  }

  @Override
  public String getPostDate() {
    return articleDetail.getPostDate();
  }

  @Override
  public String getUrl() {
    return articleDetail.getUrl();
  }

  @Override
  public String getContent() {
    return articleDetail.getContent();
  }

  public String getParentPageTitle() {
    initParentPage();
    if (parentPage == null) return null;
    String navTitle = parentPage.getNavigationTitle();
    return navTitle != null ? navTitle : parentPage.getTitle();
  }

  public String getParentPagePath() {
    initParentPage();
    if (parentPage == null) return null;
    Link link = linkManager.get(parentPage).build();
    return link.getURL();
  }

  public boolean hasArticle() {
    return articleDetail != null && StringUtils.isNotBlank(articleDetail.getUrl());
  }

  private void initParentPage() {
    if (!parentPageResolved) {
      parentPageResolved = true;
      PageManager pageManager = request.getResourceResolver().adaptTo(PageManager.class);
      if (pageManager != null) {
        Page currentPage = pageManager.getContainingPage(request.getResource());
        if (currentPage != null) {
          parentPage = currentPage.getParent();
        }
      }
    }
  }
}
