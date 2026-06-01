package com.voghan.pillar.core.models.cfm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.voghan.pillar.common.links.model.SimpleLink;
import com.voghan.pillar.core.testcontext.AppAemContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(AemContextExtension.class)
public class CardCfmTest {

  private static final AemContext context = AppAemContext.newAemContext();

  private static final String DEMO_LINKS_PATH = "/pillar-core/model/cfm/links.json";
  private static final String DEMO_CARD_PATH = "/pillar-core/model/cfm/simpleCards.json";

  CardCfm cardCfm;

  @BeforeAll
  static void setup() {
    // Load context content once
    context.addModelsForClasses(LinkCfm.class);
    context.load().json(DEMO_LINKS_PATH, "/content/dam/links");
    context.load().json(DEMO_CARD_PATH, "/content/dam/simple-cards");
  }

  @Test
  void getHeadline_default() {
    cardCfm = getComponent("/content/dam/simple-cards/option1", "master");

    assertNotNull(cardCfm);
    assertEquals("Epic Journey", cardCfm.getHeadline());
  }

  @Test
  void getHeadline_variationEs() {
    cardCfm = getComponent("/content/dam/simple-cards/option1", "es");

    assertNotNull(cardCfm);
    assertEquals("Viaje épico", cardCfm.getHeadline());
  }

  @Test
  void getShortDescription_default() {
    cardCfm = getComponent("/content/dam/simple-cards/option1", "master");
    String expected = "<p>Don't stop half way, go for the top!</p>\n";

    assertNotNull(cardCfm);
    assertEquals(expected, cardCfm.getShortDescription());
  }

  @Test
  void getCallToActions_default() {
    cardCfm = getComponent("/content/dam/simple-cards/option1", "master");

    assertNotNull(cardCfm);
    assertFalse(cardCfm.getCallToActions().isEmpty());
    assertEquals(1, cardCfm.getCallToActions().size());
    SimpleLink link = cardCfm.getCallToActions().get(0);
    assertNotNull(link);
    assertEquals("Get started", link.getLinkText());
    assertEquals("/content/pillar/us/en", link.getLinkPath());
  }

  @Test
  void getVersion_default() {
    cardCfm = getComponent("/content/dam/simple-cards/option1", "master");

    assertNotNull(cardCfm);
    assertEquals("master", cardCfm.getVersion());
  }

  @Test
  void getVersion_variationEs() {
    cardCfm = getComponent("/content/dam/simple-cards/option1", "es");

    assertNotNull(cardCfm);
    assertEquals("es", cardCfm.getVersion());
  }

  CardCfm getComponent(String path, String version) {
    Resource resource = context.currentResource(path + "/jcr:content/data/" + version);
    return resource != null ? resource.adaptTo(CardCfm.class) : null;
  }
}
