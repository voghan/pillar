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

@ExtendWith(AemContextExtension.class)
public class SimpleCardCfmTest {
    private static final AemContext context = AppAemContext.newAemContext();

    private static final String DEMO_LINKS_PATH = "/pillar-core/model/cfm/links.json";
    private static final String DEMO_CARD_PATH = "/pillar-core/model/cfm/simpleCards.json";

    SimpleCardCfm cardCfm;

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
        Link link = cardCfm.getCallToActions().get(0);
        assertNotNull(link);
        assertEquals("Get started", link.getLinkText());
        assertEquals("/content/pillar/us/en", link.getLinkPath());
    }

    @Test
    void getImage_default() {
        cardCfm = getComponent("/content/dam/simple-cards/option1", "master");

        assertNotNull(cardCfm);
        assertEquals("/content/dam/images/BrianLogo2019-medium.png", cardCfm.getImage());
    }

    SimpleCardCfm getComponent(String path, String version) {
        Resource resource = context.currentResource(path + "/jcr:content/data/" + version);
        return resource != null ? resource.adaptTo(SimpleCardCfm.class) : null;
    }
}
