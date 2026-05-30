package com.voghan.pillar.common.links;

import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.drew.lang.annotations.NotNull;
import com.voghan.pillar.common.links.impl.SimpleLinkImpl;
import com.voghan.pillar.common.links.model.SimpleLink;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Model(
    adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SimpleLinkBuilder {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Self
  private Resource resource;

  private String url;
  private String text;
  private Resource providedResource;
  private String providedPath;
  private String providedText;
  private Page page;
  private boolean dynamicUrl;

  private PageManager pageManager;
  private ResourceResolver resolver;


  @PostConstruct
  protected void init() {
    logger.debug("Initializing SimpleLinkBuilder");
  }

  public SimpleLinkBuilder withPage(Page page) {
    this.page = page;
    return this;
  }

  public SimpleLinkBuilder withResource(Resource resource) {
    this.providedResource = resource;
    return this;
  }

  public SimpleLinkBuilder withText(String text) {
    this.providedText = text;
    return this;
  }

  public SimpleLinkBuilder withPath(String path) {
    this.providedPath = path;
    return this;
  }

  public SimpleLinkBuilder withDynamicUrl(String url) {
    this.dynamicUrl = true;
    this.providedPath = url;
    return this;
  }

  public SimpleLink build() {

    if (page != null) {
      this.url = formatUrl(getPageUrl(page.getPath()));
      this.text = StringUtils.isNotBlank(page.getNavigationTitle()) ? page.getNavigationTitle() : page.getTitle();
    } else if (providedResource != null) {
      this.url = formatUrl(getPageUrl(resource.getPath()));
      this.text = providedResource.getValueMap().get("jcr:Tile", String.class);
    }

    // Override url if path provided
    if (providedPath != null) {
      this.url = getPageUrl(providedPath);
    }

    if (providedText != null) {
      this.text = providedText;
    }

    return new SimpleLinkImpl(text, url);
  }

  protected boolean isExternalLink(String url) {
    return StringUtils.isNotBlank(url) && !url.startsWith("/");
  }

  protected boolean isAssetLink(String url) {
    return StringUtils.isNotBlank(url) && url.startsWith("/content/dam/");
  }

  protected String getPageUrl(String path) {
    String url = path;

    try {
      if (isExternalLink(url)) return url;
      if (isAssetLink(url)) return url;

      String absPath = getAbsolutePath(path);

      if (dynamicUrl) {
        url = formatUrl(absPath);
      } else {
        pageManager = getPageManager();
        if (pageManager != null && pageManager.getPage(absPath) != null) {
          Page page = pageManager.getPage(absPath);
          url = formatUrl(findTargetPage(page));
        }
      }

    } catch (Exception e) {
      logger.info("Error occurred transforming {}", path, e);
    }

    return url;
  }

  protected String findTargetPage(@NotNull Page page) {
    String url = page.getPath();
    ValueMap valueMap = page.getProperties();
    if (valueMap.containsKey(NameConstants.PN_REDIRECT_TARGET)) {
      String redirectTarget = valueMap.get(NameConstants.PN_REDIRECT_TARGET, String.class);
      if (StringUtils.isNotBlank(redirectTarget)) {
        url = redirectTarget;
        Page targetPage = getPageManager().getPage(redirectTarget);
        if (targetPage != null) {
          url = findTargetPage(targetPage);
        }
      }
    }
    return url;
  }

  protected String formatUrl(String url) {
    String formattedUrl = url;
    if (!isExternalLink(url)) {
      formattedUrl = getResolver().map(url) + ".html";
    }

    return formattedUrl;
  }

  protected String getAbsolutePath(String path) {
    String absolutePath = path;

    if (!isExternalLink(path) && !path.startsWith("/content/")) {
      absolutePath = "/content/pillar" + path;
    }

    return absolutePath;
  }

  private PageManager getPageManager() {
    if (pageManager == null) {
      pageManager = getResolver().adaptTo(PageManager.class);
    }
    return pageManager;
  }

  private ResourceResolver getResolver() {
    if (resolver == null) {
      resolver = resource.getResourceResolver();
    }
    return resolver;
  }
}
