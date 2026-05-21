package com.voghan.pillar.core.models.pages;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import com.voghan.pillar.core.testcontext.AppAemContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(AemContextExtension.class)
public class CardPageModelTest {

  private static final AemContext context = AppAemContext.newAemContext();

  private static final String PAGES_PATH = "/pillar-core/model/pages/card-pages.json";

  @BeforeAll
  static void setup() {
    context.addModelsForClasses(CardPageModel.class);
    context.load().json(PAGES_PATH, "/content/pages");
  }

  @Test
  void getExportedType_withType_returnsResourceType() {
    CardPageModel model = getModel("/content/pages/jcr:content");
    assertNotNull(model);
    assertEquals(CardPageModel.RESOURCE_TYPE, model.getExportedType());
  }

  private CardPageModel getModel(String resourcePath) {
    context.currentResource(resourcePath);
    return context.request().adaptTo(CardPageModel.class);
  }
}
