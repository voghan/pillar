package com.voghan.pillar.common.models.impl;

import com.voghan.pillar.common.models.PillarPageModel;
import com.voghan.pillar.common.testcontext.AppAemContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

@ExtendWith(AemContextExtension.class)
class SimplePageModelImplTest {

  private static final AemContext context = AppAemContext.newAemContext();

  private static final String PAGES_PATH = "/pillar-common/model/impl/simple-pages.json";

  @BeforeAll
  static void setup() {
    context.addModelsForClasses(SimplePageModelImpl.class);
    context.load().json(PAGES_PATH, "/content/pages");
  }

  @Test
  void getPageTitle_withTitle_returnsTitle() {
    PillarPageModel model = getModel("/content/pages/jcr:content");
    assertNotNull(model);
    assertEquals("Test Page Title", model.getPageTitle());
  }

  @Test
  void getPageDescription_withDescription_returnsDescription() {
    PillarPageModel model = getModel("/content/pages/jcr:content");
    assertNotNull(model);
    assertEquals("Test page description", model.getPageDescription());
  }

  @Test
  void getPageDescription_default_returnsFalse() {
    PillarPageModel model = getModel("/content/pages/jcr:content");
    assertNotNull(model);
    assertFalse(model.isHideInNav());
  }

  @Test
  void getPageDescription_withoutDescription_returnsNull() {
    PillarPageModel model = getModel("/content/pages/page_no_description/jcr:content");
    assertNotNull(model);
    assertNull(model.getPageDescription());
  }

  @Test
  void isHideInNav_withHideInNav_returnsTrue() {
    PillarPageModel model = getModel("/content/pages/page_hide_in_nav/jcr:content");
    assertNotNull(model);
    assertTrue(model.isHideInNav());
  }

  private PillarPageModel getModel(String resourcePath) {
    context.currentResource(resourcePath);
    return context.request().adaptTo(PillarPageModel.class);
  }
}
