package com.voghan.pillar.core.models.pages;

import com.voghan.pillar.core.testcontext.AppAemContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

@ExtendWith(AemContextExtension.class)
public class ArticlePageModelTest {

  private static final AemContext context = AppAemContext.newAemContext();

  private static final String CONTENT_PATH = "/content/pages/jcr:content";

  @BeforeAll
  static void setup() {
    context.addModelsForClasses(ArticlePageModel.class);
    context.load().json("/pillar-core/model/pages/article-pages.json", "/content/pages");
  }

  @BeforeEach
  void resetSelectors() {
    context.requestPathInfo().setSelectorString(null);
  }

  @Test
  void getExportedType_returnsResourceType() {
    ArticlePageModel model = getModel();
    assertNotNull(model);
    assertEquals(ArticlePageModel.RESOURCE_TYPE, model.getExportedType());
  }

  @Test
  void getSelectors_withSingleSelector_returnsSelector() {
    context.requestPathInfo().setSelectorString("downhill-skiing-wyoming");

    ArticlePageModel model = getModel();
    assertNotNull(model);
    assertEquals(1, model.getSelectors().length);
    assertEquals("downhill-skiing-wyoming", model.getSelectors()[0]);
  }

  @Test
  void getSelectors_withMultipleSelectors_returnsAll() {
    context.requestPathInfo().setSelectorString("downhill-skiing-wyoming.en");

    ArticlePageModel model = getModel();
    assertNotNull(model);
    assertEquals(2, model.getSelectors().length);
    assertEquals("downhill-skiing-wyoming", model.getSelectors()[0]);
    assertEquals("en", model.getSelectors()[1]);
  }

  @Test
  void getSelectors_withNoSelectors_returnsEmptyArray() {
    ArticlePageModel model = getModel();
    assertNotNull(model);
    assertEquals(0, model.getSelectors().length);
  }

  @Test
  void getTitle_withNoSeoData_returnsNull() {
    ArticlePageModel model = getModel();
    assertNotNull(model);
    assertNull(model.getTitle());
  }

  @Test
  void getDescription_withNoSeoData_returnsNull() {
    ArticlePageModel model = getModel();
    assertNotNull(model);
    assertNull(model.getDescription());
  }

  @Test
  void getCanonicalLink_withNoSeoData_returnsNull() {
    ArticlePageModel model = getModel();
    assertNotNull(model);
    assertNull(model.getCanonicalLink());
  }

  @Test
  void getRobotsTags_withNoSeoData_returnsEmptyList() {
    ArticlePageModel model = getModel();
    assertNotNull(model);
    assertTrue(model.getRobotsTags().isEmpty());
  }

  private ArticlePageModel getModel() {
    context.currentResource(CONTENT_PATH);
    return context.request().adaptTo(ArticlePageModel.class);
  }
}
