package com.voghan.pillar.core.models.cfm;

import com.voghan.pillar.core.testcontext.AppAemContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@ExtendWith(AemContextExtension.class)
public class SeoDataCfmTest {

  private static final AemContext context = AppAemContext.newAemContext();

  private static final String SEO_DATA_PATH = "/pillar-core/model/cfm/seoData.json";

  @BeforeAll
  static void setup() {
    context.addModelsForClasses(SeoDataCfm.class);
    context.load().json(SEO_DATA_PATH, "/content/dam/seo");
  }

  @Test
  void getTitle_default() {
    SeoDataCfm model = getModel("/content/dam/seo/article-seo", "master");
    assertNotNull(model);
    assertEquals("Downhill Skiing Wyoming | Pillar", model.getTitle());
  }

  @Test
  void getDescription_default() {
    SeoDataCfm model = getModel("/content/dam/seo/article-seo", "master");
    assertNotNull(model);
    assertEquals("Jackson Hole offers a truly unique skiing experience with 2,500 acres of legendary terrain.", model.getDescription());
  }

  @Test
  void getCanonicalUrl_default() {
    SeoDataCfm model = getModel("/content/dam/seo/article-seo", "master");
    assertNotNull(model);
    assertEquals("/us/en/articles/downhill-skiing-wyoming", model.getCanonicalUrl());
  }

  @Test
  void getThumbnailUrl_default() {
    SeoDataCfm model = getModel("/content/dam/seo/article-seo", "master");
    assertNotNull(model);
    assertEquals("/content/dam/pillar/images/en/cards/adobestock-185234795.jpeg", model.getThumbnailUrl());
  }

  @Test
  void getRobots_withRobots_returnsDirectives() {
    SeoDataCfm model = getModel("/content/dam/seo/article-seo", "master");
    assertNotNull(model);
    assertFalse(model.getRobots().isEmpty());
    assertEquals(2, model.getRobots().size());
    assertTrue(model.getRobots().contains("index"));
    assertTrue(model.getRobots().contains("follow"));
  }

  @Test
  void getRobots_withoutRobots_returnsEmptyList() {
    SeoDataCfm model = getModel("/content/dam/seo/article-no-robots", "master");
    assertNotNull(model);
    assertNotNull(model.getRobots());
    assertTrue(model.getRobots().isEmpty());
  }

  @Test
  void getVersion_default() {
    SeoDataCfm model = getModel("/content/dam/seo/article-seo", "master");
    assertNotNull(model);
    assertEquals("master", model.getVersion());
  }

  private SeoDataCfm getModel(String path, String version) {
    Resource resource = context.currentResource(path + "/jcr:content/data/" + version);
    return resource != null ? resource.adaptTo(SeoDataCfm.class) : null;
  }
}
