package com.voghan.pillar.core.models.cfm;

import com.voghan.pillar.core.testcontext.AppAemContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@ExtendWith(AemContextExtension.class)
public class ArticleDetailCfmTest {

  private static final AemContext context = AppAemContext.newAemContext();

  private static final String ARTICLE_PATH = "/pillar-core/model/cfm/articleDetail.json";

  @BeforeAll
  static void setup() {
    context.addModelsForClasses(ArticleDetailCfm.class);
    context.load().json(ARTICLE_PATH, "/content/dam/articles");
  }

  @Test
  void getHeadline_default() {
    ArticleDetailCfm model = getModel("/content/dam/articles/downhill-skiing-wyoming", "master");
    assertNotNull(model);
    assertEquals("Downhill Skiing Wyoming", model.getHeadline());
  }

  @Test
  void getSubheadline_default() {
    ArticleDetailCfm model = getModel("/content/dam/articles/downhill-skiing-wyoming", "master");
    assertNotNull(model);
    assertEquals("Jackson Hole Resort", model.getSubheadline());
  }

  @Test
  void getDescription_default() {
    ArticleDetailCfm model = getModel("/content/dam/articles/downhill-skiing-wyoming", "master");
    assertNotNull(model);
    assertNotNull(model.getDescription());
    assertEquals("<p>A skiers paradise far from crowds and close to nature with terrain so vast it appears uncharted. With 2,500 acres of legendary terrain, unmatched levels of snowfall each winter, and unparalleled backcountry access, Jackson Hole offers a truly unique skiing experience.</p>\n", model.getDescription());
  }

  @Test
  void getBannerImageUrl_default() {
    ArticleDetailCfm model = getModel("/content/dam/articles/downhill-skiing-wyoming", "master");
    assertNotNull(model);
    assertEquals("/content/dam/pillar/images/en/cards/adobestock-185234795.jpeg", model.getBannerImageUrl());
  }

  @Test
  void getUrl_default() {
    ArticleDetailCfm model = getModel("/content/dam/articles/downhill-skiing-wyoming", "master");
    assertNotNull(model);
    assertEquals("/us/en/articles/downhill-skiing-wyoming", model.getUrl());
  }

  @Test
  void getContent_default() {
    ArticleDetailCfm model = getModel("/content/dam/articles/downhill-skiing-wyoming", "master");
    assertNotNull(model);
    assertNotNull(model.getContent());
  }

  @Test
  void getPostDate_default() {
    ArticleDetailCfm model = getModel("/content/dam/articles/downhill-skiing-wyoming", "master");
    assertNotNull(model);
    assertNotNull(model.getPostDate());
  }

  @Test
  void getVersion_default() {
    ArticleDetailCfm model = getModel("/content/dam/articles/downhill-skiing-wyoming", "master");
    assertNotNull(model);
    assertEquals("master", model.getVersion());
  }

  private ArticleDetailCfm getModel(String path, String version) {
    Resource resource = context.currentResource(path + "/jcr:content/data/" + version);
    return resource != null ? resource.adaptTo(ArticleDetailCfm.class) : null;
  }
}
