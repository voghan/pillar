package com.voghan.pillar.core.models.cmp;

import com.voghan.pillar.core.models.cfm.ArticleDetailCfm;
import com.voghan.pillar.core.testcontext.AppAemContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@ExtendWith(AemContextExtension.class)
public class ArticleDetailCmpTest {

  private static final AemContext context = AppAemContext.newAemContext();

  private static final String PAGE_PATH = "/pillar-core/model/cmp/articleDetailCmps.json";
  private static final String ARTICLE_PATH = "/pillar-core/model/cfm/articleDetail.json";

  private static final String COMPONENT_PATH =
      "/content/article-pages/jcr:content/root/container/container/article_detail";

  @BeforeAll
  static void setup() {
    context.addModelsForClasses(ArticleDetailCmp.class, ArticleDetailCfm.class);
    context.load().json(PAGE_PATH, "/content/article-pages");
    context.load().json(ARTICLE_PATH, "/content/dam/articles");
  }

  @Test
  void getHeadline_default() {
    ArticleDetailCmp model = getComponent("downhill-skiing-wyoming");
    assertNotNull(model);
    assertEquals("Downhill Skiing Wyoming", model.getHeadline());
  }

  @Test
  void getSubheadline_default() {
    ArticleDetailCmp model = getComponent("downhill-skiing-wyoming");
    assertNotNull(model);
    assertEquals("Jackson Hole Resort", model.getSubheadline());
  }

  @Test
  void getDescription_default() {
    ArticleDetailCmp model = getComponent("downhill-skiing-wyoming");
    assertNotNull(model);
    assertNotNull(model.getDescription());
  }

  @Test
  void getBannerImageUrl_default() {
    ArticleDetailCmp model = getComponent("downhill-skiing-wyoming");
    assertNotNull(model);
    assertEquals("/content/dam/pillar/images/en/cards/adobestock-185234795.jpeg", model.getBannerImageUrl());
  }

  @Test
  void getUrl_default() {
    ArticleDetailCmp model = getComponent("downhill-skiing-wyoming");
    assertNotNull(model);
    assertEquals("/us/en/articles/downhill-skiing-wyoming", model.getUrl());
  }

  @Test
  void getContent_default() {
    ArticleDetailCmp model = getComponent("downhill-skiing-wyoming");
    assertNotNull(model);
    assertNotNull(model.getContent());
  }

  @Test
  void getPostDate_default() {
    ArticleDetailCmp model = getComponent("downhill-skiing-wyoming");
    assertNotNull(model);
    assertNotNull(model.getPostDate());
  }

  private ArticleDetailCmp getComponent(String articleSelector) {
    context.currentResource(COMPONENT_PATH);
    context.requestPathInfo().setSelectorString(articleSelector);
    return context.request().adaptTo(ArticleDetailCmp.class);
  }
}
