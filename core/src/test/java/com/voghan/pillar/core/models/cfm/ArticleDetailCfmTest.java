package com.voghan.pillar.core.models.cfm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.voghan.pillar.common.links.SimpleLinkBuilder;
import com.voghan.pillar.common.links.model.SimpleLink;
import com.voghan.pillar.core.testcontext.AppAemContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(AemContextExtension.class)
public class ArticleDetailCfmTest {

  private static final AemContext context = AppAemContext.newAemContext();

  private static final String ARTICLE_PATH = "/pillar-core/model/cfm/articleDetail.json";

  private static final String ARTICLE_FRAGMENT = "/content/dam/articles/downhill-skiing-wyoming";
  private static final String ARTICLE_URL = "/content/pillar/us/en/articles/downhill-skiing-wyoming.html";

  @BeforeAll
  static void setup() {
    context.addModelsForClasses(ArticleDetailCfm.class, SimpleLinkBuilder.class);
    context.load().json(ARTICLE_PATH, "/content/dam/articles");
  }

  @Test
  void getHeadline_returnsHeadline() {
    ArticleDetailCfm model = getModel(ARTICLE_FRAGMENT, "master");
    assertNotNull(model);
    assertEquals("Downhill Skiing Wyoming", model.getHeadline());
  }

  @Test
  void getSubheadline_returnsSubheadline() {
    ArticleDetailCfm model = getModel(ARTICLE_FRAGMENT, "master");
    assertNotNull(model);
    assertEquals("Jackson Hole Resort", model.getSubheadline());
  }

  @Test
  void getShortDescription_returnsDescription() {
    String expected = "<p>A skiers paradise far from crowds and close to nature with terrain so vast it appears uncharted. With 2,500 acres of "
        + "legendary terrain, unmatched levels of snowfall each winter, and unparalleled backcountry access, Jackson Hole offers a truly unique "
        + "skiing experience.</p>\n";
    ArticleDetailCfm model = getModel(ARTICLE_FRAGMENT, "master");
    assertNotNull(model);
    assertEquals(expected, model.getShortDescription());
  }

  @Test
  void getBannerImageUrl_returnsDamPath() {
    ArticleDetailCfm model = getModel(ARTICLE_FRAGMENT, "master");
    assertNotNull(model);
    assertEquals("/content/dam/pillar/images/en/cards/adobestock-185234795.jpeg", model.getBannerImageUrl());
  }

  @Test
  void getUrl_returnsArticleUrl() {
    ArticleDetailCfm model = getModel(ARTICLE_FRAGMENT, "master");
    assertNotNull(model);
    assertEquals(ARTICLE_URL, model.getUrl());
  }

  @Test
  void getContent_returnsContent() {
    ArticleDetailCfm model = getModel(ARTICLE_FRAGMENT, "master");
    assertNotNull(model);
    assertNotNull(model.getContent());
  }

  @Test
  void getPostDate_returnsFormattedDate() {
    ArticleDetailCfm model = getModel(ARTICLE_FRAGMENT, "master");
    assertNotNull(model);
    assertNotNull(model.getPostDate());
  }

  @Test
  void getCallToActions_withUrl_returnsSingleLink() {
    ArticleDetailCfm model = getModel(ARTICLE_FRAGMENT, "master");
    assertNotNull(model);
    assertFalse(model.getCallToActions().isEmpty());
    assertEquals(1, model.getCallToActions().size());
    SimpleLink link = model.getCallToActions().getFirst();
    assertEquals(ARTICLE_URL, link.getLinkPath());
  }

  @Test
  void isCallToActionEnabled_withUrl_returnsTrue() {
    ArticleDetailCfm model = getModel(ARTICLE_FRAGMENT, "master");
    assertNotNull(model);
    assertTrue(model.isCallToActionEnabled());
  }

  @Test
  void getVersion_returnsVariationName() {
    ArticleDetailCfm model = getModel(ARTICLE_FRAGMENT, "master");
    assertNotNull(model);
    assertEquals("master", model.getVersion());
  }

  private ArticleDetailCfm getModel(String path, String version) {
    Resource resource = context.currentResource(path + "/jcr:content/data/" + version);
    return resource != null ? resource.adaptTo(ArticleDetailCfm.class) : null;
  }
}
