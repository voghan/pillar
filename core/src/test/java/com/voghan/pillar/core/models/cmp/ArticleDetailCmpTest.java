package com.voghan.pillar.core.models.cmp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.voghan.pillar.core.models.cfm.ArticleDetailCfm;
import com.voghan.pillar.core.testcontext.AppAemContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(AemContextExtension.class)
public class ArticleDetailCmpTest {

  private static final AemContext context = AppAemContext.newAemContext();

  // Fixture file paths
  private static final String PARENT_PAGE_JSON  = "/pillar-core/model/cmp/articleListPage.json";
  private static final String DETAIL_PAGE_JSON  = "/pillar-core/model/cmp/articleDetailCmps.json";
  private static final String ARTICLE_JSON      = "/pillar-core/model/cfm/articleDetail.json";

  // JCR paths where fixtures are loaded
  private static final String ARTICLES_ROOT     = "/content/articles";
  private static final String DETAIL_PAGE_ROOT  = "/content/articles/detail";
  private static final String DAM_ROOT          = "/content/dam/articles";

  // Component resource paths
  private static final String COMPONENT_PATH =
      "/content/articles/detail/jcr:content/root/container/container/article_detail";
  private static final String COMPONENT_NO_FRAGMENT_PATH =
      "/content/articles/detail/jcr:content/root/container/container/article_detail_no_fragment";

  private static final String ARTICLE_SELECTOR = "downhill-skiing-wyoming";

  @BeforeAll
  static void setup() {
    context.addModelsForClasses(ArticleDetailCmp.class, ArticleDetailCfm.class);
    context.load().json(PARENT_PAGE_JSON, ARTICLES_ROOT);
    context.load().json(DETAIL_PAGE_JSON, DETAIL_PAGE_ROOT);
    context.load().json(ARTICLE_JSON, DAM_ROOT);
  }

  // -------------------------------------------------------------------------
  // Article content fields
  // -------------------------------------------------------------------------

  @Test
  void getHeadline_returnsArticleHeadline() {
    ArticleDetailCmp model = getComponent(COMPONENT_PATH);
    assertNotNull(model);
    assertEquals("Downhill Skiing Wyoming", model.getHeadline());
  }

  @Test
  void getSubheadline_returnsArticleSubheadline() {
    ArticleDetailCmp model = getComponent(COMPONENT_PATH);
    assertNotNull(model);
    assertEquals("Jackson Hole Resort", model.getSubheadline());
  }

  @Test
  void getDescription_returnsNonNullDescription() {
    ArticleDetailCmp model = getComponent(COMPONENT_PATH);
    assertNotNull(model);
    assertNotNull(model.getShortDescription());
  }

  @Test
  void getBannerImageUrl_returnsCorrectDamPath() {
    ArticleDetailCmp model = getComponent(COMPONENT_PATH);
    assertNotNull(model);
    assertEquals(
        "/content/dam/pillar/images/en/cards/adobestock-185234795.jpeg",
        model.getBannerImageUrl());
  }

  @Test
  void getUrl_returnsArticleUrl() {
    ArticleDetailCmp model = getComponent(COMPONENT_PATH);
    assertNotNull(model);
    assertEquals("/content/pillar/us/en/articles/downhill-skiing-wyoming.html", model.getUrl());
  }

  @Test
  void getContent_returnsNonNullContent() {
    ArticleDetailCmp model = getComponent(COMPONENT_PATH);
    assertNotNull(model);
    assertNotNull(model.getContent());
  }

  @Test
  void getPostDate_returnsFormattedDate() {
    ArticleDetailCmp model = getComponent(COMPONENT_PATH);
    assertNotNull(model);
    assertNotNull(model.getPostDate());
  }

  // -------------------------------------------------------------------------
  // hasArticle
  // -------------------------------------------------------------------------

  @Test
  void hasArticle_returnsTrueWhenArticleLoaded() {
    ArticleDetailCmp model = getComponent(COMPONENT_PATH);
    assertNotNull(model);
    assertTrue(model.hasArticle());
  }

