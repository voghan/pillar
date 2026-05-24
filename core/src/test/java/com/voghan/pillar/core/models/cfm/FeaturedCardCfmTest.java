package com.voghan.pillar.core.models.cfm;

import com.voghan.pillar.core.models.Link;
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
public class FeaturedCardCfmTest {

  private static final AemContext context = AppAemContext.newAemContext();

  private static final String LINKS_PATH = "/pillar-core/model/cfm/links.json";
  private static final String FEATURED_CARDS_PATH = "/pillar-core/model/cfm/featuredCards.json";

  @BeforeAll
  static void setup() {
    context.addModelsForClasses(FeaturedCardCfm.class, LinkCfm.class);
    context.load().json(LINKS_PATH, "/content/dam/links");
    context.load().json(FEATURED_CARDS_PATH, "/content/dam/featured-cards");
  }

  @Test
  void getSubheadline_default() {
    FeaturedCardCfm model = getComponent("/content/dam/featured-cards/option1", "master");
    assertNotNull(model);
    assertEquals("The best adventure awaits", model.getSubheadline());
  }

  @Test
  void getImage_default() {
    FeaturedCardCfm model = getComponent("/content/dam/featured-cards/option1", "master");
    assertNotNull(model);
    assertEquals("/content/dam/images/featured.png", model.getImage());
  }

  @Test
  void getHeadline_default() {
    FeaturedCardCfm model = getComponent("/content/dam/featured-cards/option1", "master");
    assertNotNull(model);
    assertEquals("Featured Journey", model.getHeadline());
  }

  @Test
  void getShortDescription_default() {
    FeaturedCardCfm model = getComponent("/content/dam/featured-cards/option1", "master");
    assertNotNull(model);
    assertEquals("<p>Don't stop half way, go for the top!</p>\n", model.getShortDescription());
  }

  @Test
  void getCallToActions_default() {
    FeaturedCardCfm model = getComponent("/content/dam/featured-cards/option1", "master");
    assertNotNull(model);
    assertFalse(model.getCallToActions().isEmpty());
    assertEquals(1, model.getCallToActions().size());
    Link link = model.getCallToActions().getFirst();
    assertNotNull(link);
    assertEquals("Get started", link.getLinkText());
    assertEquals("/content/pillar/us/en", link.getLinkPath());
  }

  @Test
  void isCallToActionEnabled_withCallToActions_returnsTrue() {
    FeaturedCardCfm model = getComponent("/content/dam/featured-cards/option1", "master");
    assertNotNull(model);
    assertTrue(model.isCallToActionEnabled());
  }

  @Test
  void isCallToActionEnabled_withoutCallToActions_returnsFalse() {
    FeaturedCardCfm model = getComponent("/content/dam/featured-cards/option_no_cta", "master");
    assertNotNull(model);
    assertFalse(model.isCallToActionEnabled());
  }

  @Test
  void getVersion_default() {
    FeaturedCardCfm model = getComponent("/content/dam/featured-cards/option1", "master");
    assertNotNull(model);
    assertEquals("master", model.getVersion());
  }

  FeaturedCardCfm getComponent(String path, String version) {
    Resource resource = context.currentResource(path + "/jcr:content/data/" + version);
    return resource != null ? resource.adaptTo(FeaturedCardCfm.class) : null;
  }
}
