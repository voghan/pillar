package com.voghan.pillar.core.models.pages;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import com.voghan.pillar.core.testcontext.AppAemContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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

  @BeforeEach
  void setupMocks() {
    context.requestPathInfo().setSelectorString(null);
  }

  @Test
  void getExportedType_withType_returnsResourceType() {
    CardPageModel model = getModel("/content/pages/jcr:content");
    assertNotNull(model);
    assertEquals(CardPageModel.RESOURCE_TYPE, model.getExportedType());
  }

  @Test
  void getSelectors_withSelectors_returnsSelector() {
    String [] expected = new String[] {"abc","123"};
    context.requestPathInfo().setSelectorString("abc.123");

    CardPageModel model = getModel("/content/pages/jcr:content");
    assertNotNull(model);
    assertEquals(2, model.getSelectors().length);
    assertEquals(Arrays.stream(expected).findFirst(), Arrays.stream(model.getSelectors()).findFirst());
  }

  @Test
  void getSelectors_withNoSelectors_returnsNull() {
    CardPageModel model = getModel("/content/pages/jcr:content");
    assertNotNull(model);
    assertEquals(0, model.getSelectors().length);
  }

  private CardPageModel getModel(String resourcePath) {
    context.currentResource(resourcePath);
    return context.request().adaptTo(CardPageModel.class);
  }
}