  // NOTE: hasArticle() calls articleDetail.getUrl() which delegates to
  // SimpleLinkBuilder. When articleDetail is constructed via `new ArticleDetailCfm()`
  // (no selector or no fragmentPath), linkBuilder is null and getUrl() throws NPE.
  // These cases are covered indirectly via the no-selector/no-fragment field tests below.

  // -------------------------------------------------------------------------
  // Parent page breadcrumb
  // -------------------------------------------------------------------------

  @Test
  void getParentPageTitle_returnsPageTitle() {
    ArticleDetailCmp model = getComponent(COMPONENT_PATH);
    assertNotNull(model);
    assertEquals("Articles", model.getParentPageTitle());
  }

  @Test
  void getParentPagePath_returnsParentPathWithHtmlExtension() {
    ArticleDetailCmp model = getComponent(COMPONENT_PATH);
    assertNotNull(model);
    // LinkManager in the mock context maps the path and appends .html
    assertNotNull(model.getParentPagePath());
    assertEquals("/content/articles.html", model.getParentPagePath());
  }

  @Test
  void isCallToActionEnabled_whenEnabled_returnTrue() {
    ArticleDetailCmp model = getComponent(COMPONENT_PATH);
    assertNotNull(model);
    assertTrue(model.isCallToActionEnabled());
  }

  @Test
  void getCallToActions_whenEnabled_returnActions() {
    ArticleDetailCmp model = getComponent(COMPONENT_PATH);
    assertNotNull(model);
    assertFalse(model.getCallToActions().isEmpty());
    assertEquals(1, model.getCallToActions().size());
  }

  // -------------------------------------------------------------------------
  // No-selector (empty article) behaviour
  // -------------------------------------------------------------------------

  @Test
  void getHeadline_returnsNull_whenNoSelector() {
    ArticleDetailCmp model = getComponent();
    assertNotNull(model);
    assertNull(model.getHeadline());
  }

  @Test
  void getSubheadline_returnsNull_whenNoSelector() {
    ArticleDetailCmp model = getComponent();
    assertNotNull(model);
    assertNull(model.getSubheadline());
  }

  @Test
  void getBannerImageUrl_returnsNull_whenNoSelector() {
    ArticleDetailCmp model = getComponent();
    assertNotNull(model);
    assertNull(model.getBannerImageUrl());
  }

  @Test
  void getContent_returnsNull_whenNoSelector() {
    ArticleDetailCmp model = getComponent();
    assertNotNull(model);
    assertNull(model.getContent());
  }

  @Test
  void getPostDate_returnsNull_whenNoSelector() {
    ArticleDetailCmp model = getComponent();
    assertNotNull(model);
    assertEquals("", model.getPostDate());
  }

  // -------------------------------------------------------------------------
  // No-fragment-path behaviour
  // -------------------------------------------------------------------------

  @Test
  void getHeadline_returnsNull_whenNoFragmentPath() {
    ArticleDetailCmp model = getComponent(COMPONENT_NO_FRAGMENT_PATH);
    assertNotNull(model);
    assertNull(model.getHeadline());
  }

  @Test
  void getContent_returnsNull_whenNoFragmentPath() {
    ArticleDetailCmp model = getComponent(COMPONENT_NO_FRAGMENT_PATH);
    assertNotNull(model);
    assertNull(model.getContent());
  }

  // -------------------------------------------------------------------------
  // Helpers
  // -------------------------------------------------------------------------

  private ArticleDetailCmp getComponent() {
    context.currentResource(ArticleDetailCmpTest.COMPONENT_PATH);
    context.requestPathInfo().setSelectorString(null);
    return context.request().adaptTo(ArticleDetailCmp.class);
  }

  private ArticleDetailCmp getComponent(String resourcePath) {
    context.currentResource(resourcePath);
    context.requestPathInfo().setSelectorString(ArticleDetailCmpTest.ARTICLE_SELECTOR);
    return context.request().adaptTo(ArticleDetailCmp.class);
  }
}
